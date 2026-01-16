package com.river.module.stats.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StatsAggregationJobImpl implements StatsAggregationJob {

    @Override
    public void aggregateDailyStats() {
        log.info("[aggregateDailyStats][开始聚合日报统计]");
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
