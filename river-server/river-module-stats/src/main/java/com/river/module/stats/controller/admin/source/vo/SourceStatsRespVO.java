package com.river.module.stats.controller.admin.source.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 渠道统计 Response VO")
@Data
public class SourceStatsRespVO {

    @Schema(description = "渠道 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long sourceId;

    @Schema(description = "渠道名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "Google Ads")
    private String sourceName;

    @Schema(description = "总点击数", requiredMode = Schema.RequiredMode.REQUIRED, example = "10000")
    private Integer totalClicks;

    @Schema(description = "总转化数", requiredMode = Schema.RequiredMode.REQUIRED, example = "500")
    private Integer totalConversions;

    @Schema(description = "总收入", requiredMode = Schema.RequiredMode.REQUIRED, example = "9999.99")
    private BigDecimal totalRevenue;

    @Schema(description = "总成本", requiredMode = Schema.RequiredMode.REQUIRED, example = "5000.00")
    private BigDecimal totalCost;

    @Schema(description = "总利润", requiredMode = Schema.RequiredMode.REQUIRED, example = "4999.99")
    private BigDecimal totalProfit;

    @Schema(description = "EPC", requiredMode = Schema.RequiredMode.REQUIRED, example = "0.99")
    private BigDecimal epc;

    @Schema(description = "转化率", requiredMode = Schema.RequiredMode.REQUIRED, example = "5.00")
    private BigDecimal cr;

}
