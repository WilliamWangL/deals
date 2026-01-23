package com.river.module.tracking.controller.admin.trackinglink.vo;

import com.river.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 追踪链接分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class TrackingLinkPageReqVO extends PageParam {

    @Schema(description = "目标类型", example = "2")
    private Integer targetType;

    @Schema(description = "目标 ID")
    private Long targetId;

    @Schema(description = "链接 Slug")
    private String slug;

    @Schema(description = "状态")
    private Integer status;

}
