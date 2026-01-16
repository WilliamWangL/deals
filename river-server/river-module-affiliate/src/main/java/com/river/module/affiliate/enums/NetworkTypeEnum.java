package com.river.module.affiliate.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NetworkTypeEnum {

    CPS(1, "CPS", "按销售付费"),
    CPA(2, "CPA", "按行动付费"),
    CPC(3, "CPC", "按点击付费"),
    HYBRID(4, "HYBRID", "混合模式");

    private final Integer code;
    private final String name;
    private final String description;

    public static NetworkTypeEnum getByCode(Integer code) {
        for (NetworkTypeEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
