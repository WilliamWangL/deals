package com.river.module.stats.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.river.module.affiliate.dal.dataobject.OfferDO;
import com.river.module.affiliate.dal.mysql.OfferMapper;
import com.river.module.campaign.dal.dataobject.CampaignDO;
import com.river.module.campaign.dal.mysql.CampaignMapper;
import com.river.module.campaign.dal.mysql.CostRecordMapper;
import com.river.module.stats.dal.dataobject.DailyStatsDO;
import com.river.module.stats.dal.mysql.DailyStatsMapper;
import com.river.module.stats.enums.DimensionTypeEnum;
import com.river.module.tracking.dal.dataobject.ClickDO;
import com.river.module.tracking.dal.mysql.ClickMapper;
import com.river.module.tracking.dal.mysql.ConversionMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class StatsAggregationJobImpl implements StatsAggregationJob {

    @Resource
    private ClickMapper clickMapper;
    @Resource
    private ConversionMapper conversionMapper;
    @Resource
    private CostRecordMapper costRecordMapper;
    @Resource
    private DailyStatsMapper dailyStatsMapper;
    @Resource
    private OfferMapper offerMapper;
    @Resource
    private CampaignMapper campaignMapper;

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

    // ==================== Campaign 维度 ====================

    @Transactional(rollbackFor = Exception.class)
    public void aggregateCampaignDimension(LocalDate date) {
        // 1. 聚合点击
        Map<Long, Integer> clicksMap = toClicksMap(clickMapper.selectClicksGroupByCampaign(date));

        // 2. 聚合转化
        Map<Long, Integer> conversionsMap = new HashMap<>();
        Map<Long, BigDecimal> revenueMap = new HashMap<>();
        aggregateConversionsToDimension(date, ClickDO::getCampaignId, conversionsMap, revenueMap);

        // 3. 聚合成本
        Map<Long, BigDecimal> costMap = costRecordMapper.selectCostGroupByCampaign(date);

        // 4. 保存
        saveStats(date, DimensionTypeEnum.CAMPAIGN.getType(),
                  clicksMap, conversionsMap, revenueMap, costMap);
    }

    // ==================== Offer 维度 ====================

    @Transactional(rollbackFor = Exception.class)
    public void aggregateOfferDimension(LocalDate date) {
        // 1. 聚合点击
        Map<Long, Integer> clicksMap = toClicksMap(clickMapper.selectClicksGroupByOffer(date));

        // 2. 聚合转化
        Map<Long, Integer> conversionsMap = new HashMap<>();
        Map<Long, BigDecimal> revenueMap = new HashMap<>();
        aggregateConversionsToDimension(date, ClickDO::getOfferId, conversionsMap, revenueMap);

        // 3. 保存
        saveStats(date, DimensionTypeEnum.OFFER.getType(),
                  clicksMap, conversionsMap, revenueMap, null);
    }

    // ==================== LandingPage 维度 ====================

    @Transactional(rollbackFor = Exception.class)
    public void aggregateLandingPageDimension(LocalDate date) {
        // 1. 聚合点击
        Map<Long, Integer> clicksMap = toClicksMap(clickMapper.selectClicksGroupByLandingPage(date));

        // 2. 聚合转化
        Map<Long, Integer> conversionsMap = new HashMap<>();
        Map<Long, BigDecimal> revenueMap = new HashMap<>();
        aggregateConversionsToDimension(date, ClickDO::getLandingPageId, conversionsMap, revenueMap);

        // 3. 保存
        saveStats(date, DimensionTypeEnum.LANDING_PAGE.getType(),
                  clicksMap, conversionsMap, revenueMap, null);
    }

    // ==================== Source 维度 (Campaign -> TrafficSource) ====================

    @Transactional(rollbackFor = Exception.class)
    public void aggregateSourceDimension(LocalDate date) {
        // 1. 获取按 Campaign 聚合的点击数据及 Campaign 映射
        List<Map<String, Object>> clicksData = clickMapper.selectClicksGroupByCampaign(date);
        Map<Long, CampaignDO> campaignMap = getCampaignMap(extractDimensionIds(clicksData));

        // 2. 转换到 Source 维度
        Map<Long, Integer> clicksMap = remapClicksToDimension(clicksData, campaignMap, CampaignDO::getTrafficSourceId);

        // 3. 聚合转化
        Map<Long, Integer> conversionsMap = new HashMap<>();
        Map<Long, BigDecimal> revenueMap = new HashMap<>();
        aggregateConversionsWithMapping(date, ClickDO::getCampaignId, campaignMap,
                CampaignDO::getTrafficSourceId, conversionsMap, revenueMap);

        // 4. 保存
        saveStats(date, DimensionTypeEnum.SOURCE.getType(),
                  clicksMap, conversionsMap, revenueMap, null);
    }

    // ==================== Merchant 维度 (Offer -> Merchant) ====================

    @Transactional(rollbackFor = Exception.class)
    public void aggregateMerchantDimension(LocalDate date) {
        // 1. 获取按 Offer 聚合的点击数据及 Offer 映射
        List<Map<String, Object>> clicksData = clickMapper.selectClicksGroupByOffer(date);
        Map<Long, OfferDO> offerMap = getOfferMap(extractDimensionIds(clicksData));

        // 2. 转换到 Merchant 维度
        Map<Long, Integer> clicksMap = remapClicksToDimension(clicksData, offerMap, OfferDO::getMerchantId);

        // 3. 聚合转化
        Map<Long, Integer> conversionsMap = new HashMap<>();
        Map<Long, BigDecimal> revenueMap = new HashMap<>();
        aggregateConversionsWithMapping(date, ClickDO::getOfferId, offerMap,
                OfferDO::getMerchantId, conversionsMap, revenueMap);

        // 4. 保存
        saveStats(date, DimensionTypeEnum.MERCHANT.getType(),
                  clicksMap, conversionsMap, revenueMap, null);
    }

    // ==================== Category 维度 (Offer -> Category[0]) ====================

    @Transactional(rollbackFor = Exception.class)
    public void aggregateCategoryDimension(LocalDate date) {
        // 1. 获取按 Offer 聚合的点击数据及 Offer 映射
        List<Map<String, Object>> clicksData = clickMapper.selectClicksGroupByOffer(date);
        Map<Long, OfferDO> offerMap = getOfferMap(extractDimensionIds(clicksData));

        // 2. 转换到 Category 维度 (仅首分类)
        Map<Long, Integer> clicksMap = remapClicksToDimension(clicksData, offerMap, this::getFirstCategoryId);

        // 3. 聚合转化
        Map<Long, Integer> conversionsMap = new HashMap<>();
        Map<Long, BigDecimal> revenueMap = new HashMap<>();
        aggregateConversionsWithMapping(date, ClickDO::getOfferId, offerMap,
                this::getFirstCategoryId, conversionsMap, revenueMap);

        // 4. 保存
        saveStats(date, DimensionTypeEnum.CATEGORY.getType(),
                  clicksMap, conversionsMap, revenueMap, null);
    }

    // ==================== 通用转化聚合方法 ====================

    private void aggregateConversionsToDimension(
            LocalDate date,
            Function<ClickDO, Long> dimensionExtractor,
            Map<Long, Integer> conversionsMap,
            Map<Long, BigDecimal> revenueMap) {

        List<Map<String, Object>> convData = conversionMapper.selectConversionsGroupByClickId(date);
        if (convData.isEmpty()) {
            return;
        }

        Set<String> clickIds = convData.stream()
                .map(m -> (String) m.get("clickid"))
                .collect(Collectors.toSet());
        Map<String, ClickDO> clickMap = clickMapper.selectByClickIds(clickIds).stream()
                .collect(Collectors.toMap(ClickDO::getClickId, Function.identity()));

        for (Map<String, Object> row : convData) {
            String clickId = (String) row.get("clickid");
            ClickDO click = clickMap.get(clickId);
            if (click == null) continue;

            Long dimensionId = dimensionExtractor.apply(click);
            if (dimensionId == null) continue;

            int convs = ((Number) row.get("conversions")).intValue();
            BigDecimal rev = (BigDecimal) row.get("revenue");

            conversionsMap.merge(dimensionId, convs, Integer::sum);
            revenueMap.merge(dimensionId, rev != null ? rev : BigDecimal.ZERO, BigDecimal::add);
        }
    }

    /**
     * 通用转化聚合方法 - 通过 Click 属性提取器解析中间关联后映射到目标维度
     */
    private <T> void aggregateConversionsWithMapping(
            LocalDate date,
            Function<ClickDO, Long> clickPropertyExtractor,
            Map<Long, T> entityMap,
            Function<T, Long> targetDimensionExtractor,
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
            if (click == null) continue;

            Long entityId = clickPropertyExtractor.apply(click);
            if (entityId == null) continue;

            T entity = entityMap.get(entityId);
            if (entity == null) continue;

            Long dimensionId = targetDimensionExtractor.apply(entity);
            if (dimensionId == null) continue;

            int convs = ((Number) row.get("conversions")).intValue();
            BigDecimal rev = (BigDecimal) row.get("revenue");

            conversionsMap.merge(dimensionId, convs, Integer::sum);
            revenueMap.merge(dimensionId, rev != null ? rev : BigDecimal.ZERO, BigDecimal::add);
        }
    }

    // ==================== 保存与计算 ====================

    private void saveStats(LocalDate date, Integer dimensionType,
                           Map<Long, Integer> clicksMap,
                           Map<Long, Integer> conversionsMap,
                           Map<Long, BigDecimal> revenueMap,
                           Map<Long, BigDecimal> costMap) {

        Set<Long> allIds = new HashSet<>();
        allIds.addAll(clicksMap.keySet());
        allIds.addAll(conversionsMap.keySet());

        for (Long dimensionId : allIds) {
            DailyStatsDO stats = new DailyStatsDO();
            stats.setDate(date);
            stats.setDimensionType(dimensionType);
            stats.setDimensionId(dimensionId);
            stats.setClicks(clicksMap.getOrDefault(dimensionId, 0));
            stats.setConversions(conversionsMap.getOrDefault(dimensionId, 0));
            stats.setRevenue(revenueMap.getOrDefault(dimensionId, BigDecimal.ZERO));
            stats.setCost(costMap != null ? costMap.getOrDefault(dimensionId, BigDecimal.ZERO) : BigDecimal.ZERO);

            calculateDerivedMetrics(stats);
            dailyStatsMapper.upsertStats(stats);
        }
    }

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

    // ==================== 辅助查询方法 ====================

    /**
     * 将查询结果转换为点击数映射 (dimensionId -> clicks)
     */
    private Map<Long, Integer> toClicksMap(List<Map<String, Object>> clicksData) {
        return clicksData.stream()
                .collect(Collectors.toMap(
                        m -> ((Number) m.get("dimensionid")).longValue(),
                        m -> ((Number) m.get("clicks")).intValue(),
                        Integer::sum));
    }

    /**
     * 从点击数据中提取所有维度 ID
     */
    private Set<Long> extractDimensionIds(List<Map<String, Object>> clicksData) {
        return clicksData.stream()
                .map(m -> ((Number) m.get("dimensionid")).longValue())
                .collect(Collectors.toSet());
    }

    /**
     * 将点击数据通过实体映射重新聚合到目标维度
     */
    private <T> Map<Long, Integer> remapClicksToDimension(
            List<Map<String, Object>> clicksData,
            Map<Long, T> entityMap,
            Function<T, Long> targetExtractor) {
        Map<Long, Integer> result = new HashMap<>();
        for (Map<String, Object> row : clicksData) {
            Long entityId = ((Number) row.get("dimensionid")).longValue();
            Integer clicks = ((Number) row.get("clicks")).intValue();
            T entity = entityMap.get(entityId);
            if (entity != null) {
                Long targetId = targetExtractor.apply(entity);
                if (targetId != null) {
                    result.merge(targetId, clicks, Integer::sum);
                }
            }
        }
        return result;
    }

    private Map<Long, CampaignDO> getCampaignMap(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyMap();
        }
        return campaignMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(CampaignDO::getId, Function.identity()));
    }

    private Map<Long, OfferDO> getOfferMap(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyMap();
        }
        return offerMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(OfferDO::getId, Function.identity()));
    }

    private Long getFirstCategoryId(OfferDO offer) {
        if (offer == null || offer.getCategoryIds() == null) {
            return null;
        }
        try {
            List<Long> categoryIds = JSONUtil.toList(offer.getCategoryIds(), Long.class);
            return categoryIds.isEmpty() ? null : categoryIds.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void aggregateHourlyStats() {
        log.info("[aggregateHourlyStats][开始聚合小时统计]");
    }

    @Override
    public void cleanupOldHourlyStats() {
        log.info("[cleanupOldHourlyStats][开始清理过期小时统计，保留 7 天]");
    }

}
