package com.river.module.stats.controller.admin.daily.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "管理后台 - 日报统计趋势 Response VO")
@Data
public class DailyStatsTrendRespVO {

    @Schema(description = "日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate date;

    @Schema(description = "点击数", requiredMode = Schema.RequiredMode.REQUIRED, example = "1000")
    private Integer clicks;

    @Schema(description = "转化数", requiredMode = Schema.RequiredMode.REQUIRED, example = "50")
    private Integer conversions;

    @Schema(description = "收入", requiredMode = Schema.RequiredMode.REQUIRED, example = "999.99")
    private BigDecimal revenue;

    @Schema(description = "成本", requiredMode = Schema.RequiredMode.REQUIRED, example = "500.00")
    private BigDecimal cost;

    @Schema(description = "利润", requiredMode = Schema.RequiredMode.REQUIRED, example = "499.99")
    private BigDecimal profit;

}
