# Phase 4: 流量投放与 ROI 模块实施计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 实现 river-module-campaign 模块，管理流量来源、广告计划、落地页，并实现 ROI 自动计算和成本管理。

**Architecture:**
- 新建 Maven 模块 river-module-campaign
- 核心实体：TrafficSource, Campaign, AdGroup, LandingPage, CostRecord
- 货币与汇率：Currency, FxRate
- 关联 affiliate 模块获取收入数据
- 自动计算 ROI = (Revenue - Cost) / Cost

**Tech Stack:** Java 17, Spring Boot 3.5, MyBatis-Plus, PostgreSQL, Scheduled Tasks

---

## Task 1: 创建 Maven 模块结构

**Files:**
- Create: `river-server/river-module-campaign/pom.xml`
- Create: `river-server/river-module-campaign/river-module-campaign-api/pom.xml`
- Create: `river-server/river-module-campaign/river-module-campaign-biz/pom.xml`
- Modify: `river-server/pom.xml`

**Step 1: 创建目录结构**
```bash
mkdir -p river-server/river-module-campaign/river-module-campaign-api/src/main/java/com/river/module/campaign
mkdir -p river-server/river-module-campaign/river-module-campaign-biz/src/main/java/com/river/module/campaign
```

**Step 2: 配置 pom.xml (参考 affiliate 模块)**
- 引入 `river-module-affiliate-api` 依赖以获取收入数据

---

## Task 2: 基础配置实体 (Currency & TrafficSource)

**Files:**
- Create: `river-module-campaign-biz/.../dal/dataobject/CurrencyDO.java`
- Create: `river-module-campaign-biz/.../dal/dataobject/FxRateDO.java`
- Create: `river-module-campaign-biz/.../dal/dataobject/TrafficSourceDO.java`

**Entities:**

1. **CurrencyDO**: ISO 货币代码 (USD, EUR, CNY), 符号, 小数位
2. **FxRateDO**: 汇率表, source_currency, target_currency, rate, date
3. **TrafficSourceDO**: 流量源 (Google Ads, Facebook, TikTok), Postback URL 模板, 成本参数名 (cpc, cost)

---

## Task 3: 投放核心实体 (Campaign & AdGroup)

**Files:**
- Create: `river-module-campaign-api/.../enums/CampaignTypeEnum.java`
- Create: `river-module-campaign-api/.../enums/CampaignStatusEnum.java`
- Create: `river-module-campaign-biz/.../dal/dataobject/CampaignDO.java`
- Create: `river-module-campaign-biz/.../dal/dataobject/AdGroupDO.java`

**Enums:**
- **CampaignTypeEnum**: SEARCH(1), DISPLAY(2), SOCIAL(3), NATIVE(4)
- **CampaignStatusEnum**: ACTIVE(1), PAUSED(2), ARCHIVED(3)

**Entities:**
1. **CampaignDO**:
   - `traffic_source_id`: 关联流量源
   - `name`: 计划名称
   - `budget`: 每日预算
   - `tracking_url`: 追踪链接
2. **AdGroupDO**:
   - `campaign_id`: 关联计划
   - `name`: 广告组名称
   - `targeting`: 定向信息 (JSON)

---

## Task 4: 落地页实体 (LandingPage)

**Files:**
- Create: `river-module-campaign-api/.../enums/LandingPageTypeEnum.java`
- Create: `river-module-campaign-biz/.../dal/dataobject/LandingPageDO.java`

**Enums:**
- **LandingPageTypeEnum**: PRELANDER(1), DIRECT_LINK(2), ADVERTORIAL(3)

**Entities:**
1. **LandingPageDO**:
   - `name`: 名称
   - `url`: 真实 URL
   - `type`: 类型
   - `offer_id`: 关联默认 Offer (可选)

---

## Task 5: 成本记录实体 (CostRecord)

**Files:**
- Create: `river-module-campaign-api/.../enums/CostSourceEnum.java`
- Create: `river-module-campaign-biz/.../dal/dataobject/CostRecordDO.java`

**Enums:**
- **CostSourceEnum**: API_SYNC(1), MANUAL_IMPORT(2), ESTIMATED(3)

**Entities:**
1. **CostRecordDO**:
   - `campaign_id`, `ad_group_id`: 维度
   - `date`: 日期
   - `cost`: 金额
   - `currency`: 货币
   - `impressions`, `clicks`: 展现点击数据
   - `source`: 数据来源

---

## Task 6: 数据库脚本

**Files:**
- Create: `river-server/sql/postgresql/campaign/river_campaign.sql`

**Content:**
- 建表语句 for: `river_campaign_currency`, `river_campaign_fx_rate`, `river_campaign_traffic_source`, `river_campaign`, `river_campaign_ad_group`, `river_campaign_landing_page`, `river_campaign_cost_record`
- 索引优化：`idx_cost_date_campaign` 等

---

## Task 7: Service & Logic 实现

**Files:**
- Create: `river-module-campaign-biz/.../service/roi/RoiCalculationService.java`

**Logic:**
1. **ROI 计算**:
   - 获取指定维度 (Campaign/AdGroup) 的 Cost (from CostRecord)
   - 获取同维度的 Revenue (from Affiliate Conversion)
   - 统一货币 (使用 FxRate)
   - ROI = (Revenue - Cost) / Cost * 100%
   - Profit = Revenue - Cost

---

## Task 8: Controllers

**Files:**
- Create: `river-module-campaign-biz/.../controller/admin/CampaignController.java`
- Create: `river-module-campaign-biz/.../controller/admin/CostController.java`
- Create: `river-module-campaign-biz/.../controller/admin/vo/CampaignRespVO.java` (包含 calculated ROI)

---

## Verification

**Build:**
```bash
mvn compile -pl river-module-campaign -am
```

**Test:**
- 单元测试 RoiCalculationService
- 验证汇率转换逻辑正确性
