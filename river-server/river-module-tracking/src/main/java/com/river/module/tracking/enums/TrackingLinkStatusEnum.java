package com.river.module.tracking.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 追踪链接状态枚举
 */
@Getter
@AllArgsConstructor
public enum TrackingLinkStatusEnum {

    DISABLED(0, "禁用"),
    ENABLED(1, "启用");

    private final Integer code;
    private final String name;

}
