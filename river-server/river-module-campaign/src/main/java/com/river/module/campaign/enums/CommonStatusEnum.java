package com.river.module.campaign.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通用状态枚举
 */
@Getter
@AllArgsConstructor
public enum CommonStatusEnum {

    DISABLE(0, "禁用"),
    ENABLE(1, "启用");

    private final Integer code;
    private final String name;

    public static CommonStatusEnum getByCode(Integer code) {
        for (CommonStatusEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
