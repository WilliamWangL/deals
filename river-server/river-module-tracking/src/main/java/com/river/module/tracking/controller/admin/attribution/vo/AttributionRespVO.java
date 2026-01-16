package com.river.module.tracking.controller.admin.attribution.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 归因记录 Response VO")
@Data
public class AttributionRespVO {

    @Schema(description = "主键 ID", example = "1")
    private Long id;

    @Schema(description = "转化 ID", example = "1")
    private Long conversionId;

    @Schema(description = "点击 ID", example = "01HXYZ...")
    private String clickId;

    @Schema(description = "归因类型", example = "1")
    private Integer attributionType;

    @Schema(description = "置信度 (0-100)", example = "100")
    private Integer confidenceScore;

    @Schema(description = "归因窗口（毫秒）", example = "86400000")
    private Long attributionWindow;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
