# River 广告平台 - 缺失模块完成设计

> **Created**: 2026-01-14
> **Status**: Approved

## 目标

完成 River 广告平台 6 个缺失模块的实现。

## 模块清单

| Phase | 模块 | 优先级 | 预估工作量 |
|-------|------|--------|-----------|
| 1 | ecommica 追踪系统 (`/api/go` + `tracking.ts`) | P0 | 小 |
| 2 | ecommica Mock 数据替换为真实 API | P0 | 中 |
| 3 | admin River CRUD 页面 | P0 | 大 |
| 4 | admin Dashboard 真实数据接入 | P1 | 小 |
| 5 | server Alert 告警逻辑实现 | P1 | 中 |
| 6 | server 业务模块单元测试 | P2 | 大 |

## 技术设计

### Phase 1: ecommica 追踪系统

```typescript
// /api/go/[id]/route.ts - Edge Runtime
export async function GET(req, { params }) {
  const offerId = params.id
  const clickId = generateULID()
  
  // 1. 调用 river-server /app-api/tracking/click 记录点击
  // 2. 获取 offer 的联盟跳转 URL
  // 3. 302 重定向到联盟链接
}

// lib/tracking.ts
export function generateClickId(): string
export function trackClick(offerId: string, meta: ClickMeta): Promise<string>
export function getTrackingUrl(offerId: string, clickId: string): string
```

### Phase 2: ecommica API 集成

```typescript
// lib/api.ts
const API_BASE = process.env.NEXT_PUBLIC_API_URL

export async function getStores(): Promise<Store[]>
export async function getDeals(): Promise<Deal[]>
export async function getCoupons(): Promise<Coupon[]>
export async function getBlogPosts(): Promise<BlogPost[]>
```

### Phase 3: admin River CRUD

遵循 yudao-ui-admin 模式创建：
- `/river/merchant/index.vue` - 商家管理
- `/river/offer/index.vue` - Offer 管理
- `/river/campaign/index.vue` - Campaign 管理
- `/river/coupon/index.vue` - 优惠券管理
- `/river/deal/index.vue` - Deal 管理
- `/river/blog/index.vue` - 博客管理

### Phase 4: admin Dashboard

调用 `/admin-api/river/stats/summary` 获取真实统计数据。

### Phase 5: server Alert

实现 `AlertDailyCheckJob` 和 `AlertHourlyCheckJob`：
- 检测异常指标（点击下降、转化异常等）
- 触发消息通知

### Phase 6: server Tests

使用 JUnit 5 + Mockito 覆盖：
- AffiliateService
- TrackingService
- CampaignService
- CouponService
- BlogService
- StatsService

## 依赖关系

```
Phase 1 → Phase 2 → Phase 3/4 (并行) → Phase 5/6 (并行)
```

## 验收标准

- [ ] Phase 1: `/api/go/[id]` 可正常跳转并记录点击
- [ ] Phase 2: 首页展示真实商家和 Deal 数据
- [ ] Phase 3: 可通过 admin 管理 River 所有实体
- [ ] Phase 4: Dashboard 展示真实统计
- [ ] Phase 5: 告警任务可检测异常并通知
- [ ] Phase 6: 业务模块测试覆盖率 > 80%
