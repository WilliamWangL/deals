# Phase 6: Ecommica 前台站点实施计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 初始化 river-ecommica (Next.js 15) 前台站点，打造高性能、SEO 友好的优惠券聚合平台。

**Architecture:**
- Framework: Next.js 15 (App Router)
- Language: TypeScript
- Styling: TailwindCSS + shadcn/ui
- State Management: React Context / Zustand (轻量级)
- API Integration: Server Components 直接调用 river-server 接口

**Tech Stack:** Next.js 15, React 18, TypeScript, TailwindCSS, Lucide Icons, next-intl (i18n)

---

## Task 1: 初始化项目

**Files:**
- Create: `river-ecommica/package.json`
- Create: `river-ecommica/tsconfig.json`
- Create: `river-ecommica/next.config.ts`
- Create: `river-ecommica/tailwind.config.ts`

**Command:**
```bash
npx create-next-app@latest river-ecommica --typescript --tailwind --eslint
# 选择: App Router, No src directory (optional), Import alias @/*
```

---

## Task 2: UI 组件库搭建

**Files:**
- Setup: `shadcn/ui`
- Components: `Button`, `Card`, `Input`, `Dialog`, `Badge`

**Steps:**
1. `npx shadcn@latest init`
2. `npx shadcn@latest add button card input dialog badge`
3. 配置 Theme (颜色系统：Primary Blue/Red for deals)

---

## Task 3: 核心页面结构 (App Router)

**Files:**
- `app/layout.tsx`: Root Layout (Header, Footer, Font, Metadata)
- `app/page.tsx`: Homepage (Hero, Trending Deals, Top Stores)
- `app/stores/page.tsx`: 商家列表页
- `app/stores/[slug]/page.tsx`: 商家详情页 (包含该商家的 Coupons)
- `app/deals/page.tsx`: 优惠列表页
- `app/blog/page.tsx`: 博客文章列表 (SEO 内容)

---

## Task 4: 业务组件开发

**Files:**
- Create: `components/layout/Header.tsx` (Logo, Search, Nav)
- Create: `components/layout/Footer.tsx` (Links, Newsletter)
- Create: `components/business/DealCard.tsx` (Image, Title, Price, Copy Code Btn)
- Create: `components/business/StoreCard.tsx` (Logo, Name, Cashback info)
- Create: `components/business/CouponCard.tsx` (Store Logo, Discount, Expiry)

---

## Task 5: API Client & Types

**Files:**
- Create: `lib/api-client.ts` (Fetch wrapper with base URL)
- Create: `types/api.ts` (API Response interfaces)

**Interfaces:**
- `Offer`: 对应后端 OfferDO
- `Merchant`: 对应后端 MerchantDO
- `Category`: 对应后端 CategoryDO

---

## Task 6: SEO 优化配置

**Files:**
- Modify: `app/layout.tsx` (Default Metadata)
- Create: `app/sitemap.ts` (Dynamic sitemap generation)
- Create: `app/robots.ts`

**Features:**
- JSON-LD Structured Data (Product, Organization, Breadcrumb)
- Open Graph Tags (Title, Description, Image)
- Canonical URLs

---

## Task 7: 国际化 (i18n)

**Files:**
- Config: `i18n/request.ts`
- Messages: `messages/en.json`, `messages/zh.json`, `messages/fr.json`

**Logic:**
- 使用 `next-intl` 实现多语言路由 `/en/stores`, `/fr/stores`
- 根据 User-Agent 或 IP 自动检测 (Middleware)

---

## Verification

**Build:**
```bash
cd river-ecommica
npm run build
```

**Test:**
- `npm run dev` 启动本地服务
- 访问首页，检查组件渲染
- 验证 API 数据获取是否正常
- 使用 Lighthouse 跑分 (目标：Performance > 90, SEO = 100)
