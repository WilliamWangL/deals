# River 模块字典改造实施计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 将 River 模块中所有硬编码的下拉框选项改为使用系统字典，统一状态值定义，后端使用枚举替代魔法数字。

**Architecture:** 三层改造 - 后端枚举定义类型安全、数据库迁移同步状态值、前端组件使用字典 API。状态值统一为 0=开启/1=关闭。

**Tech Stack:** Java 17 枚举、PostgreSQL 迁移脚本、Vue 3 + Element Plus + TypeScript

**Design Reference:** `docs/plans/2026-01-17-river-dict-refactor-design.md`

---

## Phase 1: 后端枚举改造

### Task 1.1: 创建 PayoutModelEnum（计费模型枚举）

**Files:**
- Create: `river-server/river-module-affiliate/src/main/java/com/river/module/affiliate/enums/PayoutModelEnum.java`

**Step 1: 创建枚举文件**

```java
package com.river.module.affiliate.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 计费模型枚举（对应字典 affiliate_commission_type）
 * CPS: Cost Per Sale - 按销售付费
 * CPA: Cost Per Action - 按行动付费
 * CPC: Cost Per Click - 按点击付费
 * CPL: Cost Per Lead - 按潜在客户付费
 * CPM: Cost Per Mille - 按千次展示付费
 */
@Getter
@AllArgsConstructor
public enum PayoutModelEnum {

    CPS(1, "CPS (按销售)"),
    CPA(2, "CPA (按行动)"),
    CPC(3, "CPC (按点击)"),
    CPL(4, "CPL (按潜在客户)"),
    CPM(5, "CPM (按千次展示)");

    private final Integer code;
    private final String name;

    public static PayoutModelEnum getByCode(Integer code) {
        if (code == null) return CPS;
        for (PayoutModelEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return CPS;
    }

    /**
     * 从 Admitad API 类型字符串映射
     */
    public static PayoutModelEnum fromAdmitadType(String type) {
        if (type == null) return CPS;
        return switch (type.toLowerCase()) {
            case "sale" -> CPS;
            case "lead" -> CPA;
            case "click" -> CPC;
            default -> CPS;
        };
    }
}
```

**Step 2: 编译验证**

Run: `cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-server && mvn compile -pl river-module-affiliate -am -q`
Expected: BUILD SUCCESS

**Step 3: 提交**

```bash
git add river-server/river-module-affiliate/src/main/java/com/river/module/affiliate/enums/PayoutModelEnum.java
git commit -m "feat(affiliate): add PayoutModelEnum for payout model types"
```

---

### Task 1.2: 创建 CouponTypeEnum（优惠券类型枚举）

**Files:**
- Create: `river-server/river-module-coupon/src/main/java/com/river/module/coupon/enums/CouponTypeEnum.java`

**Step 1: 创建枚举文件**

```java
package com.river.module.coupon.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 优惠券类型枚举（对应字典 coupon_type）
 */
@Getter
@AllArgsConstructor
public enum CouponTypeEnum {

    PROMOCODE(1, "优惠码"),
    SALE(2, "促销"),
    DEAL(3, "Deal");

    private final Integer code;
    private final String name;

    public static CouponTypeEnum getByCode(Integer code) {
        if (code == null) return PROMOCODE;
        for (CouponTypeEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return PROMOCODE;
    }

    /**
     * 从 Admitad API species 字符串映射
     */
    public static CouponTypeEnum fromAdmitadSpecies(String species) {
        if (species == null) return PROMOCODE;
        return switch (species.toLowerCase()) {
            case "promocode" -> PROMOCODE;
            case "sale" -> SALE;
            default -> DEAL;
        };
    }
}
```

**Step 2: 编译验证**

Run: `cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-server && mvn compile -pl river-module-coupon -am -q`
Expected: BUILD SUCCESS

**Step 3: 提交**

