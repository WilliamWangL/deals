package com.river.module.tracking.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TrackingTargetTypeEnum {

    MERCHANT(1, "商家"),
    OFFER(2, "Offer"),
    DEAL(3, "Deal"),
    COUPON(4, "优惠券");

    private final Integer type;
    private final String name;

    public static TrackingTargetTypeEnum valueOf(Integer type) {
        for (TrackingTargetTypeEnum value : values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return null;
    }
}
