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