```bash
git add river-server/river-module-coupon/src/main/java/com/river/module/coupon/enums/CouponTypeEnum.java
git commit -m "feat(coupon): add CouponTypeEnum for coupon types"
```

---

### Task 1.3: 改造 AdmitadSyncService 使用枚举

**Files:**
- Modify: `river-server/river-module-affiliate/src/main/java/com/river/module/affiliate/service/network/admitad/AdmitadSyncService.java`

**Step 1: 添加 import 语句**

在文件顶部 import 区域添加：

```java
import com.river.framework.common.enums.CommonStatusEnum;
import com.river.module.coupon.enums.CouponTypeEnum;
import com.river.module.coupon.enums.DiscountTypeEnum;
```

**Step 2: 改造 mapStatus 方法（约第 206-208 行）**

改造前：
```java
private Integer mapStatus(String status) {
    return "active".equalsIgnoreCase(status) ? 1 : 0;
}
```

改造后：
```java
private Integer mapStatus(String status) {
    return "active".equalsIgnoreCase(status)
        ? CommonStatusEnum.ENABLE.getStatus()
        : CommonStatusEnum.DISABLE.getStatus();
}
```

**Step 3: 改造 mapCommissionType 方法（约第 210-218 行）**

改造前：
```java
private Integer mapCommissionType(String type) {
    if (type == null) return 1;
    return switch (type.toLowerCase()) {
        case "sale" -> 1;
        case "lead" -> 2;
        case "click" -> 3;
        default -> 1;
    };
}
```

改造后：
```java
private Integer mapCommissionType(String type) {
    return PayoutModelEnum.fromAdmitadType(type).getCode();
}
```

**Step 4: 改造 createLocalCategory 方法中的 setStatus（约第 280 行）**

改造前：
```java
category.setStatus(1);
```

改造后：
```java
category.setStatus(CommonStatusEnum.ENABLE.getStatus());
```

**Step 5: 改造 updateCoupon 方法中的 setStatus（约第 387 行）**

改造前：
```java
coupon.setStatus(1); // 默认启用
```

改造后：
```java
coupon.setStatus(CommonStatusEnum.ENABLE.getStatus());
```

**Step 6: 改造 updateDeal 方法中的 setStatus（约第 453 行）**

改造前：
```java
deal.setStatus(1); // 默认启用
```

改造后：
```java
deal.setStatus(CommonStatusEnum.ENABLE.getStatus());
```

**Step 7: 改造 mapCouponType 方法（约第 496-502 行）**

改造前：
```java
private Integer mapCouponType(String species) {
    if (species == null) return 1;
    return switch (species.toLowerCase()) {
        case "promocode" -> 1;
        case "sale" -> 2;
        default -> 3; // deal
    };
}
```

改造后：
```java
private Integer mapCouponType(String species) {
    return CouponTypeEnum.fromAdmitadSpecies(species).getCode();
}
```

**Step 8: 改造 parseDiscount 方法中的 setDiscountType（约第 513, 523 行）**

改造前：
```java
coupon.setDiscountType(1); // 百分比
// ...
coupon.setDiscountType(2); // 固定金额
```

改造后：
```java
coupon.setDiscountType(DiscountTypeEnum.PERCENT.getCode());
// ...
coupon.setDiscountType(DiscountTypeEnum.FIXED.getCode());
```

**Step 9: 编译验证**

Run: `cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-server && mvn compile -pl river-module-affiliate -am -q`
Expected: BUILD SUCCESS

**Step 10: 提交**

```bash
git add river-server/river-module-affiliate/src/main/java/com/river/module/affiliate/service/network/admitad/AdmitadSyncService.java
git commit -m "refactor(affiliate): replace magic numbers with enums in AdmitadSyncService"
```

---

### Task 1.4: 删除旧的 CommissionTypeEnum

**Files:**
- Delete: `river-server/river-module-affiliate/src/main/java/com/river/module/affiliate/enums/CommissionTypeEnum.java`

