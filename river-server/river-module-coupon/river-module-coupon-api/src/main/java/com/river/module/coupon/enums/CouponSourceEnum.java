package com.river.module.coupon.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CouponSourceEnum {

    NETWORK_SYNC(1, "联盟同步"),
    MANUAL(2, "手动录入"),
    USER_SUBMIT(3, "用户提交");

    private final Integer code;
    private final String name;

    public static CouponSourceEnum getByCode(Integer code) {
        for (CouponSourceEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
