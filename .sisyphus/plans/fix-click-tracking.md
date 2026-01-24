# 修复 Deal 和 Coupon 点击追踪问题

## Context

### Original Request
用户在站点点击 deal 和 coupon 时没有点击记录，需要排查并修复问题。

### Interview Summary

**Key Discussions**:
- 前端使用 Next.js + React，后端使用 Spring Boot
- 点击追踪系统已存在：后端有 `ClickServiceImpl.recordClickAndGetRedirectUrl()` 
- 追踪 API 路由 `/api/go/[id]` 已实现为 Edge Function
- 数据库表 `river_tracking_link` 和 `river_tracking_click` 已存在
- 后端 API 响应已包含 `trackingLinkId` 字段

**Research Findings**:
- 后端 `AppDealRespVO` 和 `AppCouponRespVO` 已正确返回 `trackingLinkId`
- 前端类型定义 `Deal` 和 `Coupon` 接口缺少 `trackingLinkId` 字段
- 前端组件直接使用 `gotoUrl` 绕过追踪系统
- 追踪工具函数 `getTrackingUrl()` 在 `lib/tracking.ts` 中已存在

### Metis Review

**Identified Gaps (Addressed)**:
- Deal 详情页 (`src/app/[locale]/deals/[slug]/page.tsx`) 也存在同样问题
- 5 处需要修改：DealCard (1处)、CouponCard (3处)、Deal详情页 (1处)
- 需要添加空值检查和 gotoUrl 回退机制

---

## Work Objectives

### Core Objective
修复前端 deal 和 coupon 卡片点击时绕过追踪系统的问题，使用 `/api/go/{trackingLinkId}` 路由记录点击。

### Concrete Deliverables
1. 修改 `types/index.ts` - 添加 `trackingLinkId` 字段
2. 修改 `DealCard.tsx` - 追踪链接
3. 修改 `CouponCard.tsx` - 3 处追踪链接
4. 修改 `deals/[slug]/page.tsx` - 追踪链接

### Definition of Done
- [x] `pnpm build` 编译成功
- [x] TypeScript 类型检查通过
- [x] 5 处链接全部使用追踪 URL
- [x] 空 `trackingLinkId` 时回退到 `gotoUrl`

### Must Have
- 所有 deal/coupon 点击通过 `/api/go/{id}` 路由
- 点击记录写入 `river_click` 表
- 类型定义完整

### Must NOT Have (Guardrails)
- 不修改后端 API（前端修复）
- 不添加新的 API 端点
- 不改变 SEO 逻辑（JSON-LD 使用 deal 数据）
- 不添加超出追踪系统范围的统计功能

---

## Verification Strategy

### Test Decision
- **Infrastructure exists**: YES (TypeScript + Jest in Next.js)
- **User wants tests**: Manual verification (see below)
- **Framework**: N/A (TypeScript compilation sufficient)

### Manual Execution Verification

**Build Verification**:
```bash
cd river-ecommica
pnpm build  # 编译成功，无错误
```

**Type Check**:
```bash
pnpm type-check  # 类型检查通过
```

**Manual Test**:
1. 访问 `/deals` 页面，点击任意 deal 的 "Get Deal" 按钮
2. 访问 `/coupons` 页面，点击任意 coupon 的链接
3. 验证后端 `river_tracking_click` 表有新记录
4. 验证浏览器正确重定向到商家网站

---

## Task Flow

```
Task 1 (类型定义) → Task 2 (DealCard) → Task 3 (CouponCard) → Task 4 (Deal详情页)
```

## Parallelization

| Group | Tasks | Reason |
|-------|-------|--------|
| A | 2, 3, 4 | 互相独立，每个文件单独修改 |

---

## TODOs

> Implementation + Test = ONE Task. Never separate.
> Specify parallelizability for EVERY task.

- [x] 1. 添加 trackingLinkId 到类型定义

  **What to do**:
  - 在 `types/index.ts` 中 `Deal` 接口添加 `trackingLinkId?: string`
  - 在 `types/index.ts` 中 `Coupon` 接口添加 `trackingLinkId?: string`

  **Must NOT do**:
  - 不修改其他字段

  **Parallelizable**: YES (with 2, 3, 4)

  **References**:
  - `types/index.ts:8-23` - Deal 接口定义位置
  - `types/index.ts:38-50` - Coupon 接口定义位置
  - `AppDealRespVO.java:55-56` - 后端返回 trackingLinkId
  - `AppCouponRespVO.java:46-47` - 后端返回 trackingLinkId

  **Acceptance Criteria**:
  - [ ] `types/index.ts` 已更新
  - [ ] `pnpm type-check` 通过

  **Commit**: YES
  - Message: `fix(types): add trackingLinkId to Deal and Coupon interfaces`
  - Files: `river-ecommica/src/types/index.ts`

---

