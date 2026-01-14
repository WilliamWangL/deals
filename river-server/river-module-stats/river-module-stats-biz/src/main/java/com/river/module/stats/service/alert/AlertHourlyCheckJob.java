package com.river.module.stats.service.alert;

import com.river.framework.quartz.core.handler.JobHandler;
import com.river.framework.tenant.core.job.TenantJob;
import com.river.module.system.service.notify.NotifySendService;
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
     * 注: 当前为简化实现，实际应从 Tracking 模块获取实时数据
     */
    private int checkUnattributedConversionRate() {
        int alertCount = 0;

        try {
            // 简化实现: 实际应查询 Tracking 模块的实时数据
            // 例如: trackingService.getUnattributedConversionRate(lastHour)
            log.debug("[Alert] Checking unattributed conversion rate (threshold: {}%)", UNATTRIBUTED_RATE_THRESHOLD * 100);

            // 模拟检查逻辑 - 实际应替换为真实查询
            // double unattributedRate = trackingService.getUnattributedConversionRate();
            // if (unattributedRate > UNATTRIBUTED_RATE_THRESHOLD) {
            //     AlertRecord alert = createAlert(AlertRecord.AlertType.UNATTRIBUTED_CONVERSION, ...);
            //     sendAlert(alert);
            //     alertCount++;
            // }

            log.debug("[Alert] Unattributed conversion rate check completed");
        } catch (Exception e) {
            log.error("[Alert] Failed to check unattributed conversion rate: {}", e.getMessage(), e);
        }

        return alertCount;
    }

    /**
     * 检查高频点击
     * 查询同IP高频点击（>100次/小时），发现异常流量告警
     *
     * 注: 当前为简化实现，实际应从 Tracking 模块获取实时数据
     */
    private int checkHighFrequencyClicks() {
        int alertCount = 0;

        try {
            // 简化实现: 实际应查询 Tracking 模块的 IP 点击统计
            // 例如: trackingService.getHighFrequencyClickIps(threshold)
            log.debug("[Alert] Checking high frequency clicks (threshold: {} clicks/hour)", HIGH_FREQUENCY_CLICK_THRESHOLD);

            // 模拟检查逻辑 - 实际应替换为真实查询
            // List<IpClickStats> suspiciousIps = trackingService.getHighFrequencyClickIps(HIGH_FREQUENCY_CLICK_THRESHOLD);
            // for (IpClickStats ip : suspiciousIps) {
            //     AlertRecord alert = createAlert(AlertRecord.AlertType.HIGH_FREQUENCY_CLICK, ...);
            //     sendAlert(alert);
            //     alertCount++;
            // }

            log.debug("[Alert] High frequency clicks check completed");
        } catch (Exception e) {
            log.error("[Alert] Failed to check high frequency clicks: {}", e.getMessage(), e);
        }

        return alertCount;
    }

    /**
     * 检查 Postback 失败率
     * 监控 Postback 失败率，失败率超过 5% 告警
     *
     * 注: 当前为简化实现，实际应从 Postback 模块获取实时数据
     */
    private int checkPostbackFailureRate() {
        int alertCount = 0;

        try {
            // 简化实现: 实际应查询 Postback 模块的失败率统计
            // 例如: postbackService.getFailureRate(lastHour)
            log.debug("[Alert] Checking postback failure rate (threshold: {}%)", POSTBACK_FAILURE_RATE_THRESHOLD * 100);

            // 模拟检查逻辑 - 实际应替换为真实查询
            // double failureRate = postbackService.getFailureRate();
            // if (failureRate > POSTBACK_FAILURE_RATE_THRESHOLD) {
            //     AlertRecord alert = createAlert(AlertRecord.AlertType.POSTBACK_FAILURE, ...);
            //     sendAlert(alert);
            //     alertCount++;
            // }

            log.debug("[Alert] Postback failure rate check completed");
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
            // 发送给管理员 (userId=1)
            notifySendService.sendSingleNotifyToAdmin(1L, "alert-notification", params);
            log.warn("[Alert] {} - {}: {}", alert.getLevel(), alert.getType(), alert.getMessage());
        } catch (Exception e) {
            // 通知发送失败不影响告警检查流程
            log.error("[Alert] Failed to send notification: {}", e.getMessage());
            log.warn("[Alert] {} - {}: {}", alert.getLevel(), alert.getType(), alert.getMessage());
        }
    }

}
