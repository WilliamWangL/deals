# GitHub Actions 部署实现计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 为 River 广告平台配置 GitHub Actions 自动化部署到腾讯云 CVM 服务器

**Architecture:** 使用 GitHub Actions 并行构建三个子项目的 Docker 镜像，推送到 ghcr.io，然后 SSH 到服务器执行 docker-compose 部署。Nginx 作为反向代理，根据路径分发请求到不同服务。

**Tech Stack:** GitHub Actions, Docker, Docker Compose, Nginx, ghcr.io

---

## Task 1: 配置 Next.js Standalone 输出

**Files:**
- Modify: `river-ecommica/next.config.ts:6-10`

**Step 1: 修改 next.config.ts 添加 standalone 输出**

在 `nextConfig` 对象中添加 `output: 'standalone'` 配置：

```typescript
const nextConfig: NextConfig = {
  output: 'standalone',
  turbopack: {
    root: __dirname,
  },
  // ... 其余配置保持不变
```

**Step 2: 验证配置生效**

Run: `cd river-ecommica && pnpm build`

Expected: 构建成功，且 `.next/standalone` 目录被创建

**Step 3: Commit**

```bash
git add river-ecommica/next.config.ts
git commit -m "$(cat <<'EOF'
build(ecommica): enable standalone output for Docker deployment

Required for optimized Docker image that only includes necessary
production dependencies.
EOF
)"
```

---

## Task 2: 配置 Vue Admin 构建路径

**Files:**
- Modify: `river-ui-admin/.env.prod:25`

**Step 1: 修改 VITE_BASE_PATH 为 /admin**

将 `VITE_BASE_PATH=/` 改为 `VITE_BASE_PATH=/admin`：

```env
# 打包路径
VITE_BASE_PATH=/admin
```

**Step 2: 验证配置**

Run: `cd river-ui-admin && pnpm build:prod`

Expected: 构建成功，生成的 `dist-prod/index.html` 中资源路径以 `/admin` 开头

**Step 3: Commit**

```bash
git add river-ui-admin/.env.prod
git commit -m "$(cat <<'EOF'
build(ui-admin): set base path to /admin for production

Admin UI will be served under /admin path behind Nginx.
EOF
)"
```

---

## Task 3: 创建 river-server 多阶段 Dockerfile

**Files:**
- Modify: `river-server/river-server/Dockerfile`

**Step 1: 替换为多阶段构建 Dockerfile**

```dockerfile
# 构建阶段
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests -pl river-server -am

# 运行阶段
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /build/river-server/target/river-server.jar app.jar

ENV TZ=Asia/Shanghai
ENV JAVA_OPTS="-Xms512m -Xmx512m -Djava.security.egd=file:/dev/./urandom"
ENV ARGS=""

EXPOSE 48080

CMD java ${JAVA_OPTS} -jar app.jar ${ARGS}
```

**Step 2: 验证 Dockerfile 语法**

Run: `cd river-server && docker build -f river-server/Dockerfile -t river-server:test . --dry-run` (如果 Docker 版本支持) 或直接构建测试

Expected: Dockerfile 语法正确

**Step 3: Commit**

```bash
git add river-server/river-server/Dockerfile
git commit -m "$(cat <<'EOF'
build(server): convert to multi-stage Dockerfile

Enables building from source in CI without pre-built JAR.
EOF
)"
```

---

## Task 4: 创建 river-ui-admin Dockerfile

**Files:**
- Create: `river-ui-admin/Dockerfile`

**Step 1: 创建 Dockerfile**

```dockerfile
# 构建阶段
FROM node:20-alpine AS builder
WORKDIR /app

# 安装 pnpm
RUN npm install -g pnpm

# 复制依赖文件
COPY package.json pnpm-lock.yaml ./

# 安装依赖
RUN pnpm install --frozen-lockfile

# 复制源代码
COPY . .

# 构建生产版本
RUN pnpm build:prod

# 运行阶段 - 使用 nginx 托管静态文件
FROM nginx:alpine
COPY --from=builder /app/dist-prod /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

**Step 2: 验证 Dockerfile**

Run: `cd river-ui-admin && docker build -t river-ui-admin:test .`

Expected: 构建成功

**Step 3: Commit**

```bash
git add river-ui-admin/Dockerfile
git commit -m "$(cat <<'EOF'
build(ui-admin): add Dockerfile for containerized deployment

