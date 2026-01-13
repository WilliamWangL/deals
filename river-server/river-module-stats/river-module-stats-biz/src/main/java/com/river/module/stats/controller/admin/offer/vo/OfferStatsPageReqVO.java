package com.river.module.stats.controller.admin.offer.vo;

import com.river.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

import static com.river.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Schema(description = "管理后台 - Offer 统计分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class OfferStatsPageReqVO extends PageParam {

    @Schema(description = "维度类型", example = "1")
    private Integer dimensionType;

    @Schema(description = "维度 ID", example = "1024")
    private Long dimensionId;

    @Schema(description = "开始日期", example = "2024-01-01")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate startDate;

    @Schema(description = "结束日期", example = "2024-01-31")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate endDate;

}
