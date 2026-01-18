# StatsAggregationJob 聚合逻辑实现计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 实现 StatsAggregationJob 的日报统计聚合逻辑，从 click/conversion/cost 表聚合数据到 river_stats_daily 表

**Architecture:** 采用"分表查询 + 内存拼接"模式，每个维度独立事务，支持重跑的 UPSERT 模式

**Tech Stack:** Spring Boot 3.5, MyBatis Plus, PostgreSQL 17

---

## 背景

### 数据源表

| 表名 | 用途 | 关键字段 |
|------|------|---------|
| river_tracking_click | 点击记录 | click_id, offer_id, campaign_id, landing_page_id, click_time |
| river_tracking_conversion | 转化记录 | id, click_id, commission, conversion_time |
| river_campaign_cost_record | 成本记录 | campaign_id, date, cost |
| river_affiliate_offer | Offer信息 | id, merchant_id, category_ids |
| river_campaign_campaign | Campaign信息 | id, traffic_source_id |

### 目标表

`river_stats_daily`: date, dimension_type, dimension_id, dimension_name, clicks, conversions, revenue, cost, profit, epc, cr, roi

### 维度类型

| 维度 | dimension_type | 数据来源 |
|------|---------------|---------|
| Campaign | 1 | click.campaign_id |
| Source | 2 | click.campaign_id → campaign.traffic_source_id |
| Offer | 3 | click.offer_id |
| LandingPage | 4 | click.landing_page_id |
| Merchant | 5 | click.offer_id → offer.merchant_id |
| Category | 6 | click.offer_id → offer.category_ids[0] (仅首分类) |

### 派生指标计算

- `profit = revenue - cost`
- `epc = revenue / clicks`
- `cr = conversions / clicks * 100`
- `roi = profit / cost * 100`

注：cost 仅 Campaign 维度有直接数据，其他维度 cost 为 0

---

## Task 1: 创建聚合 DTO 类

**Files:**
- Create: `river-server/river-module-stats/src/main/java/com/river/module/stats/service/dto/DimensionAggregateDTO.java`

**Step 1: 创建 DTO**

```java
package com.river.module.stats.service.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DimensionAggregateDTO {
    private Long dimensionId;
    private String dimensionName;
    private Integer clicks;
    private Integer conversions;
    private BigDecimal revenue;
}
```

**Step 2: 验证编译**

Run: `cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-server && mvn compile -pl river-module-stats -am`

---

## Task 2: 扩展 ClickMapper 添加聚合查询

**Files:**
- Modify: `river-server/river-module-tracking/src/main/java/com/river/module/tracking/dal/mysql/ClickMapper.java`

**Step 1: 添加按 Campaign 聚合方法**

```java
/**
 * 按 Campaign 维度聚合指定日期的点击数
 */
default List<Map<String, Object>> selectClicksGroupByCampaign(LocalDate date) {
    QueryWrapper<ClickDO> wrapper = new QueryWrapper<>();
    wrapper.select("campaign_id as dimensionId", "COUNT(*) as clicks")
            .apply("DATE(click_time) = {0}", date)
            .isNotNull("campaign_id")
            .groupBy("campaign_id");
    return selectMaps(wrapper);
}
```

**Step 2: 添加按 Offer 聚合方法**

```java
/**
 * 按 Offer 维度聚合指定日期的点击数
 */
default List<Map<String, Object>> selectClicksGroupByOffer(LocalDate date) {
    QueryWrapper<ClickDO> wrapper = new QueryWrapper<>();
    wrapper.select("offer_id as dimensionId", "COUNT(*) as clicks")
            .apply("DATE(click_time) = {0}", date)
            .isNotNull("offer_id")
            .groupBy("offer_id");
    return selectMaps(wrapper);
}
```

**Step 3: 添加按 LandingPage 聚合方法**

