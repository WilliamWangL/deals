# 分类按地区展示 + 过期关闭 + Admitad 同步优化

## Context

### Original Request
1) 站点端分类展示只显示当前地区有数据的分类，分类页面按地区查询
2) 同步逻辑增加定时任务关闭过期数据
3) 检查同步逻辑优化（分页批量、去重、幂等）

### Interview Summary
**Key Decisions**
- “当前地区”解析逻辑沿用现有：URL region → cookie(region) → IP → DEFAULT_REGION
- 分类“有数据”判定：Deal 或 Coupon 任一，且为有效状态 + 未过期
- 分类过滤页面范围：首页分类区块、分类详情页 /[locale]/category/[slug]（含你说的“分类列表页”）、Sitemap
- 分类过滤策略：前端传 regions，后端按 regions 过滤（沿用 RegionUtils 语义）
- Sitemap 地区策略：不做地区过滤（输出全量分类）
- 过期范围：Deal/Coupon（Offer/Merchant 无 endTime → 不参与过期关闭）
- 过期判定：endTime < 当前时间；endTime 为空视为未过期
- 过期处理：改状态（按现有枚举）
- 定时任务：JobHandler + 后台任务配置（频率可配置）
- 同步优化范围：仅 Admitad；重点分页批量、去重、幂等
- 测试策略：仅手工验证

**Research Findings**
- 前端分类：`fetchCategories()` 目前仅调用 `/affiliate/category/tree`，无 regions 参数
- 分类页：`/category/[slug]` 目前对 deals/coupons 未传 regions
- 地区能力：`getCurrentRegion` + `RegionProvider` 已存在；`fetchDeals/fetchCoupons/fetchStores` 支持 regions
- 后端分类 API：`AppCategoryController` 的 tree/list/get-by-slug 无地区过滤
- RegionUtils：regions 为空或含 GLOBAL 视为全局可用
- 后端无现成过期关闭任务；示例 JobHandler 在 stats/infra 模块
- 状态枚举：
  - Deal: `CommonStatusEnum.ENABLE(0)` 为有效
  - Coupon: `CouponStatusEnum.ACTIVE(1)` / `EXPIRED(2)`
  - Merchant: status=1 启用（但不参与过期）
  - Offer: `OfferStatusEnum`（不参与过期）

### Metis Review (addressed)
- 明确 region 语义与 sitemap 策略
- endTime 时区沿用默认，无额外转换
- 明确 Offer/Merchant 不参与过期关闭
- 同步优化限定仅 Admitad，避免范围膨胀

---

## Work Objectives

### Core Objective
让站点分类与分类页按地区实时展示“有数据”的分类，同时补充过期关闭定时任务，并优化 Admitad 同步的分页批量、去重与幂等。

### Concrete Deliverables
- 前端分类相关页面：按当前地区传参并渲染过滤后的分类
- 后端分类 API：支持 regions 过滤并按“有数据”筛选
- 新增过期关闭 JobHandler（Deal/Coupon）
- Admitad 同步优化：分页批量、去重、幂等

### Definition of Done
- 分类树 API 支持 regions 参数，返回仅包含“有数据”的分类（保留有数据子分类的父级）
- 首页分类区块 + 分类详情页均按当前地区展示分类与数据
- 新增过期关闭 JobHandler，可通过后台任务配置触发
- Admitad 同步保持幂等（重复执行不产生重复数据）

### Must NOT Have (Guardrails)
- 不新增/修改数据库表结构
- 不改变 region 解析优先级
- 不新增外部依赖
- Sitemap 仍输出全量分类，不做地区过滤
- 同步优化仅限 Admitad

---

## Verification Strategy (Manual Only)

### Manual QA Checklist (by deliverable)

**Frontend/UI** (Playwright)
- 访问首页 `http://localhost:3000/[locale]?region=XX`：分类区块只显示有数据分类
- 访问分类页 `http://localhost:3000/[locale]/category/[slug]?region=XX`：
  - 分类面包屑、分类信息正常
  - Deals/Coupons 列表与地区过滤一致

**Backend API**
- `GET /app-api/affiliate/category/tree?regions=XX`
  - 返回分类数减少（仅有数据分类）
  - 父级保留（子分类有数据时父级也出现）

**JobHandler**
- 后台创建并执行“过期关闭”任务（JobHandler）
  - endTime < now 的 Deal/Coupon 被改为过期/禁用状态
  - endTime 为空不受影响

**Admitad 同步**
- 触发 Admitad 同步两次
  - 第二次不新增重复 Deal/Coupon/Merchant/Offer
  - 更新逻辑保持一致

---

## Task Flow

1) 后端分类 API 增加 regions + 有数据筛选
2) 前端分类相关页面按地区调用并渲染
3) 过期关闭 JobHandler
4) Admitad 同步优化（分页批量/去重/幂等）

---

## TODOs

### 1) 后端分类 API 增加 regions + 有数据筛选 ✅ COMPLETED

**What to do**
- 为 `AppCategoryController` 的 tree/list/get-by-slug 增加 `regions` 可选参数
- 在 `CategoryService` 增加按 regions 过滤的方法：
  - 计算当前地区“有数据”的分类 ID 集合：
    - 数据来源：Deal + Coupon
    - 过滤规则：status=有效 + endTime 未过期 + regions 匹配（RegionUtils 语义）
  - 父级保留：若子分类有数据，父级保留在树中
- 构建树时仅保留 “有数据” 的分类（含父级）

**Must NOT do**
- 不改变分类表结构
- 不改变 RegionUtils 逻辑

**Parallelizable**: NO (后端基础能力先完成)