Multi-stage build with pnpm, outputs static files to nginx.
EOF
)"
```

---

## Task 5: 创建 river-ecommica Dockerfile

**Files:**
- Create: `river-ecommica/Dockerfile`

**Step 1: 创建 Dockerfile**

```dockerfile
# 构建阶段
FROM node:20-alpine AS builder
WORKDIR /app

# 安装 pnpm
RUN npm install -g pnpm

# 复制依赖文件
COPY package.json pnpm-lock.yaml ./

# 安装依赖
RUN pnpm install --frozen-lockfile

# 复制源代码
COPY . .

# 构建生产版本
RUN pnpm build

# 运行阶段
FROM node:20-alpine
WORKDIR /app

# 从构建阶段复制 standalone 输出
COPY --from=builder /app/.next/standalone ./
COPY --from=builder /app/.next/static ./.next/static
COPY --from=builder /app/public ./public

ENV NODE_ENV=production
ENV PORT=3000

EXPOSE 3000

CMD ["node", "server.js"]
```

**Step 2: 验证 Dockerfile**

Run: `cd river-ecommica && docker build -t river-ecommica:test .`

Expected: 构建成功

**Step 3: Commit**

```bash
git add river-ecommica/Dockerfile
git commit -m "$(cat <<'EOF'
build(ecommica): add Dockerfile for containerized deployment

Uses Next.js standalone output for minimal image size.
EOF
)"
```

---

## Task 6: 创建 Nginx 配置

**Files:**
- Create: `docker/nginx/nginx.conf`

**Step 1: 创建目录结构**

Run: `mkdir -p docker/nginx`

**Step 2: 创建 nginx.conf**

```nginx
worker_processes auto;

events {
    worker_connections 1024;
}

