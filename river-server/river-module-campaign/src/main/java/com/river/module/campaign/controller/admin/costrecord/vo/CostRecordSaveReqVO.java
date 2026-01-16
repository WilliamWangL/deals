package com.river.module.campaign.controller.admin.costrecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "管理后台 - 成本记录 创建/修改 Request VO")
@Data
public class CostRecordSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "Campaign ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "Campaign 不能为空")
    private Long campaignId;

    @Schema(description = "广告组 ID", example = "1")
    private Long adGroupId;

    @Schema(description = "日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "日期不能为空")
    private LocalDate date;

    @Schema(description = "展示次数", example = "1000")
    private Integer impressions;

    @Schema(description = "点击次数", example = "50")
    private Integer clicks;

    @Schema(description = "成本", requiredMode = Schema.RequiredMode.REQUIRED, example = "100.00")
    @NotNull(message = "成本不能为空")
    private BigDecimal cost;

    @Schema(description = "货币", example = "USD")
    private String currency;

    @Schema(description = "来源", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "来源不能为空")
    private Integer source;
}
