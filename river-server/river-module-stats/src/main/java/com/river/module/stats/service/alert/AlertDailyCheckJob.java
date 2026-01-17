package com.river.module.stats.service.alert;

import com.river.framework.quartz.core.handler.JobHandler;
import com.river.framework.tenant.core.context.TenantContextHolder;
import com.river.framework.tenant.core.job.TenantJob;
import com.river.module.stats.controller.admin.daily.vo.DailyStatsPageReqVO;
import com.river.module.stats.controller.admin.daily.vo.DailyStatsSummaryRespVO;
import com.river.module.stats.dal.dataobject.DailyStatsDO;
import com.river.module.stats.dal.mysql.DailyStatsMapper;
import com.river.module.stats.enums.DimensionTypeEnum;
import com.river.module.stats.service.DailyStatsService;
import com.river.module.system.dal.dataobject.tenant.TenantDO;
import com.river.module.system.service.notify.NotifySendService;
import com.river.module.system.service.tenant.TenantService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 每日告警检查任务
 *
 * - checkCampaignROI: 检查 Campaign ROI，低于阈值触发告警
 * - checkConversionTrend: 对比昨日转化数据，下降明显时告警
 */
@Slf4j
@Component("alertDailyCheckJob")
public class AlertDailyCheckJob implements JobHandler {

    /**
     * ROI 阈值：低于 -20% 触发告警
     */
    private static final BigDecimal ROI_THRESHOLD = new BigDecimal("-20");

    /**
     * 转化下降阈值：下降超过 30% 触发告警
     */
    private static final BigDecimal CONVERSION_DROP_THRESHOLD = new BigDecimal("30");

    @Resource
    private DailyStatsService dailyStatsService;

    @Resource
    private DailyStatsMapper dailyStatsMapper;

    @Resource
    private NotifySendService notifySendService;

    @Resource
    private TenantService tenantService;

    @Override
    @TenantJob
    public String execute(String param) throws Exception {
        log.info("[Alert] Starting daily alert check for {}", LocalDate.now().minusDays(1));

        int alertCount = 0;
        alertCount += checkCampaignROI();
        alertCount += checkConversionTrend();

        log.info("[Alert] Daily alert check completed, {} alerts triggered", alertCount);
        return String.format("Checked, %d alerts", alertCount);
    }

    /**
     * 检查 Campaign ROI
     * 遍历昨日各 Campaign ROI，如果低于阈值则发送告警
     */
    private int checkCampaignROI() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        int alertCount = 0;

