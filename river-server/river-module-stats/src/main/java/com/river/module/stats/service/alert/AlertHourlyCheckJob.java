package com.river.module.stats.service.alert;

import com.river.framework.quartz.core.handler.JobHandler;
import com.river.framework.tenant.core.context.TenantContextHolder;
import com.river.framework.tenant.core.job.TenantJob;
import com.river.module.system.dal.dataobject.tenant.TenantDO;
import com.river.module.system.service.notify.NotifySendService;
import com.river.module.system.service.tenant.TenantService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 每小时告警检查任务
 *
 * - checkUnattributedConversionRate: 查询未归因转化率，超过阈值触发告警
 * - checkHighFrequencyClicks: 检查同IP高频点击，发现异常流量
 * - checkPostbackFailureRate: 监控 Postback 签名失败率
 */
@Slf4j
@Component("alertHourlyCheckJob")
public class AlertHourlyCheckJob implements JobHandler {

    /**
     * 未归因转化率阈值: 超过 10% 触发告警
     */
    private static final double UNATTRIBUTED_RATE_THRESHOLD = 0.10;

    /**
     * 高频点击阈值: 同 IP 每小时超过 100 次触发告警
     */
    private static final int HIGH_FREQUENCY_CLICK_THRESHOLD = 100;

    /**
     * Postback 失败率阈值: 超过 5% 触发告警
     */
    private static final double POSTBACK_FAILURE_RATE_THRESHOLD = 0.05;

    @Resource
    private NotifySendService notifySendService;

    @Resource
    private TenantService tenantService;

    @Override
    @TenantJob
    public String execute(String param) throws Exception {
        log.info("[Alert] Starting hourly alert check at {}", LocalDateTime.now());

        int alertCount = 0;
        alertCount += checkUnattributedConversionRate();
        alertCount += checkHighFrequencyClicks();
        alertCount += checkPostbackFailureRate();

        log.info("[Alert] Hourly alert check completed, {} alerts triggered", alertCount);
        return String.format("Checked, %d alerts", alertCount);
    }

    /**
     * 检查未归因转化率
     * 查询最近1小时未归因转化率，如果超过 10%，触发告警
     *
     * TODO: 依赖 TrackingService.getUnattributedConversionRate() 方法实现
     *       需要在 river-module-tracking 中暴露统计接口
     */
    private int checkUnattributedConversionRate() {
        int alertCount = 0;

        try {
            log.debug("[Alert] Checking unattributed conversion rate (threshold: {}%)", UNATTRIBUTED_RATE_THRESHOLD * 100);

            // TODO: 实现步骤:
            // 1. 注入 TrackingStatsService (需要在 tracking 模块创建)
            // 2. 查询最近1小时未归因转化率
            // 3. 超过阈值时创建告警并发送
            //
            // 示例代码:
            // double unattributedRate = trackingStatsService.getUnattributedConversionRate(LocalDateTime.now().minusHours(1));
            // if (unattributedRate > UNATTRIBUTED_RATE_THRESHOLD) {
            //     AlertRecord alert = AlertRecord.builder()
            //             .type(AlertRecord.AlertType.UNATTRIBUTED_CONVERSION)
            //             .level(AlertRecord.AlertLevel.WARNING)
            //             .message(String.format("未归因转化率 %.2f%% 超过阈值 %.2f%%", unattributedRate * 100, UNATTRIBUTED_RATE_THRESHOLD * 100))
            //             .build();
            //     sendAlert(alert);
            //     alertCount++;
            // }

            log.debug("[Alert] Unattributed conversion rate check completed (placeholder - no data source)");
        } catch (Exception e) {
            log.error("[Alert] Failed to check unattributed conversion rate: {}", e.getMessage(), e);
        }

        return alertCount;
    }