```java
/**
 * 按 LandingPage 维度聚合指定日期的点击数
 */
default List<Map<String, Object>> selectClicksGroupByLandingPage(LocalDate date) {
    QueryWrapper<ClickDO> wrapper = new QueryWrapper<>();
    wrapper.select("landing_page_id as dimensionId", "COUNT(*) as clicks")
            .apply("DATE(click_time) = {0}", date)
            .isNotNull("landing_page_id")
            .groupBy("landing_page_id");
    return selectMaps(wrapper);
}
```

**Step 4: 验证编译**

Run: `cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-server && mvn compile -pl river-module-tracking -am`

---

## Task 3: 扩展 ConversionMapper 添加聚合查询

**Files:**
- Modify: `river-server/river-module-tracking/src/main/java/com/river/module/tracking/dal/mysql/ConversionMapper.java`

**Step 1: 添加按 ClickId 聚合转化方法**

```java
/**
 * 按 ClickId 聚合指定日期的转化数据
 * 返回 clickId -> (conversions, revenue)
 */
default List<Map<String, Object>> selectConversionsGroupByClickId(LocalDate date) {
    QueryWrapper<ConversionDO> wrapper = new QueryWrapper<>();
    wrapper.select("click_id as clickId",
                   "COUNT(*) as conversions",
                   "COALESCE(SUM(commission), 0) as revenue")
            .apply("DATE(conversion_time) = {0}", date)
            .isNotNull("click_id")
            .groupBy("click_id");
    return selectMaps(wrapper);
}
```

**Step 2: 验证编译**

Run: `cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-server && mvn compile -pl river-module-tracking -am`

---

## Task 4: 扩展 ClickMapper 添加批量查询 Click 方法

**Files:**
- Modify: `river-server/river-module-tracking/src/main/java/com/river/module/tracking/dal/mysql/ClickMapper.java`

**Step 1: 添加批量查询方法（用于转化关联）**

```java
/**
 * 根据 clickIds 批量查询 Click 记录
 */
default List<ClickDO> selectByClickIds(Collection<String> clickIds) {
    if (CollUtil.isEmpty(clickIds)) {
        return Collections.emptyList();
    }
    return selectList(new LambdaQueryWrapperX<ClickDO>()
            .in(ClickDO::getClickId, clickIds));
}
```

**Step 2: 验证编译**

Run: `cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-server && mvn compile -pl river-module-tracking -am`

---

## Task 5: 扩展 CostRecordMapper 添加按日期聚合

**Files:**
- Modify: `river-server/river-module-campaign/river-module-campaign-biz/src/main/java/com/river/module/campaign/dal/mysql/cost/CostRecordMapper.java`

**Step 1: 添加按 Campaign 聚合成本方法**

```java
/**
 * 按 Campaign 聚合指定日期的成本
 */
default Map<Long, BigDecimal> selectCostGroupByCampaign(LocalDate date) {
    List<CostRecordDO> costs = selectList(new LambdaQueryWrapperX<CostRecordDO>()
            .eq(CostRecordDO::getDate, date));
    return costs.stream()
            .collect(Collectors.toMap(
                    CostRecordDO::getCampaignId,
                    CostRecordDO::getCost,
                    BigDecimal::add));
}
```

**Step 2: 验证编译**

Run: `cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-server && mvn compile -pl river-module-campaign/river-module-campaign-biz -am`

---

## Task 6: 扩展 DailyStatsMapper 添加 UPSERT 方法

**Files:**
- Modify: `river-server/river-module-stats/src/main/java/com/river/module/stats/dal/mysql/DailyStatsMapper.java`

**Step 1: 添加 upsertStats 方法**

```java
/**
 * 插入或更新日报统计
 * 根据 date + dimensionType + dimensionId 唯一键判断
 */
default void upsertStats(DailyStatsDO stats) {
    DailyStatsDO existing = selectOne(new LambdaQueryWrapperX<DailyStatsDO>()
            .eq(DailyStatsDO::getDate, stats.getDate())
            .eq(DailyStatsDO::getDimensionType, stats.getDimensionType())
            .eq(DailyStatsDO::getDimensionId, stats.getDimensionId()));

    if (existing != null) {
        stats.setId(existing.getId());
        updateById(stats);
    } else {
        insert(stats);
    }
}
```

