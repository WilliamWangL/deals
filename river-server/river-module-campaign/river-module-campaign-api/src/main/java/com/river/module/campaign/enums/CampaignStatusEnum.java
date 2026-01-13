package com.river.module.campaign.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Campaign 状态枚举
 */
@Getter
@AllArgsConstructor
public enum CampaignStatusEnum {

    DRAFT(0, "草稿"),
    ACTIVE(1, "活跃"),
    PAUSED(2, "暂停"),
    ENDED(3, "已结束");

    private final Integer code;
    private final String name;

    public static CampaignStatusEnum getByCode(Integer code) {
        for (CampaignStatusEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
