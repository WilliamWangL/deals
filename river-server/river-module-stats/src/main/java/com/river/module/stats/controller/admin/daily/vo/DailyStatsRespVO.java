package com.river.module.stats.controller.admin.daily.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 日报统计 Response VO")
@Data
public class DailyStatsRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "统计日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate date;

    @Schema(description = "维度类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer dimensionType;

    @Schema(description = "维度 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long dimensionId;

    @Schema(description = "维度名称", example = "Campaign-001")
    private String dimensionName;

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

    @Schema(description = "EPC", requiredMode = Schema.RequiredMode.REQUIRED, example = "0.9999")
    private BigDecimal epc;

    @Schema(description = "转化率", requiredMode = Schema.RequiredMode.REQUIRED, example = "5.00")
    private BigDecimal cr;

    @Schema(description = "ROI", requiredMode = Schema.RequiredMode.REQUIRED, example = "99.99")
    private BigDecimal roi;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
