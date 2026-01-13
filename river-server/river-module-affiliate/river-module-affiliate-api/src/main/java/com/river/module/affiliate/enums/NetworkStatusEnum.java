package com.river.module.affiliate.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NetworkStatusEnum {

    ACTIVE(1, "启用"),
    INACTIVE(0, "停用"),
    PENDING(2, "待审核");

    private final Integer code;
    private final String name;

    public static NetworkStatusEnum getByCode(Integer code) {
        for (NetworkStatusEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
