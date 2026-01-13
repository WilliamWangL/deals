package com.river.module.coupon.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CouponStatusEnum {

    ACTIVE(1, "生效中"),
    EXPIRED(2, "已过期"),
    DISABLED(0, "已禁用");

    private final Integer code;
    private final String name;

    public static CouponStatusEnum getByCode(Integer code) {
        for (CouponStatusEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
