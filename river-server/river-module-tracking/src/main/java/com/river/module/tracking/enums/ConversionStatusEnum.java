package com.river.module.tracking.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 转化状态枚举
 */
@Getter
@AllArgsConstructor
public enum ConversionStatusEnum {

    PENDING(0, "待确认"),
    APPROVED(1, "已确认"),
    REJECTED(2, "已拒绝"),
    REVERSED(3, "已撤销");

    private final Integer code;
    private final String name;

}
