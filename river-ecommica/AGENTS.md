# 优惠聚合站点 - React 18 + Next.js 15

Ecommica 优惠聚合站点 (deals.ecommica.com)

## 项目概述

展示品牌优惠券、折扣码的聚合站点：
- 多语言支持 (EN/中文)
- SEO 优化，静态生成 + ISR
- 联盟追踪链接
- 响应式设计

## 构建命令

```bash
# 安装依赖
pnpm install

# 开发服务器
pnpm dev

# 生产构建
pnpm build

# 启动生产服务
pnpm start

# 代码检查
pnpm lint
```

## 技术栈

- Next.js 15 (App Router + SSG/ISR)
- React 18 + TypeScript
- Tailwind CSS
- shadcn/ui 组件库
- next-intl（国际化）

## 目录结构

```
app/
  [locale]/               # 国际化路由
    page.tsx              # 首页
    deals/
      page.tsx            # 优惠列表
    brands/
      page.tsx            # 品牌列表
      [slug]/
        page.tsx          # 品牌详情
    about/
      page.tsx
components/
  ui/                     # shadcn 基础组件
  deals/                  # 优惠相关组件
    DealCard.tsx
    DealList.tsx
  brands/                 # 品牌相关组件
    BrandCard.tsx
    BrandGrid.tsx
  layout/                 # 布局组件
    Header.tsx
    Footer.tsx
lib/
  api.ts                  # 调用 river-server API
  tracking.ts             # 联盟链接追踪
  utils.ts                # 工具函数
hooks/
  useDeal.ts
  useBrand.ts
types/
  deal.ts
  brand.ts
messages/                 # i18n 翻译文件
  en.json
  zh.json
```

## 命名规范

| 类型 | 规范 | 示例 |
|------|------|------|
| 组件 | PascalCase | `DealCard.tsx` |
| hooks | camelCase + use 前缀 | `useDeal.ts` |
| 工具函数 | camelCase | `formatPrice.ts` |
| 类型 | PascalCase | `Deal.ts` |
| 页面 | 小写 + 目录 | `app/[locale]/deals/page.tsx` |

## 页面渲染策略

| 页面 | 渲染方式 | revalidate | 说明 |
|------|----------|------------|------|
| 首页 | ISR | 3600 (1h) | 热门优惠动态更新 |
| 品牌列表 | SSG | - | 静态生成 |
| 品牌详情 | ISR | 1800 (30min) | 优惠频繁更新 |
| 优惠列表 | ISR | 3600 (1h) | 分页 + 筛选 |
| 静态页面 | SSG | - | About, Terms 等 |

## 组件结构

```tsx
'use client' // 仅客户端组件需要

import { useState } from 'react'
import { Button } from '@/components/ui/button'

interface DealCardProps {
  deal: Deal
}

export function DealCard({ deal }: DealCardProps) {
  // hooks
  // state
  // handlers
  // render
}
```

## API 调用

```tsx
// Server Components - 直接 fetch
async function DealsPage() {
  const deals = await fetch(`${API_URL}/app-api/affiliate/deal/list`, {
    next: { revalidate: 3600 }
  }).then(res => res.json())
  
  return <DealList deals={deals.data} />
}

// Client Components - 使用 SWR 或 React Query
'use client'
function DealFilter() {
  const { data } = useSWR('/app-api/affiliate/deal/list', fetcher)
}
```

## SEO 要求

- 每个页面必须导出 `generateMetadata`
- 使用 `generateStaticParams` 预生成动态路由
- 添加结构化数据 (JSON-LD)

```tsx
export async function generateMetadata({ params }: Props): Promise<Metadata> {
  return {
    title: 'Page Title',
    description: 'Page description',
  }
}
```

## 国际化

使用 next-intl，翻译文件放在 `messages/` 目录：

```tsx
import { useTranslations } from 'next-intl'

export function Component() {
  const t = useTranslations('deals')
  return <h1>{t('title')}</h1>
}
```

## 重要约束

- 禁止使用 `as any` 或 `@ts-ignore`
- 优先使用 Server Components
- 图片必须使用 `next/image`
- 链接必须使用 `next/link`
- 遵循 Tailwind CSS 类名顺序
