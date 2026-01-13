package com.river.module.tracking.controller.admin.conversion.vo;

import com.river.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.river.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 转化记录分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ConversionPageReqVO extends PageParam {

    @Schema(description = "点击 ID")
    private String clickId;

    @Schema(description = "联盟网络编码")
    private String networkCode;

    @Schema(description = "转化类型")
    private Integer conversionType;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "转化时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] conversionTime;

}