**Step 1: 检查引用**

Run: `cd /Users/apple/Projects/shixiaohe/river-ad-workspace && grep -r "CommissionTypeEnum" river-server --include="*.java" | grep -v "^Binary"`
Expected: 仅在 CommissionTypeEnum.java 文件本身有引用，无其他引用

**Step 2: 删除文件**

```bash
rm river-server/river-module-affiliate/src/main/java/com/river/module/affiliate/enums/CommissionTypeEnum.java
```

**Step 3: 编译验证**

Run: `cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-server && mvn compile -pl river-module-affiliate -am -q`
Expected: BUILD SUCCESS

**Step 4: 提交**

```bash
git add -A
git commit -m "refactor(affiliate): remove obsolete CommissionTypeEnum (replaced by PayoutModelEnum)"
```

---

## Phase 2: 数据库迁移

### Task 2.1: 创建数据库迁移脚本

**Files:**
- Create: `river-server/sql/migrations/V2026.01.17__dict_and_status_fix.sql`

**Step 1: 创建迁移脚本**

```sql
-- =====================================================
-- River 模块字典改造迁移脚本
-- 1. 补充字典类型和数据
-- 2. 状态值反转（0↔1 互换）以匹配 COMMON_STATUS
-- 3. 修改列默认值
-- =====================================================

-- =====================================================
-- 1. 补充字典类型
-- =====================================================

-- 新增 coupon_type 字典类型
INSERT INTO system_dict_type (id, name, type, status, remark, creator, create_time, updater, update_time, deleted)
VALUES (20052, '优惠券类型', 'coupon_type', 0, '优惠券类型：优惠码、促销、Deal', '1', NOW(), '1', NOW(), 0)
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- 2. 补充字典数据
-- =====================================================

-- affiliate_commission_type 补充 CPL 和 CPM
INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted)
VALUES
(20007, 4, 'CPL (按潜在客户)', '4', 'affiliate_commission_type', 0, 'warning', '', '', '1', NOW(), '1', NOW(), 0),
(20008, 5, 'CPM (按千次展示)', '5', 'affiliate_commission_type', 0, 'info', '', '', '1', NOW(), '1', NOW(), 0)
ON CONFLICT (id) DO NOTHING;

-- coupon_type 字典数据
INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted)
VALUES
(20150, 1, '优惠码', '1', 'coupon_type', 0, 'primary', '', '', '1', NOW(), '1', NOW(), 0),
(20151, 2, '促销', '2', 'coupon_type', 0, 'success', '', '', '1', NOW(), '1', NOW(), 0),
(20152, 3, 'Deal', '3', 'coupon_type', 0, 'warning', '', '', '1', NOW(), '1', NOW(), 0)
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- 3. 状态值反转（0↔1 互换）
-- COMMON_STATUS: 0=开启, 1=关闭
-- River 原来: 1=开启, 0=关闭
-- =====================================================

-- Affiliate 模块
UPDATE river_affiliate_network SET status = CASE WHEN status = 1 THEN 0 WHEN status = 0 THEN 1 ELSE status END WHERE deleted = 0;
UPDATE river_affiliate_merchant SET status = CASE WHEN status = 1 THEN 0 WHEN status = 0 THEN 1 ELSE status END WHERE deleted = 0;
UPDATE river_affiliate_offer SET status = CASE WHEN status = 1 THEN 0 WHEN status = 0 THEN 1 ELSE status END WHERE deleted = 0;
UPDATE river_affiliate_category SET status = CASE WHEN status = 1 THEN 0 WHEN status = 0 THEN 1 ELSE status END WHERE deleted = 0;

-- Coupon 模块
UPDATE river_coupon_coupon SET status = CASE WHEN status = 1 THEN 0 WHEN status = 0 THEN 1 ELSE status END WHERE deleted = 0;
UPDATE river_coupon_deal SET status = CASE WHEN status = 1 THEN 0 WHEN status = 0 THEN 1 ELSE status END WHERE deleted = 0;

-- Blog 模块
UPDATE river_blog_author SET status = CASE WHEN status = 1 THEN 0 WHEN status = 0 THEN 1 ELSE status END WHERE deleted = 0;
UPDATE river_blog_tag SET status = CASE WHEN status = 1 THEN 0 WHEN status = 0 THEN 1 ELSE status END WHERE deleted = 0;
-- blog_post status 是发布状态，不是开启/关闭，不需要反转

-- Campaign 模块
UPDATE river_campaign_traffic_source SET status = CASE WHEN status = 1 THEN 0 WHEN status = 0 THEN 1 ELSE status END WHERE deleted = 0;
UPDATE river_campaign_campaign SET status = CASE WHEN status = 1 THEN 0 WHEN status = 0 THEN 1 ELSE status END WHERE deleted = 0;
UPDATE river_campaign_ad_group SET status = CASE WHEN status = 1 THEN 0 WHEN status = 0 THEN 1 ELSE status END WHERE deleted = 0;
UPDATE river_campaign_landing_page SET status = CASE WHEN status = 1 THEN 0 WHEN status = 0 THEN 1 ELSE status END WHERE deleted = 0;
UPDATE river_campaign_currency SET status = CASE WHEN status = 1 THEN 0 WHEN status = 0 THEN 1 ELSE status END WHERE deleted = 0;

-- Tracking 模块
UPDATE river_tracking_link SET status = CASE WHEN status = 1 THEN 0 WHEN status = 0 THEN 1 ELSE status END WHERE deleted = 0;

-- =====================================================
-- 4. 修改列默认值
-- 新记录默认 status = 0（开启）
-- =====================================================

-- Affiliate 模块
ALTER TABLE river_affiliate_network ALTER COLUMN status SET DEFAULT 0;
ALTER TABLE river_affiliate_merchant ALTER COLUMN status SET DEFAULT 0;
ALTER TABLE river_affiliate_offer ALTER COLUMN status SET DEFAULT 0;
ALTER TABLE river_affiliate_category ALTER COLUMN status SET DEFAULT 0;

-- Coupon 模块
ALTER TABLE river_coupon_coupon ALTER COLUMN status SET DEFAULT 0;
ALTER TABLE river_coupon_deal ALTER COLUMN status SET DEFAULT 0;

-- Blog 模块
ALTER TABLE river_blog_author ALTER COLUMN status SET DEFAULT 0;
ALTER TABLE river_blog_tag ALTER COLUMN status SET DEFAULT 0;

-- Campaign 模块
ALTER TABLE river_campaign_traffic_source ALTER COLUMN status SET DEFAULT 0;
ALTER TABLE river_campaign_campaign ALTER COLUMN status SET DEFAULT 0;
ALTER TABLE river_campaign_ad_group ALTER COLUMN status SET DEFAULT 0;
ALTER TABLE river_campaign_landing_page ALTER COLUMN status SET DEFAULT 0;
ALTER TABLE river_campaign_currency ALTER COLUMN status SET DEFAULT 0;

-- Tracking 模块
ALTER TABLE river_tracking_link ALTER COLUMN status SET DEFAULT 0;
```

