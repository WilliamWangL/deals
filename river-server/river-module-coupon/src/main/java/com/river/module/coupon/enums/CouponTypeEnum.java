package com.river.module.coupon.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 优惠券类型枚举（对应字典 coupon_type）
 */
@Getter
@AllArgsConstructor
public enum CouponTypeEnum {

    PROMOCODE(1, "优惠码"),
    SALE(2, "促销"),
    DEAL(3, "Deal");

    private final Integer code;
    private final String name;

    public static CouponTypeEnum getByCode(Integer code) {
        if (code == null) return PROMOCODE;
        for (CouponTypeEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return PROMOCODE;
    }

    /**
     * 从 Admitad API species 字符串映射
     */
    public static CouponTypeEnum fromAdmitadSpecies(String species) {
        if (species == null) return PROMOCODE;
        return switch (species.toLowerCase()) {
            case "promocode" -> PROMOCODE;
            case "sale" -> SALE;
            default -> DEAL;
        };
    }
}
