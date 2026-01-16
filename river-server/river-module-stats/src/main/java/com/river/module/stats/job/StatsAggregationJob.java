package com.river.module.stats.job;

public interface StatsAggregationJob {

    void aggregateDailyStats();

    void aggregateHourlyStats();

    void cleanupOldHourlyStats();

}
