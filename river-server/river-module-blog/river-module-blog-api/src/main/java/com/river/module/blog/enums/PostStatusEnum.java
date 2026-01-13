package com.river.module.blog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PostStatusEnum {

    DRAFT(0, "草稿"),
    PENDING(1, "待审"),
    PUBLISHED(2, "已发布"),
    ARCHIVED(3, "下架");

    private final Integer code;
    private final String name;

    public static PostStatusEnum getByCode(Integer code) {
        for (PostStatusEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