**Step 2: 验证编译**

Run: `cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-server && mvn compile -pl river-module-stats -am`

---

## Task 7: 实现 StatsAggregationJobImpl 核心逻辑

**Files:**
- Modify: `river-server/river-module-stats/src/main/java/com/river/module/stats/job/StatsAggregationJobImpl.java`

**Step 1: 添加依赖注入**

```java
@Resource
private ClickMapper clickMapper;
@Resource
private ConversionMapper conversionMapper;
@Resource
private CostRecordMapper costRecordMapper;
@Resource
private DailyStatsMapper dailyStatsMapper;
@Resource
private OfferService offerService;
@Resource
private CampaignService campaignService;
@Resource
private MerchantService merchantService;
@Resource
private CategoryService categoryService;
@Resource
private LandingPageService landingPageService;
@Resource
private TrafficSourceService trafficSourceService;
```

**Step 2: 实现主方法框架**

```java
@Override
public void aggregateDailyStats() {
    LocalDate yesterday = LocalDate.now().minusDays(1);
    log.info("[aggregateDailyStats][开始聚合 {} 的日报统计]", yesterday);

    // 各维度独立聚合，一个失败不影响其他
    tryAggregate(() -> aggregateCampaignDimension(yesterday), "Campaign");
    tryAggregate(() -> aggregateSourceDimension(yesterday), "Source");
    tryAggregate(() -> aggregateOfferDimension(yesterday), "Offer");
    tryAggregate(() -> aggregateLandingPageDimension(yesterday), "LandingPage");
    tryAggregate(() -> aggregateMerchantDimension(yesterday), "Merchant");
    tryAggregate(() -> aggregateCategoryDimension(yesterday), "Category");

    log.info("[aggregateDailyStats][聚合完成]");
}

private void tryAggregate(Runnable task, String dimension) {
    try {
        task.run();
        log.info("[aggregateDailyStats][{} 维度聚合成功]", dimension);
    } catch (Exception e) {
        log.error("[aggregateDailyStats][{} 维度聚合失败]", dimension, e);
    }
}
```

**Step 3: 实现派生指标计算**

```java
private void calculateDerivedMetrics(DailyStatsDO stats) {
    int clicks = stats.getClicks() != null ? stats.getClicks() : 0;
    int conversions = stats.getConversions() != null ? stats.getConversions() : 0;
    BigDecimal revenue = stats.getRevenue() != null ? stats.getRevenue() : BigDecimal.ZERO;
    BigDecimal cost = stats.getCost() != null ? stats.getCost() : BigDecimal.ZERO;

    // profit = revenue - cost
    stats.setProfit(revenue.subtract(cost));

    // epc = revenue / clicks
    if (clicks > 0) {
        stats.setEpc(revenue.divide(BigDecimal.valueOf(clicks), 4, RoundingMode.HALF_UP));
    } else {
        stats.setEpc(BigDecimal.ZERO);
    }

    // cr = conversions / clicks * 100
    if (clicks > 0) {
        stats.setCr(BigDecimal.valueOf(conversions * 100.0 / clicks)
                .setScale(2, RoundingMode.HALF_UP));
    } else {
        stats.setCr(BigDecimal.ZERO);
    }

    // roi = profit / cost * 100
    if (cost.compareTo(BigDecimal.ZERO) > 0) {
        stats.setRoi(stats.getProfit()
                .multiply(BigDecimal.valueOf(100))
                .divide(cost, 2, RoundingMode.HALF_UP));
    } else {
        stats.setRoi(BigDecimal.ZERO);
    }
}
```

**Step 4: 验证编译**

Run: `cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-server && mvn compile -pl river-module-stats -am`

---

## Task 8: 实现 Campaign 维度聚合

**Files:**
- Modify: `river-server/river-module-stats/src/main/java/com/river/module/stats/job/StatsAggregationJobImpl.java`

**Step 1: 实现 aggregateCampaignDimension**