- [x] 2. 修复 DealCard 追踪链接

  **What to do**:
  - 在 `DealCard.tsx:200` 将 `href={deal.gotoUrl}` 改为追踪链接
  - 使用 `deal.trackingLinkId ? /api/go/${deal.trackingLinkId} : deal.gotoUrl`

  **Must NOT do**:
  - 不修改其他按钮或链接

  **Parallelizable**: YES (with 1, 3, 4)

  **References**:
  - `DealCard.tsx:199-213` - CTA 按钮区域
  - `lib/tracking.ts:7-10` - `getTrackingUrl()` 函数模式

  **Acceptance Criteria**:
  - [ ] DealCard.tsx:200 使用追踪链接
  - [ ] 空值时回退到 gotoUrl
  - [ ] `pnpm build` 通过

  **Commit**: YES
  - Message: `fix(DealCard): use tracking URL for CTA link`
  - Files: `river-ecommica/src/components/deal/DealCard.tsx`

---

- [x] 3. 修复 CouponCard 追踪链接 (3处)

  **What to do**:
  - Line 78: 商户 logo 链接 → `/api/go/${coupon.trackingLinkId}`
  - Line 99: 商户名称链接 → `/api/go/${coupon.trackingLinkId}`
  - Line 217: 底部 "Get Coupon" 链接 → `/api/go/${coupon.trackingLinkId}`
  - 每处使用 `coupon.trackingLinkId ? /api/go/${coupon.trackingLinkId} : coupon.gotoUrl`

  **Must NOT do**:
  - 不修改复制优惠券代码的逻辑
  - 不修改其他 UI 元素

  **Parallelizable**: YES (with 1, 2, 4)

  **References**:
  - `CouponCard.tsx:77-96` - 商户 logo 和名称区域
  - `CouponCard.tsx:215-224` - 底部链接区域
  - `lib/tracking.ts:7-10` - 追踪 URL 构建模式

  **Acceptance Criteria**:
  - [ ] CouponCard.tsx:78 使用追踪链接
  - [ ] CouponCard.tsx:99 使用追踪链接
  - [ ] CouponCard.tsx:217 使用追踪链接
  - [ ] 空值时回退到 gotoUrl
  - [ ] `pnpm build` 通过

  **Commit**: YES
  - Message: `fix(CouponCard): use tracking URLs for all merchant links`
  - Files: `river-ecommica/src/components/coupon/CouponCard.tsx`

---

- [x] 4. 修复 Deal 详情页追踪链接

  **What to do**:
  - Line 51: 将 `const trackingUrl = deal.gotoUrl || '#'` 改为使用追踪链接
  - Line 110: `<a href={trackingUrl}>` 使用追踪链接
  - 使用 `deal.trackingLinkId ? /api/go/${deal.trackingLinkId} : deal.gotoUrl`

  **Must NOT do**:
  - 不修改 SEO JSON-LD 逻辑
  - 不修改面包屑导航

  **Parallelizable**: YES (with 1, 2, 3)

  **References**:
  - `deals/[slug]/page.tsx:51` - trackingUrl 变量定义
  - `deals/[slug]/page.tsx:109-113` - CTA 按钮区域
  - `lib/tracking.ts:7-10` - 追踪 URL 构建模式

  **Acceptance Criteria**:
  - [ ] deals/[slug]/page.tsx:51 使用追踪链接
  - [ ] deals/[slug]/page.tsx:110 使用追踪链接
  - [ ] 空值时回退到 gotoUrl
  - [ ] `pnpm build` 通过

  **Commit**: YES
  - Message: `fix(DealDetailPage): use tracking URL for CTA link`
  - Files: `river-ecommica/src/app/[locale]/deals/[slug]/page.tsx`

---

## Commit Strategy

| After Task | Message | Files | Verification |
|------------|---------|-------|--------------|
| 1 | `fix(types): add trackingLinkId to Deal and Coupon interfaces` | types/index.ts | pnpm type-check |
| 2 | `fix(DealCard): use tracking URL for CTA link` | DealCard.tsx | pnpm build |
| 3 | `fix(CouponCard): use tracking URLs for all merchant links` | CouponCard.tsx | pnpm build |
| 4 | `fix(DealDetailPage): use tracking URL for CTA link` | deals/[slug]/page.tsx | pnpm build |

---

## Success Criteria

### Verification Commands
```bash
cd river-ecommica

# 类型检查
pnpm type-check  # Expected: No errors

# 构建
pnpm build  # Expected: Build successful

# 手动测试
# 1. Visit http://localhost:3000/deals
# 2. Click "Get Deal" button on any deal card
# 3. Check browser network tab for /api/go/{id} request
# 4. Verify redirect to merchant site
```

### Final Checklist
- [x] All "Must Have" present (tracking links work)
- [x] All "Must NOT Have" absent (no backend changes)
- [x] TypeScript compilation passes
- [x] Build succeeds
- [x] 5 link locations updated with tracking URL
- [x] Null checks with gotoUrl fallback implemented
