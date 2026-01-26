# Draft: 分类按地区展示 + 过期关闭 + 同步优化

## Requirements (confirmed)
- 站点端分类展示：只展示当前地区有数据的分类
- 分类页面需要按地区查询
- 同步逻辑增加定时任务关闭过期数据
- 检查同步逻辑是否还有可优化的地方

## Technical Decisions
- (待确认) 前端站点范围：river-ecommica
- 现有“当前地区”来源：URL 参数 region → cookie(region) → IP 识别 → DEFAULT_REGION
- 过期数据范围：Deal / Coupon / Offer / Merchant
- 过期处理方式：改状态（非软删除）
- 分类“有数据”判定：Deal 或 Coupon 任一
- 分类过滤页面范围：首页分类区块、分类列表页、分类详情页、Sitemap
- 过期判定规则：按 endTime
- 定时任务触发方式：JobHandler + 后台任务配置
- 同步优化关注点：性能 + 一致性
- 分类列表页已存在（只需改逻辑）
- 过期关闭目标状态：按现有枚举
- 测试策略：仅手工验证
- 分类过滤策略：前端传地区参数，后端按地区过滤（与商家/Deal/Coupon一致）
- 分类树筛选：按是否有数据过滤
- 定时任务频率：基础平台配置
- 分类“有数据”统计口径：有效状态 + 未过期的 Deal/Coupon
- 分类树过滤保留父级（子分类有数据时父级保留）
- 同步优化范围：仅 Admitad
- 分类“列表页”指分类详情页 /[locale]/category/[slug]
- Deal 有效状态候选：CommonStatusEnum.ENABLE (0)
- Merchant 有效状态候选：status=1 启用（0 停用）
- Deal/Merchant 有效状态确认：Deal=CommonStatusEnum.ENABLE(0)，Merchant=1 启用
- 同步优化重点：分页批量、去重、幂等
- Sitemap 地区策略：不做地区过滤（全量分类）
- 过期关闭规则：endTime 为空视为未过期
- 分类地区过滤语义：沿用 RegionUtils（regions 为空或包含 GLOBAL 视为全局可用）
- endTime 时区：沿用数据库/服务端默认时区
- Offer/Merchant 无 endTime：不参与过期关闭

## Research Findings
- 前端分类展示：首页与分类页使用 fetchCategories()（/affiliate/category/tree），目前不支持地区参数
- 分类页：/category/[slug] 页面当前对 deals/coupons 无地区过滤
- 前端地区能力：fetchDeals/fetchCoupons/fetchStores 支持 regions 参数；RegionProvider 提供当前地区
- 后端分类 API：/affiliate/category/tree、/list、/get-by-slug，目前无地区过滤
- 后端地区能力：deal/coupon/merchant 列表支持 regions 过滤，RegionUtils 处理 GLOBAL/地区匹配逻辑
- 测试基础设施：前端 Playwright E2E；后端 JUnit5 + Mockito + BaseDbUnitTest
- 后端无现成“过期关闭”定时任务；Coupon/Deal 有 endTime 与 status 字段，CouponStatusEnum 含 EXPIRED(2)
- RegionUtils：GLOBAL 或地区匹配逻辑用于列表过滤（regions 为空也视为全局）
- OfferStatusEnum：ACTIVE(1)/PAUSED(2)/ENDED(3)/PENDING(0)
- CouponStatusEnum：ACTIVE(1)/EXPIRED(2)/DISABLED(0)
- CommonStatusEnum：ENABLE(0)/DISABLE(1)
- 未发现独立“分类列表页”路由；当前仅有分类详情页 /[locale]/category/[slug]，首页存在分类横向导航
- DealDO.status 未绑定枚举；AdmitadSyncService 写入 CommonStatusEnum.ENABLE.getStatus()
- MerchantDO.status 字段注释：0-停用，1-启用（未绑定枚举）
- RegionProvider / getCurrentRegion 在服务端解析 region（URL → cookie → IP → DEFAULT_REGION）
- Sitemap 路由：src/app/sitemap.ts
- CategoryService/CategoryMapper 当前仅支持基础列表/树，不含地区过滤
- RegionUtils.matchesRegion：regions 为空视为全球可用，GLOBAL 也视为匹配
- JobHandler 典型任务：stats/infra 模块有 JobHandler 示例，支持后台配置 cron

## Open Questions
- （已确认）Offer/Merchant 不参与过期关闭

## Scope Boundaries
- INCLUDE: 前端分类展示与分类页地区过滤；后端同步过期关闭与优化
- EXCLUDE: 未明确的其他页面或业务模块
