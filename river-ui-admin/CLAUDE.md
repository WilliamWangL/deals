# CLAUDE.md - River UI Admin

This file provides guidance to Claude Code (claude.ai/code) when working with the river-ui-admin management frontend.

## 项目概述

River 广告平台管理后台，基于 Vue 3 + Element Plus + TypeScript + Vite 5。

## 常用命令

```bash
# 安装依赖（使用 pnpm）
pnpm install

# 本地开发
pnpm dev          # 连接本地后端 (env.local)
pnpm dev-server   # 连接开发服务器

# 类型检查
pnpm ts:check

# 构建
pnpm build:local  # 本地环境
pnpm build:dev    # 开发环境
pnpm build:test   # 测试环境
pnpm build:stage  # 预发布环境
pnpm build:prod   # 生产环境

# 代码检查和格式化
pnpm lint:eslint
pnpm lint:format
pnpm lint:style
```

## 项目架构

### 目录结构

```
src/
├── api/              # API 调用模块（按业务模块组织）
│   ├── river/        # River 业务模块 API
│   │   ├── affiliate/
│   │   ├── campaign/
│   │   ├── coupon/
│   │   ├── blog/
│   │   ├── deal/
│   │   ├── merchant/
│   │   ├── offer/
│   │   └── stats/
│   ├── system/       # 系统模块 API
│   ├── infra/        # 基础设施 API
│   └── ...
├── views/            # 页面组件
│   └── river/        # River 业务模块页面
├── components/       # 公共组件
├── router/           # 路由配置
│   └── modules/      # 路由模块
├── store/            # Pinia 状态管理
│   └── modules/      # 状态模块
│       ├── app.ts
│       ├── user.ts
│       ├── permission.ts
│       ├── tagsView.ts
│       ├── dict.ts
│       └── locale.ts
├── utils/            # 工具函数
├── types/            # TypeScript 类型定义
├── styles/           # 全局样式
├── directives/       # Vue 指令
├── hooks/            # 组合式函数
├── layout/           # 布局组件
├── plugins/          # 插件配置
├── config/           # 配置文件
├── locales/          # 国际化文件
├── assets/           # 静态资源
├── App.vue           # 根组件
├── main.ts           # 入口文件
└── permission.ts     # 权限控制
```

### API 模块结构

每个业务模块的 API 按功能组织：

```
src/api/river/{module}/
├── index.ts          # 主要 CRUD 接口
├── types.ts          # TypeScript 类型定义
└── ...               # 其他特定接口
```

### 页面组件结构

```
src/views/river/{module}/
├── index.vue         # 列表页面
└── components/       # 模块专用组件
```

## 开发规范

### 组件命名

- 页面组件：PascalCase（如 `CampaignList.vue`）
- 工具组件：PascalCase（如 `StatusTag.vue`）

### API 调用

使用 `src/utils/http/axios` 封装的 axios 实例：

```typescript
import { getCouponList, createCoupon } from '@/api/river/coupon'

// 列表查询
const { data } = await getCouponList({ pageNo: 1, pageSize: 10 })

// 创建
await createCoupon(data)
```

### 状态管理

使用 Pinia store：

```typescript
import { useUserStore } from '@/store/modules/user'

const userStore = useUserStore()
```

### 路由

路由配置位于 `src/router/modules/`，动态路由由后端返回。

## 配置文件

- `.env.local` - 本地开发环境
- `.env.dev` - 开发服务器环境
- `.env.test` - 测试环境
- `.env.stage` - 预发布环境
- `.env.prod` - 生产环境

## 样式规范

- 使用 UnoCSS + Tailwind CSS
- SCSS 变量定义在 `src/styles/variables.scss`
- 全局样式在 `src/styles/index.scss`

## Superpowers 工作流

项目已安装 Superpowers 插件。可用命令：

```bash
/superpowers:brainstorm    # 交互式设计细化
/superpowers:write-plan    # 创建实施计划
/superpowers:execute-plan  # 批量执行计划
```

## 重要注意事项

1. **遵循现有模式** - 创建新页面/组件前先查看类似模块
2. **类型安全** - 充分利用 TypeScript，使用 `types` 目录定义类型
3. **组件复用** - 优先使用 `components` 中的公共组件
4. **API 路径** - 前端 API 路径与后端 Controller 路径保持一致
5. **未明确要求不得 commit** - 不要主动创建 Git 提交
