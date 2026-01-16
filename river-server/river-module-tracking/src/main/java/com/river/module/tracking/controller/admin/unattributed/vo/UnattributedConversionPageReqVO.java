package com.river.module.tracking.controller.admin.unattributed.vo;

import com.river.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.river.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 未归因转化分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class UnattributedConversionPageReqVO extends PageParam {

    @Schema(description = "网络代码", example = "impact")
    private String networkCode;

    @Schema(description = "外部转化 ID")
    private String externalConversionId;

    @Schema(description = "转化类型")
    private Integer conversionType;

    @Schema(description = "转化时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] conversionTime;

}