```java
@Transactional(rollbackFor = Exception.class)
public void aggregateCampaignDimension(LocalDate date) {
    // 1. 聚合点击
    List<Map<String, Object>> clicksData = clickMapper.selectClicksGroupByCampaign(date);
    Map<Long, Integer> clicksMap = clicksData.stream()
            .collect(Collectors.toMap(
                    m -> ((Number) m.get("dimensionid")).longValue(),
                    m -> ((Number) m.get("clicks")).intValue(),
                    Integer::sum));

    // 2. 聚合转化 (先按 clickId，再关联到 campaign)
    Map<Long, Integer> conversionsMap = new HashMap<>();
    Map<Long, BigDecimal> revenueMap = new HashMap<>();
    aggregateConversionsToDimension(date, clicksMap.keySet(),
            ClickDO::getCampaignId, conversionsMap, revenueMap);

    // 3. 聚合成本 (Campaign 直接有成本数据)
    Map<Long, BigDecimal> costMap = costRecordMapper.selectCostGroupByCampaign(date);

    // 4. 获取维度名称
    Set<Long> allIds = new HashSet<>();
    allIds.addAll(clicksMap.keySet());
    allIds.addAll(conversionsMap.keySet());
    Map<Long, String> nameMap = campaignService.getCampaignMap(allIds).entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getName()));

    // 5. 保存
    saveStats(date, DimensionTypeEnum.CAMPAIGN.getType(),
              clicksMap, conversionsMap, revenueMap, costMap, nameMap);
}
```

**Step 2: 实现通用的转化聚合辅助方法**

```java
private <T> void aggregateConversionsToDimension(
        LocalDate date,
        Set<Long> dimensionIds,
        Function<ClickDO, Long> dimensionExtractor,
        Map<Long, Integer> conversionsMap,
        Map<Long, BigDecimal> revenueMap) {

    // 1. 获取当日所有转化，按 clickId 聚合
    List<Map<String, Object>> convData = conversionMapper.selectConversionsGroupByClickId(date);
    if (convData.isEmpty()) {
        return;
    }

    // 2. 提取 clickIds，批量查询 Click
    Set<String> clickIds = convData.stream()
            .map(m -> (String) m.get("clickid"))
            .collect(Collectors.toSet());
    Map<String, ClickDO> clickMap = clickMapper.selectByClickIds(clickIds).stream()
            .collect(Collectors.toMap(ClickDO::getClickId, Function.identity()));

    // 3. 转换到目标维度
    for (Map<String, Object> row : convData) {
        String clickId = (String) row.get("clickid");
        ClickDO click = clickMap.get(clickId);
        if (click == null) continue;

        Long dimensionId = dimensionExtractor.apply(click);
        if (dimensionId == null) continue;

        int convs = ((Number) row.get("conversions")).intValue();
        BigDecimal rev = (BigDecimal) row.get("revenue");

        conversionsMap.merge(dimensionId, convs, Integer::sum);
        revenueMap.merge(dimensionId, rev, BigDecimal::add);
    }
}
```

**Step 3: 实现通用保存方法**

```java
private void saveStats(LocalDate date, Integer dimensionType,
                       Map<Long, Integer> clicksMap,
                       Map<Long, Integer> conversionsMap,
                       Map<Long, BigDecimal> revenueMap,
                       Map<Long, BigDecimal> costMap,
                       Map<Long, String> nameMap) {

    Set<Long> allIds = new HashSet<>();
    allIds.addAll(clicksMap.keySet());
    allIds.addAll(conversionsMap.keySet());

    for (Long dimensionId : allIds) {
        DailyStatsDO stats = new DailyStatsDO();
        stats.setDate(date);
        stats.setDimensionType(dimensionType);
        stats.setDimensionId(dimensionId);
        stats.setDimensionName(nameMap.getOrDefault(dimensionId, ""));
        stats.setClicks(clicksMap.getOrDefault(dimensionId, 0));
        stats.setConversions(conversionsMap.getOrDefault(dimensionId, 0));
        stats.setRevenue(revenueMap.getOrDefault(dimensionId, BigDecimal.ZERO));
        stats.setCost(costMap != null ? costMap.getOrDefault(dimensionId, BigDecimal.ZERO) : BigDecimal.ZERO);

        calculateDerivedMetrics(stats);
        dailyStatsMapper.upsertStats(stats);
    }
}
```

