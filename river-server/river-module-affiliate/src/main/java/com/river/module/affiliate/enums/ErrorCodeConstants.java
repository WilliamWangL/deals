package com.river.module.affiliate.enums;

import com.river.framework.common.exception.ErrorCode;

/**
 * Affiliate 错误码枚举类
 *
 * affiliate 模块，使用 1-020-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== 联盟网络 1-020-000-000 ==========
    ErrorCode NETWORK_NOT_EXISTS = new ErrorCode(1_020_000_001, "联盟网络不存在");
    ErrorCode NETWORK_CODE_DUPLICATE = new ErrorCode(1_020_000_002, "联盟编码已存在");

    // ========== 商家 1-020-001-000 ==========
    ErrorCode MERCHANT_NOT_EXISTS = new ErrorCode(1_020_001_001, "商家不存在");

    // ========== 分类 1-020-002-000 ==========
    ErrorCode CATEGORY_NOT_EXISTS = new ErrorCode(1_020_002_001, "分类不存在");
    ErrorCode CATEGORY_SLUG_DUPLICATE = new ErrorCode(1_020_002_002, "分类 Slug 已存在");
    ErrorCode CATEGORY_PARENT_NOT_EXISTS = new ErrorCode(1_020_002_003, "父分类不存在");
    ErrorCode CATEGORY_EXISTS_CHILDREN = new ErrorCode(1_020_002_004, "存在子分类，无法删除");

    // ========== Offer 1-020-003-000 ==========
    ErrorCode OFFER_NOT_EXISTS = new ErrorCode(1_020_003_001, "Offer 不存在");

}