http {
    include       /etc/nginx/mime.types;
    default_type  application/octet-stream;

    sendfile        on;
    keepalive_timeout  65;

    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml;

    upstream river-server {
        server river-server:48080;
    }

    upstream river-ecommica {
        server river-ecommica:3000;
    }

    server {
        listen 80;
        server_name _;

        # 管理后台静态文件 - /admin 路径
        location /admin {
            alias /usr/share/nginx/html;
            try_files $uri $uri/ /admin/index.html;
        }

        # 管理后台静态资源 - 需要单独处理带 /admin 前缀的资源
        location /admin/assets {
            alias /usr/share/nginx/html/assets;
        }

        # 后端 API - 管理后台
        location /admin-api/ {
            proxy_pass http://river-server;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }

        # 后端 API - 公开接口
        location /app-api/ {
            proxy_pass http://river-server;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }

        # 优惠站 - 默认路径 (Next.js)
        location / {
            proxy_pass http://river-ecommica;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
    }
}
```

**Step 3: Commit**

```bash
git add docker/nginx/nginx.conf
git commit -m "$(cat <<'EOF'
build(nginx): add production nginx configuration

Routes /admin to Vue admin, /admin-api and /app-api to Spring Boot,
/ to Next.js ecommica site.
EOF
)"
```

---

## Task 7: 创建生产 Docker Compose

**Files:**
- Create: `docker/docker-compose.prod.yml`

**Step 1: 创建 docker-compose.prod.yml**

```yaml
version: '3.8'

services:
  river-server:
    image: ghcr.io/${GITHUB_REPO:-river}/river-server:latest
    container_name: river-server
    restart: always
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - TZ=Asia/Shanghai
    networks:
      - river-network

  river-ecommica:
    image: ghcr.io/${GITHUB_REPO:-river}/river-ecommica:latest
    container_name: river-ecommica
    restart: always
    environment:
      - NODE_ENV=production
    networks:
      - river-network

  nginx:
    image: nginx:alpine
    container_name: river-nginx
    restart: always
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
      - ./admin-dist:/usr/share/nginx/html:ro
      - ./ssl:/etc/nginx/ssl:ro
    depends_on:
      - river-server
      - river-ecommica
    networks:
      - river-network

networks:
  river-network:
    driver: bridge
```

**Step 2: Commit**

```bash
git add docker/docker-compose.prod.yml
git commit -m "$(cat <<'EOF'
build(docker): add production docker-compose configuration

Orchestrates river-server, river-ecommica, and nginx services.
EOF
)"
```

---

## Task 8: 创建 GitHub Actions Workflow

**Files:**
- Create: `.github/workflows/deploy.yml`

**Step 1: 创建目录结构**

Run: `mkdir -p .github/workflows`

**Step 2: 创建 deploy.yml**

```yaml
name: Build and Deploy

on:
  push:
    branches: [main]
  workflow_dispatch:

env:
  REGISTRY: ghcr.io
  IMAGE_PREFIX: ghcr.io/${{ github.repository }}

jobs:
  build-server:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      packages: write

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Login to GitHub Container Registry
        uses: docker/login-action@v3
        with:
          registry: ${{ env.REGISTRY }}
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}

      - name: Build and push river-server
        uses: docker/build-push-action@v5
        with:
          context: ./river-server
          file: ./river-server/river-server/Dockerfile
          push: true
          tags: ${{ env.IMAGE_PREFIX }}/river-server:latest

  build-ui-admin:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      packages: write

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Login to GitHub Container Registry
        uses: docker/login-action@v3
        with:
          registry: ${{ env.REGISTRY }}
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}

      - name: Build and push river-ui-admin
        uses: docker/build-push-action@v5
        with:
          context: ./river-ui-admin
          file: ./river-ui-admin/Dockerfile
          push: true
          tags: ${{ env.IMAGE_PREFIX }}/river-ui-admin:latest

  build-ecommica:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      packages: write

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Login to GitHub Container Registry
        uses: docker/login-action@v3
        with:
          registry: ${{ env.REGISTRY }}
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}

      - name: Build and push river-ecommica
        uses: docker/build-push-action@v5
        with:
          context: ./river-ecommica
          file: ./river-ecommica/Dockerfile
          push: true
          tags: ${{ env.IMAGE_PREFIX }}/river-ecommica:latest

  deploy:
    needs: [build-server, build-ui-admin, build-ecommica]
    runs-on: ubuntu-latest

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Copy files to server
        uses: appleboy/scp-action@v0.1.7
        with:
          host: ${{ secrets.SERVER_HOST }}
          username: ${{ secrets.SERVER_USER }}
          key: ${{ secrets.SERVER_SSH_KEY }}
          port: ${{ secrets.SERVER_PORT }}
          source: "docker/docker-compose.prod.yml,docker/nginx/nginx.conf"
          target: "/opt/river"
          strip_components: 1

      - name: Extract admin static files
        uses: appleboy/ssh-action@v1
        with:
          host: ${{ secrets.SERVER_HOST }}
          username: ${{ secrets.SERVER_USER }}
          key: ${{ secrets.SERVER_SSH_KEY }}
          port: ${{ secrets.SERVER_PORT }}
          script: |
            cd /opt/river

            # 登录 ghcr.io
            echo ${{ secrets.GITHUB_TOKEN }} | docker login ghcr.io -u ${{ github.actor }} --password-stdin

            # 提取 admin 静态文件
            docker pull ${{ env.IMAGE_PREFIX }}/river-ui-admin:latest
            docker create --name temp-admin ${{ env.IMAGE_PREFIX }}/river-ui-admin:latest
            rm -rf /opt/river/admin-dist
            docker cp temp-admin:/usr/share/nginx/html /opt/river/admin-dist
            docker rm temp-admin

      - name: Deploy to server
        uses: appleboy/ssh-action@v1
        with:
          host: ${{ secrets.SERVER_HOST }}
          username: ${{ secrets.SERVER_USER }}
          key: ${{ secrets.SERVER_SSH_KEY }}
          port: ${{ secrets.SERVER_PORT }}
          script: |
            cd /opt/river

            # 设置环境变量
            export GITHUB_REPO=${{ github.repository }}

            # 拉取最新镜像并重启服务
            docker-compose -f docker-compose.prod.yml pull
            docker-compose -f docker-compose.prod.yml up -d

            # 清理旧镜像
            docker image prune -f
