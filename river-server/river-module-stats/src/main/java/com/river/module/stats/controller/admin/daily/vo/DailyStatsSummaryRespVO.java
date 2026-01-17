package com.river.module.stats.controller.admin.daily.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 日报统计汇总 Response VO")
@Data
public class DailyStatsSummaryRespVO {

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

    @Schema(description = "平均 EPC", requiredMode = Schema.RequiredMode.REQUIRED, example = "0.99")
    private BigDecimal avgEpc;

    @Schema(description = "平均转化率", requiredMode = Schema.RequiredMode.REQUIRED, example = "5.00")
    private BigDecimal avgCr;

    @Schema(description = "平均 ROI", requiredMode = Schema.RequiredMode.REQUIRED, example = "99.99")
    private BigDecimal avgRoi;

}