**Step 2: 提交迁移脚本**

```bash
git add river-server/sql/migrations/V2026.01.17__dict_and_status_fix.sql
git commit -m "feat(db): add migration for dict data and status value fix"
```

---

### Task 2.2: 执行数据库迁移

**Step 1: 执行迁移脚本**

Run: `psql -h localhost -U postgres -d river -f /Users/apple/Projects/shixiaohe/river-ad-workspace/river-server/sql/migrations/V2026.01.17__dict_and_status_fix.sql`
Expected: 所有语句执行成功，无错误

**Step 2: 验证字典数据**

Run: `psql -h localhost -U postgres -d river -c "SELECT id, label, value, dict_type FROM system_dict_data WHERE dict_type = 'coupon_type' ORDER BY sort;"`
Expected: 显示 3 条记录（优惠码、促销、Deal）

**Step 3: 验证状态值**

Run: `psql -h localhost -U postgres -d river -c "SELECT status, COUNT(*) FROM river_affiliate_network WHERE deleted = 0 GROUP BY status;"`
Expected: status=0 表示开启的记录

---

### Task 2.3: 更新 SQL 建表脚本默认值

**Files:**
- Modify: `river-server/sql/postgresql/affiliate/river_affiliate.sql`
- Modify: `river-server/sql/postgresql/coupon/river_coupon.sql`
- Modify: `river-server/sql/postgresql/blog/river_blog.sql`
- Modify: `river-server/sql/postgresql/campaign/river_campaign.sql`
- Modify: `river-server/sql/postgresql/tracking/river_tracking.sql`

