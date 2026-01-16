package com.river.module.tracking.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 转化类型枚举
 */
@Getter
@AllArgsConstructor
public enum ConversionTypeEnum {

    LEAD(1, "Lead"),
    SALE(2, "Sale"),
    INSTALL(3, "Install"),
    SIGNUP(4, "Signup");

    private final Integer code;
    private final String name;

}