**Step 4: 验证编译**

Run: `cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-server && mvn compile -pl river-module-stats -am`

---

## Task 9: 实现其他直接维度聚合 (Offer, LandingPage)

**Files:**
- Modify: `river-server/river-module-stats/src/main/java/com/river/module/stats/job/StatsAggregationJobImpl.java`

**Step 1: 实现 aggregateOfferDimension**

```java
@Transactional(rollbackFor = Exception.class)
public void aggregateOfferDimension(LocalDate date) {
    // 1. 聚合点击
    List<Map<String, Object>> clicksData = clickMapper.selectClicksGroupByOffer(date);
    Map<Long, Integer> clicksMap = clicksData.stream()
            .collect(Collectors.toMap(
                    m -> ((Number) m.get("dimensionid")).longValue(),
                    m -> ((Number) m.get("clicks")).intValue(),
                    Integer::sum));

    // 2. 聚合转化
    Map<Long, Integer> conversionsMap = new HashMap<>();
    Map<Long, BigDecimal> revenueMap = new HashMap<>();
    aggregateConversionsToDimension(date, clicksMap.keySet(),
            ClickDO::getOfferId, conversionsMap, revenueMap);

    // 3. 获取维度名称
    Set<Long> allIds = new HashSet<>();
    allIds.addAll(clicksMap.keySet());
    allIds.addAll(conversionsMap.keySet());
    Map<Long, String> nameMap = offerService.getOfferMap(allIds).entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getName()));

    // 4. 保存 (Offer 无直接成本)
    saveStats(date, DimensionTypeEnum.OFFER.getType(),
              clicksMap, conversionsMap, revenueMap, null, nameMap);
}
```

**Step 2: 实现 aggregateLandingPageDimension**

```java
@Transactional(rollbackFor = Exception.class)
public void aggregateLandingPageDimension(LocalDate date) {
    // 1. 聚合点击
    List<Map<String, Object>> clicksData = clickMapper.selectClicksGroupByLandingPage(date);
    Map<Long, Integer> clicksMap = clicksData.stream()
            .collect(Collectors.toMap(
                    m -> ((Number) m.get("dimensionid")).longValue(),
                    m -> ((Number) m.get("clicks")).intValue(),
                    Integer::sum));

    // 2. 聚合转化
    Map<Long, Integer> conversionsMap = new HashMap<>();
    Map<Long, BigDecimal> revenueMap = new HashMap<>();
    aggregateConversionsToDimension(date, clicksMap.keySet(),
            ClickDO::getLandingPageId, conversionsMap, revenueMap);

    // 3. 获取维度名称
    Set<Long> allIds = new HashSet<>();
    allIds.addAll(clicksMap.keySet());
    allIds.addAll(conversionsMap.keySet());
    Map<Long, String> nameMap = landingPageService.getLandingPageMap(allIds).entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getName()));

    // 4. 保存
    saveStats(date, DimensionTypeEnum.LANDING_PAGE.getType(),
              clicksMap, conversionsMap, revenueMap, null, nameMap);
}
```

**Step 3: 验证编译**

Run: `cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-server && mvn compile -pl river-module-stats -am`

---

## Task 10: 实现间接维度聚合 (Source, Merchant, Category)

**Files:**
- Modify: `river-server/river-module-stats/src/main/java/com/river/module/stats/job/StatsAggregationJobImpl.java`

**Step 1: 实现 aggregateSourceDimension (Campaign → TrafficSource)**

