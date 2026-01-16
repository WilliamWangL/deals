package com.river.module.stats.controller.admin.dashboard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

import static com.river.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Schema(description = "管理后台 - 仪表盘趋势查询 Request VO")
@Data
public class DashboardTrendReqVO {

    @Schema(description = "开始日期", example = "2024-01-01")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate startDate;

    @Schema(description = "结束日期", example = "2024-01-31")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate endDate;

    @Schema(description = "维度类型", example = "1")
    private Integer dimensionType;

    @Schema(description = "维度 ID", example = "1024")
    private Long dimensionId;

}