**Step 1: 修改 affiliate SQL 文件**

将所有 `status int2 NOT NULL DEFAULT 1` 改为 `status int2 NOT NULL DEFAULT 0`

**Step 2: 修改 coupon SQL 文件**

将所有 `status int2 NOT NULL DEFAULT 1` 改为 `status int2 NOT NULL DEFAULT 0`

**Step 3: 修改 blog SQL 文件**

将 author 和 tag 表的 `status int2 NOT NULL DEFAULT 1` 改为 `status int2 NOT NULL DEFAULT 0`
（post 表的 status 是发布状态，保持不变）

**Step 4: 修改 campaign SQL 文件**

将所有 `status int2 NOT NULL DEFAULT 1` 改为 `status int2 NOT NULL DEFAULT 0`

**Step 5: 修改 tracking SQL 文件**

将 tracking_link 表的 `status int2 NOT NULL DEFAULT 1` 改为 `status int2 NOT NULL DEFAULT 0`

**Step 6: 提交**

```bash
git add river-server/sql/postgresql/
git commit -m "fix(sql): change status default from 1 to 0 to match COMMON_STATUS"
```

---

## Phase 3: 前端改造

### Task 3.1: 扩展 DICT_TYPE 枚举

**Files:**
- Modify: `river-ui-admin/src/utils/dict.ts`

**Step 1: 在 DICT_TYPE 枚举中添加 River 模块字典类型**

在约第 250 行（`COMMON_STATUS` 附近）之前添加：

```typescript
// ========== RIVER - 联盟营销模块 ==========
AFFILIATE_NETWORK_TYPE = 'affiliate_network_type',
AFFILIATE_COMMISSION_TYPE = 'affiliate_commission_type',
AFFILIATE_AUTH_TYPE = 'affiliate_auth_type',
AFFILIATE_OFFER_STATUS = 'affiliate_offer_status',

// ========== RIVER - 优惠券模块 ==========
COUPON_DISCOUNT_TYPE = 'coupon_discount_type',
COUPON_SOURCE = 'coupon_source',
COUPON_TYPE = 'coupon_type',

// ========== RIVER - 博客模块 ==========
BLOG_POST_TYPE = 'blog_post_type',
BLOG_POST_STATUS = 'blog_post_status',

// ========== RIVER - Campaign模块 ==========
CAMPAIGN_TYPE = 'campaign_type',
CAMPAIGN_LANDING_PAGE_TYPE = 'campaign_landing_page_type',
CAMPAIGN_COST_SOURCE = 'campaign_cost_source',

// ========== RIVER - 追踪模块 ==========
TRACKING_CONVERSION_TYPE = 'tracking_conversion_type',
TRACKING_CONVERSION_STATUS = 'tracking_conversion_status',
TRACKING_ATTRIBUTION_TYPE = 'tracking_attribution_type',

// ========== RIVER - 统计模块 ==========
STATS_DIMENSION_TYPE = 'stats_dimension_type',
```

**Step 2: 类型检查**

