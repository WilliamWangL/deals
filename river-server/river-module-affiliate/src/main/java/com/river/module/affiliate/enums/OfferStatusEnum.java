package com.river.module.affiliate.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OfferStatusEnum {

    ACTIVE(1, "可投放"),
    PAUSED(2, "暂停"),
    ENDED(3, "已结束"),
    PENDING(0, "待审核");

    private final Integer code;
    private final String name;

    public static OfferStatusEnum getByCode(Integer code) {
        for (OfferStatusEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
