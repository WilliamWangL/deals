package com.river.module.stats.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统计维度类型枚举
 */
@Getter
@AllArgsConstructor
public enum DimensionTypeEnum {

    OFFER(1, "Offer"),
    CAMPAIGN(2, "Campaign"),
    SOURCE(3, "渠道"),
    MERCHANT(4, "商家"),
    CATEGORY(5, "分类"),
    AUTHOR(6, "作者");

    private final Integer type;
    private final String name;

    public static DimensionTypeEnum valueOf(Integer type) {
        for (DimensionTypeEnum value : values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return null;
    }

}