Run: `cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-ui-admin && pnpm ts:check`
Expected: 无类型错误

**Step 3: 提交**

```bash
git add river-ui-admin/src/utils/dict.ts
git commit -m "feat(ui): add River module dict types to DICT_TYPE enum"
```

---

### Task 3.2: 改造 Affiliate 模块前端

**Files:**
- Modify: `river-ui-admin/src/views/river/affiliate/network/index.vue`
- Modify: `river-ui-admin/src/views/river/affiliate/network/AffiliateNetworkForm.vue`
- Modify: `river-ui-admin/src/views/river/affiliate/merchant/index.vue`
- Modify: `river-ui-admin/src/views/river/affiliate/merchant/MerchantForm.vue`
- Modify: `river-ui-admin/src/views/river/affiliate/category/index.vue`
- Modify: `river-ui-admin/src/views/river/affiliate/category/CategoryForm.vue`
- Modify: `river-ui-admin/src/views/river/affiliate/offer/index.vue`
- Modify: `river-ui-admin/src/views/river/affiliate/offer/OfferForm.vue`

**改造模式 - 下拉框：**

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

**改造模式 - 表格列：**

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

**Step 1: 确保每个文件导入必要的依赖**

```typescript
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
```

**Step 2: 逐个文件改造**

按照上述模式，将每个文件中的硬编码选项改为使用字典。

**Step 3: 类型检查**

Run: `cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-ui-admin && pnpm ts:check`
Expected: 无类型错误

**Step 4: 提交**

```bash
git add river-ui-admin/src/views/river/affiliate/
git commit -m "refactor(ui): replace hardcoded options with dict in affiliate module"
```

---

### Task 3.3: 改造 Coupon 模块前端

**Files:**
- Modify: `river-ui-admin/src/views/river/coupon/coupon/index.vue`
- Modify: `river-ui-admin/src/views/river/coupon/coupon/CouponForm.vue`
- Modify: `river-ui-admin/src/views/river/coupon/deal/index.vue`
- Modify: `river-ui-admin/src/views/river/coupon/deal/DealForm.vue`

**改造字段：**
- status → DICT_TYPE.COMMON_STATUS
- discountType → DICT_TYPE.COUPON_DISCOUNT_TYPE
- source → DICT_TYPE.COUPON_SOURCE
- couponType → DICT_TYPE.COUPON_TYPE

**Step 1: 逐个文件改造**

**Step 2: 类型检查**

Run: `cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-ui-admin && pnpm ts:check`
Expected: 无类型错误

**Step 3: 提交**

```bash
git add river-ui-admin/src/views/river/coupon/
git commit -m "refactor(ui): replace hardcoded options with dict in coupon module"
```

---

### Task 3.4: 改造 Blog 模块前端

**Files:**
- Modify: `river-ui-admin/src/views/river/blog/author/index.vue`
- Modify: `river-ui-admin/src/views/river/blog/tag/index.vue`
- Modify: `river-ui-admin/src/views/river/blog/post/index.vue`
- Modify: `river-ui-admin/src/views/river/blog/post/PostForm.vue`

**改造字段：**
- author/tag 的 status → DICT_TYPE.COMMON_STATUS
- post 的 status → DICT_TYPE.BLOG_POST_STATUS
- post 的 type → DICT_TYPE.BLOG_POST_TYPE

**Step 1: 逐个文件改造**

**Step 2: 类型检查**

Run: `cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-ui-admin && pnpm ts:check`
Expected: 无类型错误

**Step 3: 提交**

```bash
git add river-ui-admin/src/views/river/blog/
git commit -m "refactor(ui): replace hardcoded options with dict in blog module"
```

---

### Task 3.5: 改造 Campaign 模块前端

