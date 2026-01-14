# 优惠聚合站点 - React 19 + Next.js 16

Ecommica 优惠聚合站点 (deals.ecommica.com)

## 项目概述

展示品牌优惠券、折扣码的聚合站点：
- 多语言支持 (EN/中文)
- SEO 优化，静态生成 + ISR
- 联盟追踪链接
- 响应式设计
- **Nocturnal Tech 设计主题** - 深色科技风格

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

- Next.js 16 (App Router + SSG/ISR)
- React 19 + TypeScript
- Tailwind CSS 4
- shadcn/ui 组件库
- next-intl 4.7（国际化）
- lucide-react（图标）

## 目录结构

```
src/
├── app/
│   ├── [locale]/               # 国际化路由
│   │   ├── page.tsx            # 首页
│   │   ├── deals/
│   │   │   ├── page.tsx        # 优惠列表
│   │   │   └── [slug]/page.tsx # 优惠详情
│   │   ├── stores/
│   │   │   ├── page.tsx        # 商家列表
│   │   │   └── [slug]/page.tsx # 商家详情
│   │   ├── coupons/
│   │   │   └── page.tsx        # 优惠券列表
│   │   ├── blog/
│   │   │   ├── page.tsx        # 博客列表
│   │   │   └── [slug]/page.tsx # 博客详情
│   │   └── layout.tsx          # 布局
│   ├── sitemap.ts              # 动态站点地图
│   └── robots.ts               # robots.txt
├── components/
│   ├── ui/                     # shadcn 基础组件
│   ├── deal/                   # 优惠相关组件
│   │   └── DealCard.tsx
│   ├── store/                  # 商家相关组件
│   │   └── StoreCard.tsx
│   ├── coupon/                 # 优惠券相关组件
│   │   ├── CouponCard.tsx
│   │   └── CouponsToolbar.tsx
│   ├── layout/                 # 布局组件
│   │   ├── Header.tsx
│   │   └── Footer.tsx
│   └── seo/                    # SEO 组件
│       └── JsonLd.tsx
├── lib/
│   ├── api.ts                  # API 调用 (支持 mock 降级)
│   ├── mock/                   # Mock 数据
│   │   ├── index.ts
│   │   ├── deals.ts
│   │   ├── stores.ts
│   │   ├── coupons.ts
│   │   └── categories.ts
│   └── utils.ts                # 工具函数
├── types/
│   └── index.ts                # 类型定义
└── messages/                   # i18n 翻译文件
    ├── en.json
    └── zh.json
```

## 设计规范

### 主题风格 - Nocturnal Tech

项目采用 **深色科技主题**，避免常见的 "AI slop" 设计：

| 设计元素 | 规范 |
|----------|------|
| 主色调 | Midnight Ocean (slate-900, blue-950, cyan-900) |
| 强调色 | Cyan-500, Indigo-500, Emerald-500 |
| 背景 | 深色渐变 + 动态光晕效果 |
| 卡片 | 玻璃拟态 (glassmorphism) + 微妙阴影 |
| 交互 | 悬停提升 + 渐变边框 + 流畅过渡 |

### 禁止的设计模式

- ❌ 紫色渐变白底 (purple gradients on white)
- ❌ Inter/Roboto/Arial 字体
- ❌ 过度圆角统一化
- ❌ 居中布局泛滥
- ❌ 无特色的通用组件

### 组件设计原则

```tsx
// 卡片悬停效果
className="transition-all duration-300 hover:-translate-y-1 hover:shadow-xl"

// 玻璃拟态
className="bg-slate-900/50 backdrop-blur-md border border-slate-700/50"

// 渐变文字
className="bg-gradient-to-r from-cyan-400 to-blue-400 bg-clip-text text-transparent"

// 动态光晕
className="absolute w-[500px] h-[500px] rounded-full bg-cyan-500/20 blur-[100px] animate-pulse"
```

## 命名规范

| 类型 | 规范 | 示例 |
|------|------|------|
| 组件 | PascalCase | `DealCard.tsx` |
| hooks | camelCase + use 前缀 | `useDeal.ts` |
| 工具函数 | camelCase | `formatPrice.ts` |
| 类型 | PascalCase | `Deal` |
| 页面 | 小写 + 目录 | `app/[locale]/deals/page.tsx` |

## 页面渲染策略

| 页面 | 渲染方式 | revalidate | 说明 |
|------|----------|------------|------|
| 首页 | ISR | 300 (5min) | 热门优惠动态更新 |
| 商家列表 | ISR | 3600 (1h) | 商家信息 |
| 商家详情 | ISR | 3600 (1h) | 商家优惠/优惠券 |
| 优惠列表 | ISR | 300 (5min) | 分页 + 筛选 |
| 优惠券列表 | ISR | 300 (5min) | 优惠码 |
| 博客 | ISR | 300 (5min) | 内容更新 |

## 组件结构

```tsx
'use client' // 仅客户端组件需要

import { useState } from 'react'
import { Button } from '@/components/ui/button'
import { Deal } from '@/types'

interface DealCardProps {
  deal: Deal
}

export function DealCard({ deal }: DealCardProps) {
  // 1. hooks
  // 2. state
  // 3. derived values
  // 4. handlers
  // 5. render
}
```

