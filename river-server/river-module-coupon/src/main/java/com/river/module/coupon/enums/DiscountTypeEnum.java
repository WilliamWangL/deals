package com.river.module.coupon.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DiscountTypeEnum {

    PERCENT(1, "百分比折扣"),
    FIXED(2, "固定金额"),
    FREE_SHIPPING(3, "免邮");

    private final Integer code;
    private final String name;

    public static DiscountTypeEnum getByCode(Integer code) {
        for (DiscountTypeEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
