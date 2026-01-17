# River 模块字典改造设计

> 创建日期：2026-01-17
> 状态：待实施

## 1. 背景与目标

### 1.1 问题

River 模块（admin-ui 和后端）中存在大量硬编码的下拉框和状态值：

- **前端**：27 个 Vue 文件中约 110+ 处硬编码选项（如 `<el-option label="启用" :value="1" />`）
- **后端**：`AdmitadSyncService` 中状态和类型映射使用魔法数字
- **状态值不一致**：River 模块使用 `1=启用/0=禁用`，系统 `COMMON_STATUS` 使用 `0=开启/1=关闭`

### 1.2 目标

1. 将所有硬编码选项改为使用系统字典
2. 统一状态值定义，与系统 `COMMON_STATUS` 保持一致
3. 后端使用枚举替代魔法数字
4. 提升可维护性和一致性

## 2. 改造范围

### 2.1 总体范围

| 层级 | 改造内容 |
|------|---------|
| **数据库** | 状态值反转（0↔1）、补充字典数据、修改默认值 |
| **后端** | 新建/更新枚举、改造 AdmitadSyncService |
| **前端** | 扩展 DICT_TYPE、改造下拉框和表格列 |

### 2.2 不在范围内

- 后端业务逻辑不变
- 不新增数据库表
- 不改变 API 接口

## 3. 后端枚举改造

### 3.1 枚举清理

| 现有枚举 | 处理方式 | 说明 |
|---------|---------|------|
| `CommissionTypeEnum` (百分比/固定/阶梯) | **删除** | 与设计文档不符，仅作注释引用 |

### 3.2 新建/更新枚举

| 枚举名 | 位置 | 值 | 对应字典 |
|--------|------|------|---------|
| `PayoutModelEnum` | affiliate/enums | CPS(1), CPA(2), CPC(3), CPL(4), CPM(5) | `affiliate_commission_type` |
| `CommonStatusEnum` | common/enums | ENABLE(0), DISABLE(1) | `common_status` |
| `CouponTypeEnum` | coupon/enums | PROMOCODE(1), SALE(2), DEAL(3) | `coupon_type` |

### 3.3 AdmitadSyncService 改造

| 行号 | 当前硬编码 | 改为 |
|------|-----------|------|
| 206-207 | `"active" → 1, 其他 → 0` | `CommonStatusEnum.ENABLE/DISABLE` |
| 210-218 | `sale→1, lead→2, click→3` | `PayoutModelEnum.CPS/CPA/CPC` |
| 280, 387, 453 | `setStatus(1)` | `CommonStatusEnum.ENABLE.getCode()` |
| 496-502 | `promocode→1, sale→2` | `CouponTypeEnum` |
| 513, 523 | `setDiscountType(1/2)` | `DiscountTypeEnum.PERCENT/FIXED` |

## 4. 数据库迁移

### 4.1 迁移脚本

文件：`sql/migrations/V2026.01.17__dict_and_status_fix.sql`

### 4.2 状态值反转

需要更新的表（status 字段 0↔1 互换）：

```
river_affiliate_network
river_affiliate_merchant
river_affiliate_offer
river_affiliate_category
river_coupon_coupon
river_coupon_deal
river_blog_author
river_blog_tag
river_blog_post（status=1 改为 0）
river_campaign_traffic_source
river_campaign_campaign
river_campaign_ad_group
river_campaign_landing_page
river_campaign_currency
river_tracking_link
```

### 4.3 修改默认值

SQL 文件中 `DEFAULT 1` → `DEFAULT 0`：

- `sql/postgresql/affiliate/river_affiliate.sql`
- `sql/postgresql/coupon/river_coupon.sql`
- `sql/postgresql/blog/river_blog.sql`
- `sql/postgresql/campaign/river_campaign.sql`
- `sql/postgresql/tracking/river_tracking.sql`

### 4.4 补充字典数据

| 字典类型 | 补充内容 | dict_type ID | dict_data ID |
|---------|---------|--------------|--------------|
| `affiliate_commission_type` | CPL(4), CPM(5) | - | 20007-20008 |
| `coupon_type` (新建) | 优惠码(1), 促销(2), Deal(3) | 20052 | 20150-20152 |

### 4.5 迁移 SQL 示例