## API 调用

项目支持 Mock 数据降级，API 未连接时自动使用本地数据：

```tsx
// lib/api.ts 模式
const USE_MOCK = process.env.NEXT_PUBLIC_USE_MOCK === 'true' || !process.env.NEXT_PUBLIC_API_URL

// Server Components - 直接 fetch
async function DealsPage() {
  const deals = await fetchDeals({ featured: true })
  return <DealList deals={deals} />
}
```

### API 端点对应

| 前端函数 | 后端 API | Mock 数据 |
|----------|----------|-----------|
| fetchDeals | `/app-api/coupon/deal/list` | mockDeals |
| fetchDealBySlug | `/app-api/coupon/deal/get-by-slug` | mockDeals |
| fetchStores | `/app-api/affiliate/merchant/list` | mockStores |
| fetchStoreBySlug | `/app-api/affiliate/merchant/get-by-slug` | mockStores |
| fetchCoupons | `/app-api/coupon/coupon/list` | mockCoupons |
| fetchCategories | `/app-api/coupon/category/list` | mockCategories |
| fetchPosts | `/app-api/blog/post/list` | MOCK_POSTS |

## SEO 要求

### 必须实现

- ✅ 每个页面导出 `generateMetadata`
- ✅ 动态路由使用 `generateStaticParams`
- ✅ JSON-LD 结构化数据 (`src/components/seo/JsonLd.tsx`)
- ✅ 动态 sitemap (`src/app/sitemap.ts`)
- ✅ robots.txt (`src/app/robots.ts`)

```tsx
// 页面 metadata
export async function generateMetadata({ params }: Props): Promise<Metadata> {
  const { locale } = await params
  const t = await getTranslations({ locale, namespace: 'deals' })
  return {
    title: t('meta.title'),
    description: t('meta.description'),
  }
}

// JSON-LD
import { generateDealJsonLd } from '@/components/seo/JsonLd'

export default async function DealPage({ params }) {
  const deal = await fetchDealBySlug(params.slug)
  return (
    <>
      <script
        type="application/ld+json"
        dangerouslySetInnerHTML={{ __html: JSON.stringify(generateDealJsonLd(deal)) }}
      />
      <DealDetail deal={deal} />
    </>
  )
}
```

### JSON-LD 类型支持

- `generateDealJsonLd` - Product + Offer schema
- `generateStoreJsonLd` - Organization schema
- `generateCouponJsonLd` - Offer schema
- `generateBlogPostJsonLd` - Article schema
- `generateBreadcrumbJsonLd` - BreadcrumbList schema

## 国际化

使用 next-intl 4.7，翻译文件放在 `src/messages/` 目录：

```tsx
// Server Components
import { getTranslations } from 'next-intl/server'

export default async function Page({ params }: { params: Promise<{ locale: string }> }) {
  const { locale } = await params
  const t = await getTranslations({ locale, namespace: 'deals' })
  return <h1>{t('title')}</h1>
}

// Client Components
import { useTranslations } from 'next-intl'

export function Component() {
  const t = useTranslations('deals')
  return <h1>{t('title')}</h1>
}
```

## Mock 数据规范

Mock 数据需与后端 API 响应保持一致：

```tsx
// types/index.ts
export interface Deal {
  id: number
  title: string
  slug: string
  description?: string
  originalPrice?: number
  dealPrice?: number
  discountPercent?: number
  merchantId: number
  merchantName: string
  merchantLogo?: string
  imageUrl?: string
  startTime?: string
  endTime?: string
  featured?: boolean
  trackingLinkId?: number
}

// Coupon.discountType 枚举
// 1 = 百分比折扣
// 2 = 固定金额
// 3 = 免运费
```

## 重要约束

- ❌ 禁止使用 `as any` 或 `@ts-ignore`
- ❌ 禁止使用 Inter/Roboto/Arial 字体
- ❌ 禁止使用紫色渐变白底设计
- ✅ 优先使用 Server Components
- ✅ 图片必须使用 `next/image`
- ✅ 链接必须使用 `next/link`
- ✅ 遵循 Tailwind CSS 类名顺序
- ✅ 保持深色科技主题风格一致性

## 开发工作流

### 添加新页面

1. 创建 `src/app/[locale]/[page]/page.tsx`
2. 添加 `generateMetadata` 导出
3. 如需动态路由，添加 `generateStaticParams`
4. 在 `src/app/sitemap.ts` 中添加页面
5. 添加翻译 key 到 `src/messages/en.json` 和 `zh.json`

### 添加新组件

1. 创建组件文件 `src/components/[category]/[Component].tsx`
2. 定义 Props interface
3. 遵循项目设计规范 (Nocturnal Tech 主题)
4. 添加悬停效果和过渡动画
5. 确保响应式设计

### 更新 Mock 数据

1. 编辑 `src/lib/mock/[entity].ts`
2. 确保字段与 `src/types/index.ts` 一致
3. 确保与后端 DTO 结构匹配