```java
@Transactional(rollbackFor = Exception.class)
public void aggregateSourceDimension(LocalDate date) {
    // 1. 先按 Campaign 聚合点击
    List<Map<String, Object>> clicksData = clickMapper.selectClicksGroupByCampaign(date);

    // 2. 获取 Campaign → TrafficSource 映射
    Set<Long> campaignIds = clicksData.stream()
            .map(m -> ((Number) m.get("dimensionid")).longValue())
            .collect(Collectors.toSet());
    Map<Long, CampaignDO> campaignMap = campaignService.getCampaignMap(campaignIds);

    // 3. 转换到 Source 维度
    Map<Long, Integer> clicksMap = new HashMap<>();
    for (Map<String, Object> row : clicksData) {
        Long campaignId = ((Number) row.get("dimensionid")).longValue();
        Integer clicks = ((Number) row.get("clicks")).intValue();
        CampaignDO campaign = campaignMap.get(campaignId);
        if (campaign != null && campaign.getTrafficSourceId() != null) {
            clicksMap.merge(campaign.getTrafficSourceId(), clicks, Integer::sum);
        }
    }

    // 4. 聚合转化 (通过 Campaign 转换)
    Map<Long, Integer> conversionsMap = new HashMap<>();
    Map<Long, BigDecimal> revenueMap = new HashMap<>();
    aggregateConversionsToSourceDimension(date, campaignMap, conversionsMap, revenueMap);

    // 5. 获取维度名称
    Set<Long> allIds = new HashSet<>();
    allIds.addAll(clicksMap.keySet());
    allIds.addAll(conversionsMap.keySet());
    Map<Long, String> nameMap = trafficSourceService.getTrafficSourceMap(allIds).entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getName()));

    // 6. 保存
    saveStats(date, DimensionTypeEnum.SOURCE.getType(),
              clicksMap, conversionsMap, revenueMap, null, nameMap);
}
```

**Step 2: 实现 aggregateMerchantDimension (Offer → Merchant)**

```java
@Transactional(rollbackFor = Exception.class)
public void aggregateMerchantDimension(LocalDate date) {
    // 1. 先按 Offer 聚合点击
    List<Map<String, Object>> clicksData = clickMapper.selectClicksGroupByOffer(date);

    // 2. 获取 Offer → Merchant 映射
    Set<Long> offerIds = clicksData.stream()
            .map(m -> ((Number) m.get("dimensionid")).longValue())
            .collect(Collectors.toSet());
    Map<Long, OfferDO> offerMap = offerService.getOfferMap(offerIds);

    // 3. 转换到 Merchant 维度
    Map<Long, Integer> clicksMap = new HashMap<>();
    for (Map<String, Object> row : clicksData) {
        Long offerId = ((Number) row.get("dimensionid")).longValue();
        Integer clicks = ((Number) row.get("clicks")).intValue();
        OfferDO offer = offerMap.get(offerId);
        if (offer != null && offer.getMerchantId() != null) {
            clicksMap.merge(offer.getMerchantId(), clicks, Integer::sum);
        }
    }

    // 4. 聚合转化
    Map<Long, Integer> conversionsMap = new HashMap<>();
    Map<Long, BigDecimal> revenueMap = new HashMap<>();
    aggregateConversionsToMerchantDimension(date, offerMap, conversionsMap, revenueMap);

    // 5. 获取维度名称
    Set<Long> allIds = new HashSet<>();
    allIds.addAll(clicksMap.keySet());
    allIds.addAll(conversionsMap.keySet());
    Map<Long, String> nameMap = merchantService.getMerchantMap(allIds).entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getName()));

    // 6. 保存
    saveStats(date, DimensionTypeEnum.MERCHANT.getType(),
              clicksMap, conversionsMap, revenueMap, null, nameMap);
}
```

**Step 3: 实现 aggregateCategoryDimension (Offer → Category[0])**