```sql
-- 1. 更新现有数据（状态值 0↔1 互换）
UPDATE river_affiliate_network SET status = CASE WHEN status = 1 THEN 0 WHEN status = 0 THEN 1 END WHERE deleted = 0;
UPDATE river_affiliate_merchant SET status = CASE WHEN status = 1 THEN 0 WHEN status = 0 THEN 1 END WHERE deleted = 0;
-- ... 其他表

-- 2. 修改列默认值
ALTER TABLE river_affiliate_network ALTER COLUMN status SET DEFAULT 0;
ALTER TABLE river_affiliate_merchant ALTER COLUMN status SET DEFAULT 0;
-- ... 其他表

-- 3. 补充字典类型
INSERT INTO system_dict_type (id, name, type, status, remark, creator, create_time, updater, update_time, deleted)
VALUES (20052, '优惠券类型', 'coupon_type', 0, '优惠券类型', '1', NOW(), '1', NOW(), 0);

-- 4. 补充字典数据
INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted)
VALUES
(20007, 4, 'CPL (按潜在客户)', '4', 'affiliate_commission_type', 0, 'warning', '', '', '1', NOW(), '1', NOW(), 0),
(20008, 5, 'CPM (按千次展示)', '5', 'affiliate_commission_type', 0, 'info', '', '', '1', NOW(), '1', NOW(), 0),
(20150, 1, '优惠码', '1', 'coupon_type', 0, 'primary', '', '', '1', NOW(), '1', NOW(), 0),
(20151, 2, '促销', '2', 'coupon_type', 0, 'success', '', '', '1', NOW(), '1', NOW(), 0),
(20152, 3, 'Deal', '3', 'coupon_type', 0, 'warning', '', '', '1', NOW(), '1', NOW(), 0);
```

## 5. 前端改造

### 5.1 DICT_TYPE 枚举扩展

在 `src/utils/dict.ts` 添加（第 250 行之前）：

```typescript
// ========== RIVER - 联盟营销模块 ==========
AFFILIATE_NETWORK_TYPE = 'affiliate_network_type', // 联盟网络类型
AFFILIATE_COMMISSION_TYPE = 'affiliate_commission_type', // 佣金类型(计费模型)
AFFILIATE_AUTH_TYPE = 'affiliate_auth_type', // 联盟凭证认证类型
AFFILIATE_OFFER_STATUS = 'affiliate_offer_status', // Offer状态

// ========== RIVER - 优惠券模块 ==========
COUPON_DISCOUNT_TYPE = 'coupon_discount_type', // 优惠券折扣类型
COUPON_SOURCE = 'coupon_source', // 优惠券来源
COUPON_TYPE = 'coupon_type', // 优惠券类型

// ========== RIVER - 博客模块 ==========
BLOG_POST_TYPE = 'blog_post_type', // 文章类型
BLOG_POST_STATUS = 'blog_post_status', // 文章状态

// ========== RIVER - Campaign模块 ==========
CAMPAIGN_TYPE = 'campaign_type', // Campaign类型
CAMPAIGN_LANDING_PAGE_TYPE = 'campaign_landing_page_type', // 落地页类型
CAMPAIGN_COST_SOURCE = 'campaign_cost_source', // 成本来源

// ========== RIVER - 追踪模块 ==========
TRACKING_CONVERSION_TYPE = 'tracking_conversion_type', // 转化类型
TRACKING_CONVERSION_STATUS = 'tracking_conversion_status', // 转化状态
TRACKING_ATTRIBUTION_TYPE = 'tracking_attribution_type', // 归因类型

// ========== RIVER - 统计模块 ==========
STATS_DIMENSION_TYPE = 'stats_dimension_type', // 统计维度类型
```

### 5.2 下拉框改造模式

```vue
<!-- 改造前 -->
<el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
  <el-option label="启用" :value="1" />
  <el-option label="禁用" :value="0" />
</el-select>

<!-- 改造后 -->
<el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
  <el-option
    v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)"
    :key="dict.value"
    :label="dict.label"
    :value="dict.value"
  />
</el-select>
```

### 5.3 表格列改造模式

```vue
<!-- 改造前 -->
<el-table-column label="状态" prop="status">
  <template #default="scope">
    <el-tag v-if="scope.row.status === 1" type="success">启用</el-tag>
    <el-tag v-else type="danger">禁用</el-tag>
  </template>
</el-table-column>

<!-- 改造后 -->
<el-table-column label="状态" prop="status">
  <template #default="scope">
    <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="scope.row.status" />
  </template>
</el-table-column>
```

### 5.4 涉及文件清单

