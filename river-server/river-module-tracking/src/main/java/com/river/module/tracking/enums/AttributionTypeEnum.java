package com.river.module.tracking.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 归因类型枚举
 */
@Getter
@AllArgsConstructor
public enum AttributionTypeEnum {

    LAST_CLICK(1, "最后点击"),
    FIRST_CLICK(2, "首次点击"),
    LINEAR(3, "线性归因");

    private final Integer code;
    private final String name;

}
