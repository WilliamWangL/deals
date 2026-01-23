# Draft: AffiliateNetwork 同步功能重构与扩展

## 现状分析

### 当前代码结构

**AffiliateNetworkController** 已有三个同步接口（`affiliate/network` 路径下）：
- `POST /affiliate/network/sync` - 同步商家和 Offer
- `POST /affiliate/network/sync-coupons` - **混合接口**：同时同步 Coupon 和 Deal（根据 species 字段分流）
- `POST /affiliate/network/sync-all` - 全量同步（先商家/Offer，再 Coupon/Deal）

**AdmitadSyncService** 实现细节：
- `syncCampaigns()` - 同步商家和 Offer
- `syncCoupons()` - 混合处理：promocode → CouponDO，sale/其他 → DealDO
- `syncSingleCoupon()` - 单独同步 Coupon
- `syncSingleDeal()` - 单独同步 Deal

### 数据模型已存在

- **MerchantDO** / **OfferDO** - 在 `river-module-affiliate` 模块
- **CouponDO** (`river_coupon_coupon` 表) - 在 `river-module-coupon` 模块
- **DealDO** (`river_coupon_deal` 表) - 在 `river-module-coupon` 模块

## 用户需求的两种可能解释

### 场景 1：Controller 层接口拆分（最可能）
用户希望在 `AffiliateNetworkController` 中增加独立的接口：
- 保留现有 `sync-coupons` 接口（兼容）
- 新增 `POST /affiliate/network/sync-deals` - 仅同步 Deal
- 新增 `POST /affiliate/network/sync-coupons-only` - 仅同步 Coupon

### 场景 2：Service 层逻辑解耦
当前 `syncCoupons()` 方法内部混合处理，需要拆分为：
- `syncCoupons()` - 仅 Coupon
- `syncDeals()` - 仅 Deal

## 待确认问题

1. **接口拆分**：是否需要在 Controller 增加独立的 `sync-deal` 和 `sync-coupon` 接口？
2. **前端需求**：管理后台是否需要独立的 Deal 同步和 Coupon 同步按钮？
3. **路径选择**：同步接口应该放在哪里？
   - 选项 A：`/affiliate/network/sync-deals` / `/affiliate/network/sync-coupons`（联盟网络模块）
   - 选项 B：`/coupon/deal/sync-network` / `/coupon/coupon/sync-network`（优惠券模块）
4. **参数设计**：同步时是否需要指定 networkId，还是通过 code 自动查找？

