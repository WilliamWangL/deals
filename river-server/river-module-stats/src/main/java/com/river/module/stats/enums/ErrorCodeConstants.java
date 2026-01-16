package com.river.module.stats.enums;

import com.river.framework.common.exception.ErrorCode;

/**
 * Stats 错误码枚举
 *
 * stats 系统，使用 1-010-000-000 段
 */
public interface ErrorCodeConstants {

    ErrorCode DAILY_STATS_NOT_EXISTS = new ErrorCode(1_010_000_001, "日报统计不存在");
    ErrorCode HOURLY_STATS_NOT_EXISTS = new ErrorCode(1_010_000_002, "小时统计不存在");

}
