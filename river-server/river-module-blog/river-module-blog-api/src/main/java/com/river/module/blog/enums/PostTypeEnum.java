package com.river.module.blog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PostTypeEnum {

    DEAL(1, "Deal帖"),
    REVIEW(2, "评测"),
    TUTORIAL(3, "教程"),
    NEWS(4, "资讯");

    private final Integer code;
    private final String name;

    public static PostTypeEnum getByCode(Integer code) {
        for (PostTypeEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
