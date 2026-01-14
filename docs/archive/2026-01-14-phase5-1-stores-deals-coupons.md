# Phase 5.1: 商家、Deal、优惠券页面

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans

**Goal:** 实现 river-ecommica 前台的商家、Deal、优惠券列表和详情页面
**Architecture:** Next.js 15 SSG+ISR 页面 + Spring Boot app-api 公开接口
**Tech Stack:** Next.js 16.1.1, React 19, TailwindCSS 4, shadcn/ui, next-intl

## 现状分析

### 已有
- 首页 `/[locale]/page.tsx` 展示 featuredDeals + popularStores
- 组件: DealCard, StoreCard, Header, Footer, UI组件(button/badge/card/input)
- API: `api.ts` 有 fetchDeals, fetchStores (带 mock fallback)
- 类型: Deal, Store, Coupon, BlogPost, Category

### 缺失
- **后端**: 无 app-api 控制器，前端无法获取真实数据
- **前端页面**: /stores, /stores/[slug], /deals, /deals/[slug], /coupons

---

## Task 1: 创建后端 App-API 控制器

**Files:**
- `river-server/river-module-affiliate/.../controller/app/AppMerchantController.java`
- `river-server/river-module-affiliate/.../controller/app/vo/AppMerchantRespVO.java`
- `river-server/river-module-coupon/.../controller/app/AppDealController.java`
- `river-server/river-module-coupon/.../controller/app/vo/AppDealRespVO.java`
- `river-server/river-module-coupon/.../controller/app/AppCouponController.java`
- `river-server/river-module-coupon/.../controller/app/vo/AppCouponRespVO.java`

**API Endpoints:**
| Method | Path | 说明 |
|--------|------|------|
| GET | /app-api/affiliate/merchant/list | 商家列表(分页) |
| GET | /app-api/affiliate/merchant/get-by-slug | 按 slug 获取商家详情 |
| GET | /app-api/coupon/deal/list | Deal 列表(分页+筛选) |
| GET | /app-api/coupon/deal/get-by-slug | 按 slug 获取 Deal 详情 |
| GET | /app-api/coupon/coupon/list | 优惠券列表(分页) |

**Steps:**
1. 创建 AppMerchantController + AppMerchantRespVO
2. 创建 AppDealController + AppDealRespVO  
3. 创建 AppCouponController + AppCouponRespVO
4. 在 Service 层添加 getBySlug 方法

**Verification:**
```bash
cd river-server && mvn compile -pl river-module-affiliate/river-module-affiliate-biz,river-module-coupon/river-module-coupon-biz -am
```

**Commit:** `feat(affiliate,coupon): add app-api controllers for frontend`

---

## Task 2: 更新前端 API 层

**Files:**
- `river-ecommica/src/lib/api.ts`
- `river-ecommica/src/types/index.ts`

**Steps:**
1. 添加 fetchMerchantBySlug, fetchDealBySlug, fetchCoupons 函数
2. 更新类型定义匹配后端 VO
3. 添加分页参数支持

**Verification:**
```bash
cd river-ecommica && pnpm build
```

**Commit:** `feat(ecommica): update api layer for stores/deals/coupons`

---

## Task 3: 商家列表页 /stores

**Files:**
- `river-ecommica/src/app/[locale]/stores/page.tsx`
- `river-ecommica/src/components/store/StoreGrid.tsx`
- `river-ecommica/src/messages/en.json` (添加翻译)
- `river-ecommica/src/messages/zh.json` (添加翻译)

**Steps:**
1. 创建 /stores/page.tsx (SSG+ISR, revalidate=3600)
2. 创建 StoreGrid 组件展示商家网格
3. 添加分类筛选 UI
4. 实现 generateMetadata 和 generateStaticParams
5. 添加 i18n 翻译

**Verification:**
```bash
cd river-ecommica && pnpm build && pnpm lint
```

**Commit:** `feat(ecommica): add stores listing page`

---

## Task 4: 商家详情页 /stores/[slug]

**Files:**
- `river-ecommica/src/app/[locale]/stores/[slug]/page.tsx`
- `river-ecommica/src/components/store/StoreHeader.tsx`
- `river-ecommica/src/components/store/StoreDeals.tsx`

**Steps:**
1. 创建 /stores/[slug]/page.tsx (SSG+ISR)
2. 展示商家信息 + 该商家所有优惠
3. 实现 generateStaticParams 预生成路径
4. 添加结构化数据 (Organization schema)

**Verification:**
```bash
cd river-ecommica && pnpm build
```

**Commit:** `feat(ecommica): add store detail page`

---

## Task 5: Deal 列表页 /deals

**Files:**
- `river-ecommica/src/app/[locale]/deals/page.tsx`
- `river-ecommica/src/components/deal/DealGrid.tsx`
- `river-ecommica/src/components/deal/DealFilters.tsx`

**Steps:**
1. 创建 /deals/page.tsx (SSG+ISR)
2. 创建 DealGrid 组件
3. 添加筛选器 (分类、折扣力度、排序)
4. 实现分页

**Verification:**
```bash
cd river-ecommica && pnpm build
```

**Commit:** `feat(ecommica): add deals listing page`

---

## Task 6: Deal 详情页 /deals/[slug]

**Files:**
- `river-ecommica/src/app/[locale]/deals/[slug]/page.tsx`
- `river-ecommica/src/components/deal/DealDetail.tsx`
- `river-ecommica/src/components/deal/TrackingButton.tsx`

**Steps:**
1. 创建 /deals/[slug]/page.tsx (SSG+ISR)
2. 展示 Deal 完整信息
3. 实现追踪链接按钮 (跳转 /api/go/{trackingId})
4. 添加结构化数据 (Offer schema)

**Verification:**
```bash
cd river-ecommica && pnpm build
```

**Commit:** `feat(ecommica): add deal detail page with tracking`

---

## Task 7: 优惠券列表页 /coupons

**Files:**
- `river-ecommica/src/app/[locale]/coupons/page.tsx`
- `river-ecommica/src/components/coupon/CouponCard.tsx`
- `river-ecommica/src/components/coupon/CouponGrid.tsx`

**Steps:**
1. 创建 /coupons/page.tsx (SSG+ISR)
2. 创建 CouponCard 组件 (显示折扣码、复制按钮)
3. 创建 CouponGrid 组件
4. 按商家/折扣类型筛选

**Verification:**
```bash
cd river-ecommica && pnpm build
```

**Commit:** `feat(ecommica): add coupons listing page`

---

## Verification Checklist

- [ ] 后端编译通过 `mvn compile`
- [ ] 前端构建通过 `pnpm build`
- [ ] 所有页面可访问
- [ ] Mock 数据正常显示
- [ ] i18n 翻译完整
- [ ] 已提交 Git

---

## 依赖关系

```
Task 1 (后端API) 
    └──> Task 2 (前端API层)
              └──> Task 3-7 (页面开发，可并行)
```