**Files:**
- Modify: `river-ui-admin/src/views/river/campaign/traffic-source/index.vue`
- Modify: `river-ui-admin/src/views/river/campaign/campaign/index.vue`
- Modify: `river-ui-admin/src/views/river/campaign/campaign/CampaignForm.vue`
- Modify: `river-ui-admin/src/views/river/campaign/ad-group/index.vue`
- Modify: `river-ui-admin/src/views/river/campaign/landing-page/index.vue`
- Modify: `river-ui-admin/src/views/river/campaign/landing-page/LandingPageForm.vue`
- Modify: `river-ui-admin/src/views/river/campaign/currency/index.vue`

**改造字段：**
- status → DICT_TYPE.COMMON_STATUS
- campaign type → DICT_TYPE.CAMPAIGN_TYPE
- landing page type → DICT_TYPE.CAMPAIGN_LANDING_PAGE_TYPE

**Step 1: 逐个文件改造**

**Step 2: 类型检查**

Run: `cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-ui-admin && pnpm ts:check`
Expected: 无类型错误

**Step 3: 提交**

```bash
git add river-ui-admin/src/views/river/campaign/
git commit -m "refactor(ui): replace hardcoded options with dict in campaign module"
```

---

### Task 3.6: 改造 Tracking 模块前端

**Files:**
- Modify: `river-ui-admin/src/views/river/tracking/tracking-link/index.vue`
- Modify: `river-ui-admin/src/views/river/tracking/conversion/index.vue`
- Modify: `river-ui-admin/src/views/river/tracking/conversion/ConversionForm.vue`
- Modify: `river-ui-admin/src/views/river/tracking/attribution/index.vue`

**改造字段：**
- tracking link status → DICT_TYPE.COMMON_STATUS
- conversion type → DICT_TYPE.TRACKING_CONVERSION_TYPE
- conversion status → DICT_TYPE.TRACKING_CONVERSION_STATUS
- attribution type → DICT_TYPE.TRACKING_ATTRIBUTION_TYPE

**Step 1: 逐个文件改造**

**Step 2: 类型检查**

Run: `cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-ui-admin && pnpm ts:check`
Expected: 无类型错误

**Step 3: 提交**

```bash
git add river-ui-admin/src/views/river/tracking/
git commit -m "refactor(ui): replace hardcoded options with dict in tracking module"
```

---

### Task 3.7: 改造 Stats 模块前端

**Files:**
- Modify: `river-ui-admin/src/views/river/stats/daily/index.vue`
- Modify: `river-ui-admin/src/views/river/stats/hourly/index.vue`

**改造字段：**
- dimension_type → DICT_TYPE.STATS_DIMENSION_TYPE

**Step 1: 逐个文件改造**

**Step 2: 类型检查**

Run: `cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-ui-admin && pnpm ts:check`
Expected: 无类型错误

**Step 3: 提交**

```bash
git add river-ui-admin/src/views/river/stats/
git commit -m "refactor(ui): replace hardcoded options with dict in stats module"
```

---

## Phase 4: 验证

### Task 4.1: 后端编译验证

**Step 1: 完整编译**

Run: `cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-server && mvn clean compile`
Expected: BUILD SUCCESS

---

### Task 4.2: 前端类型检查

**Step 1: 类型检查**

Run: `cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-ui-admin && pnpm ts:check`
Expected: 无类型错误

---

### Task 4.3: 功能验证

**Step 1: 启动后端服务**

Run: `cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-server/river-server && mvn spring-boot:run`

**Step 2: 启动前端服务**

Run: `cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-ui-admin && pnpm dev`

**Step 3: 手动验证**

1. 登录管理后台
2. 检查各模块下拉框选项是否正确显示
3. 检查表格状态标签颜色是否正确
4. 创建新记录，检查默认状态是否为"开启"
5. 触发 Admitad 同步，检查同步数据的 status 值

---

## 回滚方案

如果出现问题，执行回滚 SQL：

