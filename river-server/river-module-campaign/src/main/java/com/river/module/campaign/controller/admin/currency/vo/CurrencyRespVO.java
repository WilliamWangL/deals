package com.river.module.campaign.controller.admin.currency.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 货币 Response VO")
@Data
public class CurrencyRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "货币代码", example = "USD")
    private String code;

    @Schema(description = "名称", example = "美元")
    private String name;

    @Schema(description = "符号", example = "$")
    private String symbol;

    @Schema(description = "小数位数", example = "2")
    private Integer decimalPlaces;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
