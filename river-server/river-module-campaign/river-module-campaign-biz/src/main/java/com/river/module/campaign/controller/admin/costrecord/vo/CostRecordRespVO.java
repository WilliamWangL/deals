package com.river.module.campaign.controller.admin.costrecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 成本记录 Response VO")
@Data
public class CostRecordRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "Campaign ID", example = "1")
    private Long campaignId;

    @Schema(description = "广告组 ID", example = "1")
    private Long adGroupId;

    @Schema(description = "日期")
    private LocalDate date;

    @Schema(description = "展示次数", example = "1000")
    private Integer impressions;

    @Schema(description = "点击次数", example = "50")
    private Integer clicks;

    @Schema(description = "成本", example = "100.00")
    private BigDecimal cost;

    @Schema(description = "货币", example = "USD")
    private String currency;

    @Schema(description = "来源", example = "1")
    private Integer source;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
