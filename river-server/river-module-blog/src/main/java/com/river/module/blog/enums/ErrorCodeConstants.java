package com.river.module.blog.enums;

import com.river.framework.common.exception.ErrorCode;

/**
 * Blog 错误码枚举类
 *
 * blog 模块，使用 1-023-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== 文章 1-023-000-000 ==========
    ErrorCode POST_NOT_EXISTS = new ErrorCode(1_023_000_001, "文章不存在");
    ErrorCode POST_SLUG_DUPLICATE = new ErrorCode(1_023_000_002, "文章 Slug 已存在");

    // ========== 作者 1-023-001-000 ==========
    ErrorCode AUTHOR_NOT_EXISTS = new ErrorCode(1_023_001_001, "作者不存在");
    ErrorCode AUTHOR_SLUG_DUPLICATE = new ErrorCode(1_023_001_002, "作者 Slug 已存在");

    // ========== 标签 1-023-002-000 ==========
    ErrorCode TAG_NOT_EXISTS = new ErrorCode(1_023_002_001, "标签不存在");
    ErrorCode TAG_SLUG_DUPLICATE = new ErrorCode(1_023_002_002, "标签 Slug 已存在");

}