```

**Step 3: Commit**

```bash
git add .github/workflows/deploy.yml
git commit -m "$(cat <<'EOF'
ci: add GitHub Actions deployment workflow

Parallel build of all three services, push to ghcr.io,
and deploy to server via SSH.
EOF
)"
```

---

## Task 9: 创建 .dockerignore 文件

**Files:**
- Create: `river-server/.dockerignore`
- Create: `river-ui-admin/.dockerignore`
- Create: `river-ecommica/.dockerignore`

**Step 1: 创建 river-server/.dockerignore**

```
# IDE
.idea/
*.iml

# Build output
**/target/

# Git
.git/
.gitignore

# Docs
*.md
docs/

# Logs
logs/
*.log
```

**Step 2: 创建 river-ui-admin/.dockerignore**

```
# Dependencies
node_modules/

# Build output
dist/
dist-*/

# IDE
.idea/
.vscode/

# Git
.git/
.gitignore

# Logs
*.log

# Misc
*.md
```

**Step 3: 创建 river-ecommica/.dockerignore**

```
# Dependencies
node_modules/

# Build output
.next/
out/

# IDE
.idea/
.vscode/

# Git
.git/
.gitignore

# Logs
*.log

# Test
playwright-report/
test-results/

# Misc
*.md
```

**Step 4: Commit**

```bash
git add river-server/.dockerignore river-ui-admin/.dockerignore river-ecommica/.dockerignore
git commit -m "$(cat <<'EOF'
build: add .dockerignore files for all projects

Excludes unnecessary files from Docker build context for faster builds.
EOF
)"
```

---

## Task 10: 配置 GitHub Secrets (手动)

**此任务需要手动在 GitHub 仓库设置中完成。**

**Step 1: 进入 GitHub 仓库设置**

Navigate to: `https://github.com/{owner}/{repo}/settings/secrets/actions`

**Step 2: 添加以下 Secrets**

| Secret 名称 | 说明 | 示例值 |
|-------------|------|--------|
| `SERVER_HOST` | 服务器 IP 地址 | `1.2.3.4` |
| `SERVER_USER` | SSH 用户名 | `root` |
| `SERVER_SSH_KEY` | SSH 私钥内容 | `-----BEGIN OPENSSH PRIVATE KEY-----...` |
| `SERVER_PORT` | SSH 端口 | `22` |

**Step 3: 验证 Secrets 配置**

手动触发一次 workflow 测试部署流程。

---

## Task 11: 服务器初始化 (手动)

**此任务需要在目标服务器上手动执行。**

**Step 1: 安装 Docker**

```bash
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker $USER
# 重新登录使 docker 组生效
```

**Step 2: 安装 Docker Compose**

```bash
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

**Step 3: 创建部署目录**

```bash
mkdir -p /opt/river/{nginx,ssl,admin-dist}
```

**Step 4: 配置 SSL 证书 (可选)**

```bash
# 将 SSL 证书文件放到 /opt/river/ssl/ 目录
# 并相应修改 nginx.conf 添加 HTTPS 配置
```

---

## 验收标准

1. **代码提交检查**
   - [ ] 所有 9 个 commit 已创建
   - [ ] 每个 commit 仅包含相关文件

2. **本地构建验证**
   - [ ] `river-ecommica` 构建生成 `.next/standalone`
   - [ ] `river-ui-admin` 构建输出路径包含 `/admin`
   - [ ] 所有 Dockerfile 可正常构建

3. **GitHub Actions 验证**
   - [ ] 推送到 main 分支自动触发 workflow
   - [ ] 三个镜像并行构建成功
   - [ ] 镜像推送到 ghcr.io 成功
   - [ ] SSH 部署步骤执行成功

4. **生产环境验证**
   - [ ] `http://{server}/admin` 访问管理后台
   - [ ] `http://{server}/admin-api/` 访问后端 API
   - [ ] `http://{server}/` 访问优惠站
