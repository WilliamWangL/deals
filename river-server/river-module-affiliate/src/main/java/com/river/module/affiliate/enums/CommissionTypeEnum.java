package com.river.module.affiliate.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommissionTypeEnum {

    PERCENT(1, "百分比"),
    FIXED(2, "固定金额"),
    TIERED(3, "阶梯佣金");

    private final Integer code;
    private final String name;

    public static CommissionTypeEnum getByCode(Integer code) {
        for (CommissionTypeEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