| 模块 | 文件 | 改造字段 |
|------|------|----------|
| affiliate | network/index.vue, AffiliateNetworkForm.vue | status, type |
| affiliate | merchant/index.vue, MerchantForm.vue | status |
| affiliate | category/index.vue, CategoryForm.vue | status |
| affiliate | offer/index.vue, OfferForm.vue | status, payout_type |
| coupon | coupon/index.vue, CouponForm.vue | status, discountType, source |
| coupon | deal/index.vue, DealForm.vue | status |
| blog | author/index.vue | status |
| blog | tag/index.vue | status |
| blog | post/index.vue, PostForm.vue | status, type |
| campaign | traffic-source/index.vue | status |
| campaign | campaign/index.vue, CampaignForm.vue | status, type |
| campaign | ad-group/index.vue | status |
| campaign | landing-page/index.vue, LandingPageForm.vue | status, type |
| campaign | currency/index.vue | status |
| tracking | tracking-link/index.vue | status |
| tracking | conversion/index.vue, ConversionForm.vue | type, status |
| tracking | attribution/index.vue | type |
| stats | daily/index.vue, hourly/index.vue | dimension_type |

## 6. 实施顺序

```
1. 后端枚举创建/更新
   ├── 新建 PayoutModelEnum, CommonStatusEnum, CouponTypeEnum
   ├── 删除 CommissionTypeEnum
   └── 更新 AdmitadSyncService 使用枚举

2. 数据库迁移
   ├── 补充字典数据 (CPL, CPM, coupon_type)
   ├── 更新现有数据 (status 0↔1 互换)
   └── 修改列默认值

3. 前端改造
   ├── 扩展 DICT_TYPE 枚举
   ├── 改造下拉框组件
   └── 改造表格列显示

4. SQL 文件更新
   └── 修改 river_*.sql 中的 DEFAULT 值
```

## 7. 验证清单

| 验证项 | 方法 |
|--------|------|
| 后端编译通过 | `mvn clean compile` |
| 前端类型检查 | `pnpm ts:check` |
| 字典数据加载 | 登录后台，检查下拉框选项 |
| 状态显示正确 | 检查列表页状态标签颜色 |
| Admitad 同步正常 | 手动触发同步，检查 status 值 |
| 新增数据默认值 | 创建新记录，检查 status 默认为 0（开启） |

## 8. 回滚方案

如果出现问题，数据库迁移可逆：

```sql
-- 回滚状态值
UPDATE river_affiliate_network SET status = CASE WHEN status = 0 THEN 1 WHEN status = 1 THEN 0 END WHERE deleted = 0;
-- ... 其他表

-- 回滚默认值
ALTER TABLE river_affiliate_network ALTER COLUMN status SET DEFAULT 1;
-- ... 其他表

-- 删除新增字典
DELETE FROM system_dict_data WHERE id IN (20007, 20008, 20150, 20151, 20152);
DELETE FROM system_dict_type WHERE id = 20052;
```

## 9. 字典类型与值对照表

### 9.1 已有字典（数据库中已存在）

| dict_type | 含义 | 值定义 |
|-----------|------|--------|
| `common_status` | 通用状态 | 0=开启, 1=关闭 |
| `affiliate_network_type` | 联盟网络类型 | 1=CPS, 2=CPA, 3=CPC, 4=混合型 |
| `affiliate_auth_type` | 认证类型 | 1=OAuth2, 2=Bearer Token, 3=API Key |
| `affiliate_commission_type` | 计费模型 | 1=CPS, 2=CPA, 3=CPC, 4=CPL(新), 5=CPM(新) |
| `affiliate_offer_status` | Offer状态 | 1=活跃, 2=暂停, 3=结束 |
| `coupon_discount_type` | 折扣类型 | 1=百分比, 2=固定金额, 3=免运费 |
| `coupon_source` | 优惠来源 | 1=联盟同步, 2=手动添加, 3=用户提交 |
| `blog_post_type` | 文章类型 | 1=Deal优惠, 2=产品评测, 3=使用教程, 4=行业新闻 |
| `blog_post_status` | 文章状态 | 0=草稿, 1=待审核, 2=已发布, 3=已归档 |
| `campaign_type` | Campaign类型 | 1=套利, 2=自然流量 |
| `campaign_landing_page_type` | 落地页类型 | 1=内置页面, 2=外部链接 |
| `campaign_cost_source` | 成本来源 | 1=手动录入, 2=API同步 |
| `tracking_conversion_type` | 转化类型 | 1=Lead, 2=Sale, 3=Install, 4=Signup |
| `tracking_conversion_status` | 转化状态 | 0=待确认, 1=已确认, 2=已拒绝, 3=已撤销 |
| `tracking_attribution_type` | 归因类型 | 1=最后点击, 2=首次点击, 3=线性归因 |
| `stats_dimension_type` | 统计维度 | 1=Offer, 2=Campaign, 3=Source, 4=Merchant, 5=Category, 6=Author |

### 9.2 新增字典

| dict_type | 含义 | 值定义 |
|-----------|------|--------|
| `coupon_type` | 优惠券类型 | 1=优惠码, 2=促销, 3=Deal |
