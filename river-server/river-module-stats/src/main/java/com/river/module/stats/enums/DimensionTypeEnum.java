package com.river.module.stats.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 维度类型枚举
 *
 * @author river
 */
@Getter
@AllArgsConstructor
public enum DimensionTypeEnum {

    /**
     * 活动
     */
    CAMPAIGN(1, "活动"),

    /**
     * 流量源
     */
    SOURCE(2, "流量源"),

    /**
     * Offer
     */
    OFFER(3, "Offer"),

    /**
     * 落地页
     */
    LANDING_PAGE(4, "落地页"),

    /**
     * 商家
     */
    MERCHANT(5, "商家"),

    /**
     * 分类
     */
    CATEGORY(6, "分类");

    /**
     * 类型
     */
    private final Integer type;

    /**
     * 名称
     */
    private final String name;

    public static DimensionTypeEnum getByType(Integer type) {
        for (DimensionTypeEnum value : values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return null;
    }

}
