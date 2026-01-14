# Phase 6: river-server 业务模块单元测试

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans

**Goal:** 为 6 个 River 业务模块添加单元测试，覆盖核心 Service 层
**Architecture:** JUnit 5 + Mockito + Spring Boot Test
**Tech Stack:** Java 17, JUnit 5, Mockito, AssertJ

## 需要测试的模块

| 模块 | Service | 路径 |
|------|---------|------|
| Affiliate | MerchantService, OfferService | river-module-affiliate |
| Tracking | TrackingService | river-module-tracking |
| Campaign | CampaignService | river-module-campaign |
| Coupon | CouponService, DealService | river-module-coupon |
| Blog | PostService | river-module-blog |
| Stats | DailyStatsService | river-module-stats |

## 测试策略

每个 Service 测试覆盖:
1. 创建操作 (create)
2. 更新操作 (update)
3. 删除操作 (delete)
4. 查询操作 (get/page)
5. 边界条件和异常处理

## Verification Checklist
- [ ] mvn test 通过
- [ ] 每个模块至少有一个测试类
- [ ] 核心业务逻辑有测试覆盖
- [ ] 已提交 Git
