# GitHub Actions 部署设计

## 概述

为 River 广告平台的三个子项目配置 GitHub Actions 自动化部署流程。

## 需求

- **项目**: river-server (Java)、river-ui-admin (Vue)、river-ecommica (Next.js)
- **目标环境**: 腾讯云 CVM（美国地域）单台服务器
- **运行方式**: Docker Compose
- **触发条件**: 推送到 main 分支自动部署 + 手动触发
- **环境**: 仅生产环境
- **构建策略**: 全部部署（任何变更都重新部署三个项目）
- **镜像仓库**: GitHub Container Registry (ghcr.io)

## 整体架构

```
┌─────────────────────────────────────────────────────────────┐
│                     GitHub Actions                          │
├─────────────────────────────────────────────────────────────┤
│  触发条件：push to main / 手动触发                           │
│                                                             │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │river-server │ │river-ui-    │ │river-       │  并行构建  │
│  │   (Java)    │ │   admin     │ │  ecommica   │           │
│  └──────┬──────┘ └──────┬──────┘ └──────┬──────┘           │
│         │               │               │                   │
│         └───────────────┼───────────────┘                   │
│                         ▼                                   │
│              推送镜像到 ghcr.io                              │
│                         │                                   │
│                         ▼                                   │
│              SSH 到服务器执行部署                            │
└─────────────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                腾讯云 CVM (美国)                              │
├─────────────────────────────────────────────────────────────┤
│  docker-compose pull && docker-compose up -d                │
│                                                             │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │river-server │ │   nginx     │ │river-       │           │
│  │   :48080    │ │    :80      │ │ecommica:3000│           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
└─────────────────────────────────────────────────────────────┘
```

**路由规则**:
| 路径 | 目标 |
|------|------|
| `/admin/*` | 管理后台静态文件 |
| `/admin-api/*` | river-server |
| `/app-api/*` | river-server |
| `/*` | river-ecommica |

## 文件结构

```
river-ad-workspace/
├── .github/
│   └── workflows/
│       └── deploy.yml              # 主部署 workflow
├── docker/
│   ├── docker-compose.prod.yml     # 生产环境编排文件
│   └── nginx/
│       └── nginx.conf              # Nginx 配置
├── river-server/
│   └── river-server/
│       └── Dockerfile              # 多阶段构建
├── river-ui-admin/
│   └── Dockerfile                  # 新建
└── river-ecommica/
    └── Dockerfile                  # 新建
```

## GitHub Actions Workflow

```yaml
# .github/workflows/deploy.yml

name: Build and Deploy

on:
  push:
    branches: [main]
  workflow_dispatch:

jobs:
  build:
    runs-on: ubuntu-latest
    strategy:
      matrix:
        include:
          - name: river-server
            context: ./river-server
            dockerfile: ./river-server/river-server/Dockerfile
          - name: river-ui-admin
            context: ./river-ui-admin
            dockerfile: ./river-ui-admin/Dockerfile
          - name: river-ecommica
            context: ./river-ecommica
            dockerfile: ./river-ecommica/Dockerfile

    permissions:
      contents: read
      packages: write

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Login to ghcr.io
        uses: docker/login-action@v3
        with:
          registry: ghcr.io
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}

      - name: Build and push
        uses: docker/build-push-action@v5
        with:
          context: ${{ matrix.context }}
          file: ${{ matrix.dockerfile }}
          push: true
          tags: ghcr.io/${{ github.repository }}/${{ matrix.name }}:latest

  deploy:
    needs: build
    runs-on: ubuntu-latest

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Deploy to server
        uses: appleboy/ssh-action@v1
        with:
          host: ${{ secrets.SERVER_HOST }}
          username: ${{ secrets.SERVER_USER }}
          key: ${{ secrets.SERVER_SSH_KEY }}
          port: ${{ secrets.SERVER_PORT }}
          script: |
            cd /opt/river
            echo ${{ secrets.GITHUB_TOKEN }} | docker login ghcr.io -u ${{ github.actor }} --password-stdin
            docker-compose -f docker-compose.prod.yml pull
            docker-compose -f docker-compose.prod.yml up -d
            docker image prune -f
```

