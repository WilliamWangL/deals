package com.river.module.tracking.controller.admin.attribution.vo;

import com.river.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.river.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 归因记录分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AttributionPageReqVO extends PageParam {

    @Schema(description = "转化 ID", example = "1")
    private Long conversionId;

    @Schema(description = "点击 ID", example = "01HXYZ...")
    private String clickId;

    @Schema(description = "归因类型", example = "1")
    private Integer attributionType;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
