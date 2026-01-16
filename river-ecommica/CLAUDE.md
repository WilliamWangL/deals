# CLAUDE.md - River Ecommica

This file provides guidance to Claude Code (claude.ai/code) when working with the river-ecommica deals aggregation site.

## 项目概述

River 优惠聚合站点 (deals.ecommica.com)，基于 Next.js 16 + React 19 + Tailwind CSS 4。

## 常用命令

```bash
# 本地开发
pnpm dev

# 构建
pnpm build

# 生产运行
pnpm start

# 代码检查
pnpm lint
```

## 项目架构

### 目录结构

```
src/
├── app/              # App Router
│   ├── [locale]/     # 国际化路由
│   │   ├── (routes)/ # 页面路由
│   │   └── api/      # API 路由
│   ├── globals.css   # 全局样式
│   ├── sitemap.ts    # 站点地图
│   └── robots.ts     # 爬虫配置
├── components/       # React 组件
│   ├── coupon/       # 优惠券组件
│   ├── deal/         # 交易组件
│   ├── layout/       # 布局组件
│   ├── seo/          # SEO 组件
│   ├── store/        # 店铺组件
│   └── ui/           # UI 基础组件
├── lib/              # 工具库
│   ├── api.ts        # API 客户端
│   ├── tracking.ts   # 跟踪工具
│   ├── utils.ts      # 通用工具
│   └── mock/         # Mock 数据
├── i18n/             # 国际化配置
├── messages/         # 翻译文件
├── config/           # 配置文件
├── types/            # TypeScript 类型
└── middleware.ts     # 中间件
```

### App Router 结构

```
app/
├── [locale]/
│   ├── page.tsx           # 首页
│   ├── coupons/           # 优惠券页面
│   ├── stores/            # 店铺页面
│   ├── categories/        # 分类页面
│   └── ...
└── api/                   # API 路由
```

### 组件结构

```
components/
├── ui/               # shadcn/ui 基础组件
├── layout/           # 页面布局组件
│   ├── Header.tsx
│   ├── Footer.tsx
│   └── Sidebar.tsx
├── deal/             # 交易相关组件
├── coupon/           # 优惠券相关组件
├── store/            # 店铺相关组件
└── seo/              # SEO 组件
```

## 开发规范

### 国际化 (i18n)

使用 `next-intl` 进行国际化：

```typescript
import { useTranslations } from 'next-intl'

const t = useTranslations('common')
```

翻译文件位于 `messages/` 目录。

### API 调用

使用 `src/lib/api.ts` 中的 API 客户端：

```typescript
import { fetchDeals, fetchCoupons } from '@/lib/api'
```

### 样式

- 使用 Tailwind CSS 4
- 使用 `class-variance-authority` (cva) 管理变体样式
- 使用 `clsx` 和 `tailwind-merge` 合并类名

### 路由

- 使用 App Router（文件系统路由）
- 国际化通过 `[locale]` 动态段实现
- 中间件处理语言检测和路由

## 配置文件

- `next.config.ts` - Next.js 配置
- `tailwind.config.ts` - Tailwind CSS 配置
- `tsconfig.json` - TypeScript 配置

## Superpowers 工作流

项目已安装 Superpowers 插件。可用命令：

```bash
/superpowers:brainstorm    # 交互式设计细化
/superpowers:write-plan    # 创建实施计划
/superpowers:execute-plan  # 批量执行计划
```

## 重要注意事项

1. **遵循现有模式** - 创建新页面/组件前先查看类似模块
2. **类型安全** - 充分利用 TypeScript
3. **组件复用** - 优先使用 `components/ui` 中的基础组件（shadcn/ui）
4. **SEO 优化** - 使用 Next.js Metadata API
5. **国际化** - 所有用户可见文本必须使用翻译
6. **未明确要求不得 commit** - 不要主动创建 Git 提交
