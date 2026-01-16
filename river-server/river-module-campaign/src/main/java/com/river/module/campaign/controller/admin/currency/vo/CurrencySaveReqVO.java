package com.river.module.campaign.controller.admin.currency.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 货币 创建/修改 Request VO")
@Data
public class CurrencySaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "货币代码", requiredMode = Schema.RequiredMode.REQUIRED, example = "USD")
    @NotBlank(message = "货币代码不能为空")
    private String code;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "美元")
    @NotBlank(message = "名称不能为空")
    private String name;

    @Schema(description = "符号", example = "$")
    private String symbol;

    @Schema(description = "小数位数", example = "2")
    private Integer decimalPlaces;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;
}