    /**
     * 检查高频点击
     * 查询同IP高频点击（>100次/小时），发现异常流量告警
     *
     * TODO: 依赖 ClickMapper.selectHighFrequencyIps() 方法实现
     *       需要在 river-module-tracking 中添加 IP 统计查询
     */
    private int checkHighFrequencyClicks() {
        int alertCount = 0;

        try {
            log.debug("[Alert] Checking high frequency clicks (threshold: {} clicks/hour)", HIGH_FREQUENCY_CLICK_THRESHOLD);

            // TODO: 实现步骤:
            // 1. 注入 ClickMapper 或 ClickStatsService
            // 2. 查询最近1小时内同IP点击次数超过阈值的记录
            // 3. 为每个可疑IP创建告警
            //
            // 示例 SQL:
            // SELECT ip, COUNT(*) as click_count
            // FROM river_click
            // WHERE click_time >= NOW() - INTERVAL 1 HOUR
            // GROUP BY ip
            // HAVING COUNT(*) > #{threshold}
            //
            // 示例代码:
            // List<IpClickStats> suspiciousIps = clickMapper.selectHighFrequencyIps(HIGH_FREQUENCY_CLICK_THRESHOLD);
            // for (IpClickStats stats : suspiciousIps) {
            //     AlertRecord alert = AlertRecord.builder()
            //             .type(AlertRecord.AlertType.HIGH_FREQUENCY_CLICK)
            //             .level(AlertRecord.AlertLevel.WARNING)
            //             .message(String.format("IP %s 在1小时内点击 %d 次，超过阈值 %d", stats.getIp(), stats.getCount(), HIGH_FREQUENCY_CLICK_THRESHOLD))
            //             .build();
            //     sendAlert(alert);
            //     alertCount++;
            // }

            log.debug("[Alert] High frequency clicks check completed (placeholder - no data source)");
        } catch (Exception e) {
            log.error("[Alert] Failed to check high frequency clicks: {}", e.getMessage(), e);
        }

        return alertCount;
    }

    /**
     * 检查 Postback 失败率
     * 监控 Postback 失败率，失败率超过 5% 告警
     *
     * TODO: 依赖 PostbackService.getFailureRate() 方法实现
     *       需要在 river-module-tracking 的 PostbackService 中暴露统计接口
     */
    private int checkPostbackFailureRate() {
        int alertCount = 0;

        try {
            log.debug("[Alert] Checking postback failure rate (threshold: {}%)", POSTBACK_FAILURE_RATE_THRESHOLD * 100);

            // TODO: 实现步骤:
            // 1. 注入 PostbackStatsService 或 ConversionMapper
            // 2. 查询最近1小时 postback 成功/失败统计
            // 3. 计算失败率，超过阈值时告警
            //
            // 示例 SQL:
            // SELECT
            //   COUNT(*) as total,
            //   SUM(CASE WHEN postback_status = 'FAILED' THEN 1 ELSE 0 END) as failed
            // FROM river_conversion
            // WHERE create_time >= NOW() - INTERVAL 1 HOUR
            //
            // 示例代码:
            // PostbackStats stats = conversionMapper.selectPostbackStats(LocalDateTime.now().minusHours(1));
            // if (stats.getTotal() > 0) {
            //     double failureRate = (double) stats.getFailed() / stats.getTotal();
            //     if (failureRate > POSTBACK_FAILURE_RATE_THRESHOLD) {
            //         AlertRecord alert = AlertRecord.builder()
            //                 .type(AlertRecord.AlertType.POSTBACK_FAILURE)
            //                 .level(AlertRecord.AlertLevel.CRITICAL)
            //                 .message(String.format("Postback 失败率 %.2f%% 超过阈值 %.2f%%", failureRate * 100, POSTBACK_FAILURE_RATE_THRESHOLD * 100))
            //                 .build();
            //         sendAlert(alert);
            //         alertCount++;
            //     }
            // }

            log.debug("[Alert] Postback failure rate check completed (placeholder - no data source)");
        } catch (Exception e) {
            log.error("[Alert] Failed to check postback failure rate: {}", e.getMessage(), e);
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
            Long tenantId = TenantContextHolder.getTenantId();
            TenantDO tenant = tenantService.getTenant(tenantId);
            Long adminUserId = tenant != null && tenant.getContactUserId() != null
                    ? tenant.getContactUserId()
                    : 1L;

            notifySendService.sendSingleNotifyToAdmin(adminUserId, "alert-notification", params);
            log.warn("[Alert] {} - {}: {}", alert.getLevel(), alert.getType(), alert.getMessage());
        } catch (Exception e) {
            log.error("[Alert] Failed to send notification: {}", e.getMessage());
            log.warn("[Alert] {} - {}: {}", alert.getLevel(), alert.getType(), alert.getMessage());
        }
    }

}
