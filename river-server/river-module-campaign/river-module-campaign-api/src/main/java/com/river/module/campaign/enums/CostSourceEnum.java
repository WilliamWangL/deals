package com.river.module.campaign.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 成本来源枚举
 */
@Getter
@AllArgsConstructor
public enum CostSourceEnum {

    MANUAL(1, "手动录入"),
    API_SYNC(2, "API同步");

    private final Integer code;
    private final String name;

    public static CostSourceEnum getByCode(Integer code) {
        for (CostSourceEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
