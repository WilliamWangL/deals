package com.river.module.stats.service.alert;

import com.river.framework.quartz.core.handler.JobHandler;
import com.river.framework.tenant.core.job.TenantJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Component("alertDailyCheckJob")
public class AlertDailyCheckJob implements JobHandler {

    private static final BigDecimal ROI_THRESHOLD = new BigDecimal("-20");

    @Override
    @TenantJob
    public String execute(String param) throws Exception {
        log.info("[Alert] Starting daily alert check for {}", LocalDate.now().minusDays(1));
        
        checkCampaignROI();
        checkConversionTrend();
        
        log.info("[Alert] Daily alert check completed");
        return "OK";
    }

    private void checkCampaignROI() {
        log.debug("[Alert] Checking campaign ROI...");
    }

    private void checkConversionTrend() {
        log.debug("[Alert] Checking conversion trend...");
    }

}