        try {
            // 获取昨日按 Campaign 维度的统计数据
            List<DailyStatsDO> stats = dailyStatsMapper.selectListByCondition(
                    DimensionTypeEnum.CAMPAIGN.getType(), null, yesterday, yesterday);

            // 按 Campaign 分组计算 ROI
            Map<Long, List<DailyStatsDO>> grouped = stats.stream()
                    .collect(Collectors.groupingBy(DailyStatsDO::getDimensionId));

            for (Map.Entry<Long, List<DailyStatsDO>> entry : grouped.entrySet()) {
                Long campaignId = entry.getKey();
                List<DailyStatsDO> campaignStats = entry.getValue();

                // 计算汇总数据
                int totalClicks = campaignStats.stream()
                        .mapToInt(s -> s.getClicks() != null ? s.getClicks() : 0).sum();
                int totalConversions = campaignStats.stream()
                        .mapToInt(s -> s.getConversions() != null ? s.getConversions() : 0).sum();
                BigDecimal totalRevenue = campaignStats.stream()
                        .map(s -> s.getRevenue() != null ? s.getRevenue() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal totalCost = campaignStats.stream()
                        .map(s -> s.getCost() != null ? s.getCost() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal totalProfit = campaignStats.stream()
                        .map(s -> s.getProfit() != null ? s.getProfit() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                // 计算 ROI
                BigDecimal roi = BigDecimal.ZERO;
                if (totalCost.compareTo(BigDecimal.ZERO) > 0) {
                    roi = totalProfit
                            .multiply(BigDecimal.valueOf(100))
                            .divide(totalCost, 2, RoundingMode.HALF_UP);
                }

                if (roi.compareTo(ROI_THRESHOLD) < 0) {
                    AlertRecord alert = new AlertRecord();
                    alert.setType(AlertRecord.AlertType.ROI_DROP);
                    alert.setLevel(AlertRecord.AlertLevel.CRITICAL);
                    alert.setMessage(String.format("Campaign [Campaign-%d] ROI 严重下降: %.2f%%, 低于阈值 %.0f%%",
                            campaignId,
                            roi,
                            ROI_THRESHOLD));
                    alert.setDetails(String.format("CampaignId=%d, Clicks=%d, Conversions=%d, Revenue=%.2f, Cost=%.2f, Profit=%.2f",
                            campaignId,
                            totalClicks,
                            totalConversions,
                            totalRevenue,
                            totalCost,
                            totalProfit));
                    alert.setResolved(false);
                    alert.setCreatedAt(LocalDateTime.now());

                    sendAlert(alert);
                    alertCount++;
                }
            }

            log.debug("[Alert] Checked {} campaigns for ROI, {} below threshold", grouped.size(), alertCount);
        } catch (Exception e) {
            log.error("[Alert] Failed to check campaign ROI: {}", e.getMessage(), e);
        }

        return alertCount;
    }

    /**
     * 检查转化趋势
     * 对比昨日和前日转化数据，如果下降超过 30%，触发告警
     */
    private int checkConversionTrend() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate dayBeforeYesterday = LocalDate.now().minusDays(2);
        int alertCount = 0;

        try {
            // 使用统一的日报统计 API 获取汇总数据
            DailyStatsPageReqVO yesterdayReq = new DailyStatsPageReqVO();
            yesterdayReq.setStartDate(yesterday);
            yesterdayReq.setEndDate(yesterday);
            DailyStatsSummaryRespVO yesterdayStats = dailyStatsService.getDailyStatsSummary(yesterdayReq);

            DailyStatsPageReqVO previousReq = new DailyStatsPageReqVO();
            previousReq.setStartDate(dayBeforeYesterday);
            previousReq.setEndDate(dayBeforeYesterday);
            DailyStatsSummaryRespVO previousStats = dailyStatsService.getDailyStatsSummary(previousReq);

            int yesterdayConversions = yesterdayStats.getTotalConversions() != null ? yesterdayStats.getTotalConversions() : 0;
            int previousConversions = previousStats.getTotalConversions() != null ? previousStats.getTotalConversions() : 0;

            // 只有前日有转化数据时才计算下降比例
            if (previousConversions > 0) {
                BigDecimal dropRate = BigDecimal.valueOf(previousConversions - yesterdayConversions)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(previousConversions), 2, RoundingMode.HALF_UP);

                if (dropRate.compareTo(CONVERSION_DROP_THRESHOLD) > 0) {
                    AlertRecord alert = new AlertRecord();
                    alert.setType(AlertRecord.AlertType.CONVERSION_DROP);
                    alert.setLevel(AlertRecord.AlertLevel.WARNING);
                    alert.setMessage(String.format("转化数据大幅下降: 昨日 %d, 前日 %d, 下降 %.2f%%",
                            yesterdayConversions, previousConversions, dropRate));
                    alert.setDetails(String.format("昨日: Clicks=%d, Conversions=%d, Revenue=%.2f; 前日: Clicks=%d, Conversions=%d, Revenue=%.2f",
                            yesterdayStats.getTotalClicks(), yesterdayConversions, yesterdayStats.getTotalRevenue(),
                            previousStats.getTotalClicks(), previousConversions, previousStats.getTotalRevenue()));
                    alert.setResolved(false);
                    alert.setCreatedAt(LocalDateTime.now());

                    sendAlert(alert);
                    alertCount++;
                }
            }

            log.debug("[Alert] Conversion trend check: yesterday={}, previous={}", yesterdayConversions, previousConversions);
        } catch (Exception e) {
            log.error("[Alert] Failed to check conversion trend: {}", e.getMessage(), e);
        }

        return alertCount;
    }

    /**
     * 发送告警通知
     */
    private void sendAlert(AlertRecord alert) {
        Map<String, Object> params = new HashMap<>();
        params.put("type", alert.getType().name());
        params.put("level", alert.getLevel().name());
        params.put("message", alert.getMessage());
        params.put("details", alert.getDetails());

        try {
            // 获取当前租户的联系人（管理员）
            Long tenantId = TenantContextHolder.getTenantId();
            TenantDO tenant = tenantService.getTenant(tenantId);
            Long adminUserId = tenant != null && tenant.getContactUserId() != null
                    ? tenant.getContactUserId()
                    : 1L; // 兜底使用系统管理员

            notifySendService.sendSingleNotifyToAdmin(adminUserId, "alert-notification", params);
            log.warn("[Alert] {} - {}: {}", alert.getLevel(), alert.getType(), alert.getMessage());
        } catch (Exception e) {
            // 通知发送失败不影响告警检查流程
            log.error("[Alert] Failed to send notification: {}", e.getMessage());
            log.warn("[Alert] {} - {}: {}", alert.getLevel(), alert.getType(), alert.getMessage());
        }
    }

}