**References**
- `river-server/river-module-affiliate/controller/app/AppCategoryController.java`：分类 API 入口
- `river-server/river-module-affiliate/service/CategoryService.java` / `CategoryServiceImpl.java`：分类树构建与列表接口
- `river-server/river-module-affiliate/dal/mysql/CategoryMapper.java`：分类查询方式
- `river-server/river-module-coupon/dal/mysql/DealMapper.java`：regions 过滤 SQL 模式
- `river-server/river-module-coupon/dal/mysql/CouponMapper.java`：regions 过滤 SQL 模式
- `river-framework/river-common/util/region/RegionUtils.java`：GLOBAL 处理逻辑
- `river-server/river-module-coupon/dal/dataobject/DealDO.java`：endTime/status/categoryIds 字段
- `river-server/river-module-coupon/dal/dataobject/CouponDO.java`：endTime/status/categoryIds 字段

**Acceptance Criteria**
- `GET /app-api/affiliate/category/tree?regions=US` 返回仅包含“有数据”分类
- 父级分类在子分类有数据时被保留
- 未传 regions 时保持现有行为（全量分类）

---

### 2) 前端分类页面按地区调用与展示 ✅ COMPLETED

**What to do**
- 扩展 `fetchCategories` 支持 `regions` 参数（query params）
- 首页 `src/app/[locale]/page.tsx` 传入当前地区到 `fetchCategories`
- 分类页 `src/app/[locale]/category/[slug]/page.tsx`：
  - 使用 `getCurrentRegion` 解析地区
  - `fetchDeals`/`fetchCoupons` 传 regions
  - `fetchCategories` 传 regions（用于面包屑/分类信息）
- Sitemap 保持全量分类（不传 regions）

**Must NOT do**
- 不改变 RegionProvider/RegionSelector 现有逻辑

**Parallelizable**: YES (可与任务 3 并行)

**References**
- `river-ecommica/src/lib/api.ts`：fetchCategories/fetchDeals/fetchCoupons
- `river-ecommica/src/app/[locale]/page.tsx`：首页分类区域与 region 使用
- `river-ecommica/src/app/[locale]/category/[slug]/page.tsx`：分类详情页
- `river-ecommica/src/lib/region.ts`：getCurrentRegion
- `river-ecommica/src/app/sitemap.ts`：Sitemap 生成逻辑

**Acceptance Criteria**
- 首页 & 分类页使用 `?region=XX` 时分类展示与地区一致
- Sitemap 仍输出全量分类

---

### 3) 新增过期关闭 JobHandler（Deal/Coupon） ✅ COMPLETED

**What to do**
- 新增 JobHandler（参考 AdmitadSyncJob/Stats Jobs），每次执行：
  - Deal：status=ENABLE 且 endTime < now → 更新为 DISABLE
  - Coupon：status=ACTIVE 且 endTime < now → 更新为 EXPIRED
  - endTime 为空不处理
- 对应 Service/Mapper 提供批量更新方法（分页或一次性更新）

**Must NOT do**
- 不对 Offer/Merchant 进行过期关闭

**Parallelizable**: YES (与任务 2 并行)

**References**
- `river-server/river-module-affiliate/service/network/admitad/AdmitadSyncJob.java`：JobHandler 示例
- `river-server/river-module-stats/service/alert/*Job.java`：JobHandler 模式
- `river-server/river-module-coupon/dal/dataobject/DealDO.java`：endTime/status
- `river-server/river-module-coupon/dal/dataobject/CouponDO.java`：endTime/status
- `river-server/river-module-coupon/enums/CouponStatusEnum.java`：ACTIVE/EXPIRED
- `river-framework/river-common/enums/CommonStatusEnum.java`：ENABLE/DISABLE

**Acceptance Criteria**
- 后台创建 JobHandler 任务并执行，endTime 已过期的 Deal/Coupon 状态被更新
- endTime 为空数据保持不变

---

### 4) Admitad 同步优化：分页批量/去重/幂等 ✅ COMPLETED

**What to do**
- Admitad 同步获取分页时：
  - 使用批量接口/分页方式拉取
  - 预加载已存在数据（networkId + externalId 作为幂等 key）
  - 去重：同一批次内去重 externalId
  - 幂等写入：存在则更新，不存在才插入
- 对于创建分类/商家/offer/deal/coupon：
  - 通过 map 缓存减少重复 DB 查询
  - 批量 insert/update（避免逐条写）

**Must NOT do**
- 不引入新网络/缓存依赖
- 不更改同步业务含义，仅优化实现

**Parallelizable**: NO (依赖当前 Admitad 同步流程理解)

**References**
- `river-server/river-module-affiliate/service/network/admitad/AdmitadSyncService.java`：同步主流程
- `river-server/river-module-affiliate/service/network/admitad/AdmitadSyncJob.java`：同步任务入口
- `river-server/river-module-coupon/dal/dataobject/DealDO.java` / `CouponDO.java`：externalId/networkId
- `river-server/river-module-affiliate/dal/dataobject/MerchantDO.java` / `OfferDO.java`：externalId/networkId

**Acceptance Criteria**
- 同一数据重复同步不会产生重复记录
- 分页批量同步在现有数据量下无明显性能退化

---

## Commit Strategy
- Commit 1: `feat(category): filter categories by region data`
- Commit 2: `feat(job): add expiration job for deals/coupons`
- Commit 3: `refactor(sync): optimize Admitad sync batching`

---

## Success Criteria
- 分类展示与分类页按地区过滤生效
- 后端分类 API 支持 regions 参数且筛选规则正确
- 过期关闭任务可通过后台配置执行
- Admitad 同步具备分页批量、去重、幂等能力
