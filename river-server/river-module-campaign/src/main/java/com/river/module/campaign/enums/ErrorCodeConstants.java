package com.river.module.campaign.enums;

import com.river.framework.common.exception.ErrorCode;

/**
 * Campaign 错误码枚举类
 *
 * campaign 模块，使用 1-024-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== 流量来源 1-024-000-000 ==========
    ErrorCode TRAFFIC_SOURCE_NOT_EXISTS = new ErrorCode(1_024_000_001, "流量来源不存在");
    ErrorCode TRAFFIC_SOURCE_CODE_DUPLICATE = new ErrorCode(1_024_000_002, "流量来源编码已存在");

    // ========== Campaign 1-024-001-000 ==========
    ErrorCode CAMPAIGN_NOT_EXISTS = new ErrorCode(1_024_001_001, "Campaign 不存在");
    ErrorCode CAMPAIGN_NAME_DUPLICATE = new ErrorCode(1_024_001_002, "Campaign 名称已存在");

    // ========== 广告组 1-024-002-000 ==========
    ErrorCode AD_GROUP_NOT_EXISTS = new ErrorCode(1_024_002_001, "广告组不存在");

    // ========== 落地页 1-024-003-000 ==========
    ErrorCode LANDING_PAGE_NOT_EXISTS = new ErrorCode(1_024_003_001, "落地页不存在");
    ErrorCode LANDING_PAGE_SLUG_DUPLICATE = new ErrorCode(1_024_003_002, "落地页 Slug 已存在");

    // ========== 成本记录 1-024-004-000 ==========
    ErrorCode COST_RECORD_NOT_EXISTS = new ErrorCode(1_024_004_001, "成本记录不存在");

    // ========== 货币 1-024-005-000 ==========
    ErrorCode CURRENCY_NOT_EXISTS = new ErrorCode(1_024_005_001, "货币不存在");
    ErrorCode CURRENCY_CODE_DUPLICATE = new ErrorCode(1_024_005_002, "货币编码已存在");

    // ========== 汇率 1-024-006-000 ==========
    ErrorCode FX_RATE_NOT_EXISTS = new ErrorCode(1_024_006_001, "汇率记录不存在");

}