## Dockerfile 设计

### river-server

```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests -pl river-server -am

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /build/river-server/target/river-server.jar app.jar
ENV TZ=Asia/Shanghai
EXPOSE 48080
CMD ["java", "-Xms512m", "-Xmx512m", "-jar", "app.jar"]
```

### river-ui-admin

```dockerfile
FROM node:20-alpine AS builder
WORKDIR /app
RUN npm install -g pnpm
COPY package.json pnpm-lock.yaml ./
RUN pnpm install --frozen-lockfile
COPY . .
RUN pnpm build:prod

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
```

### river-ecommica

```dockerfile
FROM node:20-alpine AS builder
WORKDIR /app
RUN npm install -g pnpm
COPY package.json pnpm-lock.yaml ./
RUN pnpm install --frozen-lockfile
COPY . .
RUN pnpm build

FROM node:20-alpine
WORKDIR /app
COPY --from=builder /app/.next/standalone ./
COPY --from=builder /app/.next/static ./.next/static
COPY --from=builder /app/public ./public
EXPOSE 3000
CMD ["node", "server.js"]
```

> 需要在 `next.config.ts` 中添加 `output: 'standalone'`

## Docker Compose

```yaml
# docker/docker-compose.prod.yml

version: '3.8'

services:
  river-server:
    image: ghcr.io/${GITHUB_REPO}/river-server:latest
    container_name: river-server
    restart: always
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - TZ=Asia/Shanghai
    ports:
      - "48080:48080"
    networks:
      - river-network

  river-ecommica:
    image: ghcr.io/${GITHUB_REPO}/river-ecommica:latest
    container_name: river-ecommica
    restart: always
    environment:
      - NODE_ENV=production
    ports:
      - "3000:3000"
    networks:
      - river-network

  nginx:
    image: ghcr.io/${GITHUB_REPO}/river-ui-admin:latest
    container_name: river-nginx
    restart: always
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
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

## Nginx 配置

```nginx
# docker/nginx/nginx.conf

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

    server {
        listen 80;
        server_name deals.ecommica.com;

        # 管理后台 - /admin 路径
        location /admin {
            alias /usr/share/nginx/html;
            try_files $uri $uri/ /admin/index.html;
        }

        # 后端 API - 管理后台
        location /admin-api/ {
            proxy_pass http://river-server:48080;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }

        # 后端 API - 公开接口
        location /app-api/ {
            proxy_pass http://river-server:48080;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }

        # 优惠站 - 默认路径
        location / {
            proxy_pass http://river-ecommica:3000;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
    }
}
```

## GitHub Secrets 配置

| Secret 名称 | 说明 |
|-------------|------|
| `SERVER_HOST` | 服务器 IP 地址 |
| `SERVER_USER` | SSH 用户名 |
| `SERVER_SSH_KEY` | SSH 私钥 |
| `SERVER_PORT` | SSH 端口（默认 22） |

> `GITHUB_TOKEN` 由 GitHub Actions 自动提供，无需手动配置

## 服务器准备

```bash
# 1. 安装 Docker 和 Docker Compose
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker $USER

# 2. 创建部署目录
mkdir -p /opt/river/{nginx,ssl}

# 3. 首次登录 ghcr.io
echo $GITHUB_TOKEN | docker login ghcr.io -u USERNAME --password-stdin
```

## 实现任务清单

| 序号 | 任务 | 文件 |
|------|------|------|
| 1 | 创建 GitHub Actions workflow | `.github/workflows/deploy.yml` |
| 2 | 修改 river-server Dockerfile | `river-server/river-server/Dockerfile` |
| 3 | 创建 river-ui-admin Dockerfile | `river-ui-admin/Dockerfile` |
| 4 | 创建 river-ecommica Dockerfile | `river-ecommica/Dockerfile` |
| 5 | 配置 Next.js standalone 输出 | `river-ecommica/next.config.ts` |
| 6 | 创建生产 docker-compose | `docker/docker-compose.prod.yml` |
| 7 | 创建 Nginx 配置 | `docker/nginx/nginx.conf` |
| 8 | 配置 river-ui-admin 构建路径 | 调整 `base` 为 `/admin` |