```sql
-- 回滚状态值
UPDATE river_affiliate_network SET status = CASE WHEN status = 0 THEN 1 WHEN status = 1 THEN 0 ELSE status END WHERE deleted = 0;
UPDATE river_affiliate_merchant SET status = CASE WHEN status = 0 THEN 1 WHEN status = 1 THEN 0 ELSE status END WHERE deleted = 0;
UPDATE river_affiliate_offer SET status = CASE WHEN status = 0 THEN 1 WHEN status = 1 THEN 0 ELSE status END WHERE deleted = 0;
UPDATE river_affiliate_category SET status = CASE WHEN status = 0 THEN 1 WHEN status = 1 THEN 0 ELSE status END WHERE deleted = 0;
UPDATE river_coupon_coupon SET status = CASE WHEN status = 0 THEN 1 WHEN status = 1 THEN 0 ELSE status END WHERE deleted = 0;
UPDATE river_coupon_deal SET status = CASE WHEN status = 0 THEN 1 WHEN status = 1 THEN 0 ELSE status END WHERE deleted = 0;
UPDATE river_blog_author SET status = CASE WHEN status = 0 THEN 1 WHEN status = 1 THEN 0 ELSE status END WHERE deleted = 0;
UPDATE river_blog_tag SET status = CASE WHEN status = 0 THEN 1 WHEN status = 1 THEN 0 ELSE status END WHERE deleted = 0;
UPDATE river_campaign_traffic_source SET status = CASE WHEN status = 0 THEN 1 WHEN status = 1 THEN 0 ELSE status END WHERE deleted = 0;
UPDATE river_campaign_campaign SET status = CASE WHEN status = 0 THEN 1 WHEN status = 1 THEN 0 ELSE status END WHERE deleted = 0;
UPDATE river_campaign_ad_group SET status = CASE WHEN status = 0 THEN 1 WHEN status = 1 THEN 0 ELSE status END WHERE deleted = 0;
UPDATE river_campaign_landing_page SET status = CASE WHEN status = 0 THEN 1 WHEN status = 1 THEN 0 ELSE status END WHERE deleted = 0;
UPDATE river_campaign_currency SET status = CASE WHEN status = 0 THEN 1 WHEN status = 1 THEN 0 ELSE status END WHERE deleted = 0;
UPDATE river_tracking_link SET status = CASE WHEN status = 0 THEN 1 WHEN status = 1 THEN 0 ELSE status END WHERE deleted = 0;

-- 回滚默认值
ALTER TABLE river_affiliate_network ALTER COLUMN status SET DEFAULT 1;
ALTER TABLE river_affiliate_merchant ALTER COLUMN status SET DEFAULT 1;
ALTER TABLE river_affiliate_offer ALTER COLUMN status SET DEFAULT 1;
ALTER TABLE river_affiliate_category ALTER COLUMN status SET DEFAULT 1;
ALTER TABLE river_coupon_coupon ALTER COLUMN status SET DEFAULT 1;
ALTER TABLE river_coupon_deal ALTER COLUMN status SET DEFAULT 1;
ALTER TABLE river_blog_author ALTER COLUMN status SET DEFAULT 1;
ALTER TABLE river_blog_tag ALTER COLUMN status SET DEFAULT 1;
ALTER TABLE river_campaign_traffic_source ALTER COLUMN status SET DEFAULT 1;
ALTER TABLE river_campaign_campaign ALTER COLUMN status SET DEFAULT 1;
ALTER TABLE river_campaign_ad_group ALTER COLUMN status SET DEFAULT 1;
ALTER TABLE river_campaign_landing_page ALTER COLUMN status SET DEFAULT 1;
ALTER TABLE river_campaign_currency ALTER COLUMN status SET DEFAULT 1;
ALTER TABLE river_tracking_link ALTER COLUMN status SET DEFAULT 1;

-- 删除新增字典
DELETE FROM system_dict_data WHERE id IN (20007, 20008, 20150, 20151, 20152);
DELETE FROM system_dict_type WHERE id = 20052;
```
