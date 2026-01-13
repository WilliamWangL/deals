package com.river.module.campaign.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 落地页类型枚举
 */
@Getter
@AllArgsConstructor
public enum LandingPageTypeEnum {

    INTERNAL(1, "内置"),
    EXTERNAL(2, "外链");

    private final Integer code;
    private final String name;

    public static LandingPageTypeEnum getByCode(Integer code) {
        for (LandingPageTypeEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
