package com.river.module.tracking.enums;

import com.river.framework.common.exception.ErrorCode;

/**
 * Tracking 错误码枚举类
 *
 * tracking 模块，使用 1-021-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== 点击记录 1-021-000-000 ==========
    ErrorCode CLICK_NOT_EXISTS = new ErrorCode(1_021_000_001, "点击记录不存在");

    // ========== 转化记录 1-021-001-000 ==========
    ErrorCode CONVERSION_NOT_EXISTS = new ErrorCode(1_021_001_001, "转化记录不存在");
    ErrorCode CONVERSION_DUPLICATE = new ErrorCode(1_021_001_002, "转化记录重复");
    ErrorCode CONVERSION_CLICK_NOT_FOUND = new ErrorCode(1_021_001_003, "关联点击记录不存在");

    // ========== 追踪链接 1-021-002-000 ==========
    ErrorCode TRACKING_LINK_NOT_EXISTS = new ErrorCode(1_021_002_001, "追踪链接不存在");
    ErrorCode TRACKING_LINK_SLUG_DUPLICATE = new ErrorCode(1_021_002_002, "追踪链接 Slug 已存在");
    ErrorCode TRACKING_LINK_OFFER_NOT_EXISTS = new ErrorCode(1_021_002_003, "关联 Offer 不存在");

    // ========== 未归因转化 1-021-003-000 ==========
    ErrorCode UNATTRIBUTED_CONVERSION_NOT_EXISTS = new ErrorCode(1_021_003_001, "未归因转化不存在");

}
