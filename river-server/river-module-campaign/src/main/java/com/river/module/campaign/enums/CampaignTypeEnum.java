package com.river.module.campaign.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Campaign 类型枚举
 */
@Getter
@AllArgsConstructor
public enum CampaignTypeEnum {

    ARBITRAGE(1, "套利"),
    ORGANIC(2, "自然流量");

    private final Integer code;
    private final String name;

    public static CampaignTypeEnum getByCode(Integer code) {
        for (CampaignTypeEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