```java
@Transactional(rollbackFor = Exception.class)
public void aggregateCategoryDimension(LocalDate date) {
    // 1. 先按 Offer 聚合点击
    List<Map<String, Object>> clicksData = clickMapper.selectClicksGroupByOffer(date);

    // 2. 获取 Offer → Category 映射 (仅首分类)
    Set<Long> offerIds = clicksData.stream()
            .map(m -> ((Number) m.get("dimensionid")).longValue())
            .collect(Collectors.toSet());
    Map<Long, OfferDO> offerMap = offerService.getOfferMap(offerIds);

    // 3. 转换到 Category 维度 (仅首分类)
    Map<Long, Integer> clicksMap = new HashMap<>();
    for (Map<String, Object> row : clicksData) {
        Long offerId = ((Number) row.get("dimensionid")).longValue();
        Integer clicks = ((Number) row.get("clicks")).intValue();
        OfferDO offer = offerMap.get(offerId);
        if (offer != null && offer.getCategoryIds() != null && !offer.getCategoryIds().isEmpty()) {
            Long categoryId = offer.getCategoryIds().get(0);  // 仅首分类
            clicksMap.merge(categoryId, clicks, Integer::sum);
        }
    }

    // 4. 聚合转化
    Map<Long, Integer> conversionsMap = new HashMap<>();
    Map<Long, BigDecimal> revenueMap = new HashMap<>();
    aggregateConversionsToCategoryDimension(date, offerMap, conversionsMap, revenueMap);

    // 5. 获取维度名称
    Set<Long> allIds = new HashSet<>();
    allIds.addAll(clicksMap.keySet());
    allIds.addAll(conversionsMap.keySet());
    Map<Long, String> nameMap = categoryService.getCategoryMap(allIds).entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getName()));

    // 6. 保存
    saveStats(date, DimensionTypeEnum.CATEGORY.getType(),
              clicksMap, conversionsMap, revenueMap, null, nameMap);
}
```

**Step 4: 验证编译**

Run: `cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-server && mvn compile -pl river-module-stats -am`

---

## Task 11: 实现间接维度的转化聚合辅助方法

**Files:**
- Modify: `river-server/river-module-stats/src/main/java/com/river/module/stats/job/StatsAggregationJobImpl.java`

**Step 1: 实现 Source 维度转化聚合**

```java
private void aggregateConversionsToSourceDimension(
        LocalDate date,
        Map<Long, CampaignDO> campaignMap,
        Map<Long, Integer> conversionsMap,
        Map<Long, BigDecimal> revenueMap) {

    List<Map<String, Object>> convData = conversionMapper.selectConversionsGroupByClickId(date);
    if (convData.isEmpty()) return;

    Set<String> clickIds = convData.stream()
            .map(m -> (String) m.get("clickid"))
            .collect(Collectors.toSet());
    Map<String, ClickDO> clickMap = clickMapper.selectByClickIds(clickIds).stream()
            .collect(Collectors.toMap(ClickDO::getClickId, Function.identity()));

    for (Map<String, Object> row : convData) {
        String clickId = (String) row.get("clickid");
        ClickDO click = clickMap.get(clickId);
        if (click == null || click.getCampaignId() == null) continue;

        CampaignDO campaign = campaignMap.get(click.getCampaignId());
        if (campaign == null || campaign.getTrafficSourceId() == null) continue;

        Long sourceId = campaign.getTrafficSourceId();
        int convs = ((Number) row.get("conversions")).intValue();
        BigDecimal rev = (BigDecimal) row.get("revenue");

        conversionsMap.merge(sourceId, convs, Integer::sum);
        revenueMap.merge(sourceId, rev, BigDecimal::add);
    }
}
```

**Step 2: 实现 Merchant 维度转化聚合**

```java
private void aggregateConversionsToMerchantDimension(
        LocalDate date,
        Map<Long, OfferDO> offerMap,
        Map<Long, Integer> conversionsMap,
        Map<Long, BigDecimal> revenueMap) {

    List<Map<String, Object>> convData = conversionMapper.selectConversionsGroupByClickId(date);
    if (convData.isEmpty()) return;

    Set<String> clickIds = convData.stream()
            .map(m -> (String) m.get("clickid"))
            .collect(Collectors.toSet());
    Map<String, ClickDO> clickMap = clickMapper.selectByClickIds(clickIds).stream()
            .collect(Collectors.toMap(ClickDO::getClickId, Function.identity()));

    for (Map<String, Object> row : convData) {
        String clickId = (String) row.get("clickid");
        ClickDO click = clickMap.get(clickId);
        if (click == null || click.getOfferId() == null) continue;

        OfferDO offer = offerMap.get(click.getOfferId());
        if (offer == null || offer.getMerchantId() == null) continue;

        Long merchantId = offer.getMerchantId();
        int convs = ((Number) row.get("conversions")).intValue();
        BigDecimal rev = (BigDecimal) row.get("revenue");

        conversionsMap.merge(merchantId, convs, Integer::sum);
        revenueMap.merge(merchantId, rev, BigDecimal::add);
    }
}
```

**Step 3: 实现 Category 维度转化聚合**

```java
private void aggregateConversionsToCategoryDimension(
        LocalDate date,
        Map<Long, OfferDO> offerMap,
        Map<Long, Integer> conversionsMap,
        Map<Long, BigDecimal> revenueMap) {

    List<Map<String, Object>> convData = conversionMapper.selectConversionsGroupByClickId(date);
    if (convData.isEmpty()) return;

    Set<String> clickIds = convData.stream()
            .map(m -> (String) m.get("clickid"))
            .collect(Collectors.toSet());
    Map<String, ClickDO> clickMap = clickMapper.selectByClickIds(clickIds).stream()
            .collect(Collectors.toMap(ClickDO::getClickId, Function.identity()));

    for (Map<String, Object> row : convData) {
        String clickId = (String) row.get("clickid");
        ClickDO click = clickMap.get(clickId);
        if (click == null || click.getOfferId() == null) continue;

        OfferDO offer = offerMap.get(click.getOfferId());
        if (offer == null || offer.getCategoryIds() == null || offer.getCategoryIds().isEmpty()) continue;

        Long categoryId = offer.getCategoryIds().get(0);  // 仅首分类
        int convs = ((Number) row.get("conversions")).intValue();
        BigDecimal rev = (BigDecimal) row.get("revenue");

        conversionsMap.merge(categoryId, convs, Integer::sum);
        revenueMap.merge(categoryId, rev, BigDecimal::add);
    }
}
```

**Step 4: 验证编译**

Run: `cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-server && mvn compile -pl river-module-stats -am`

---

## Task 12: 添加缺失的 Service Map 方法

**Files:**
- 检查并补充各 Service 的 getXxxMap 方法

**Step 1: 检查各 Service 是否有 getXxxMap 方法**

需要确认以下 Service 存在对应的 Map 查询方法：
- `CampaignService.getCampaignMap(Collection<Long> ids)`
- `OfferService.getOfferMap(Collection<Long> ids)`
- `MerchantService.getMerchantMap(Collection<Long> ids)`
- `CategoryService.getCategoryMap(Collection<Long> ids)`
- `LandingPageService.getLandingPageMap(Collection<Long> ids)`
- `TrafficSourceService.getTrafficSourceMap(Collection<Long> ids)`

如果缺失，按照 DeptService.getDeptMap 模式添加：

```java
default Map<Long, XxxDO> getXxxMap(Collection<Long> ids) {
    if (CollUtil.isEmpty(ids)) {
        return Collections.emptyMap();
    }
    List<XxxDO> list = getXxxList(ids);
    return CollectionUtils.convertMap(list, XxxDO::getId);
}
```

**Step 2: 验证编译**

Run: `cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-server && mvn compile`

---

## Task 13: 编译验证与测试

**Step 1: 完整编译**

Run: `cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-server && mvn clean compile`

**Step 2: 启动服务器验证**

Run: `cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-server/river-server && mvn spring-boot:run`

**Step 3: 手动触发聚合任务测试**

通过后台管理界面或 API 调用触发定时任务执行，检查日志输出和数据库数据。

---

## 依赖关系

```
Task 1 (DTO)
    ↓
Task 2-5 (Mapper 扩展) - 可并行
    ↓
Task 6 (DailyStatsMapper UPSERT)
    ↓
Task 7 (核心框架)
    ↓
Task 8 (Campaign 维度)
    ↓
Task 9 (Offer, LandingPage 维度) - 可并行
    ↓
Task 10-11 (间接维度)
    ↓
Task 12 (补充 Service 方法)
    ↓
Task 13 (验证)
```
