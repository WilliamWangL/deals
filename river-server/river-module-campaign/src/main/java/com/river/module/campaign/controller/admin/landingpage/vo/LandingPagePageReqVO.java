package com.river.module.campaign.controller.admin.landingpage.vo;

import com.river.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 落地页 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class LandingPagePageReqVO extends PageParam {

    @Schema(description = "名称")
    private String name;

    @Schema(description = "类型")
    private Integer type;

    @Schema(description = "状态")
    private Integer status;
}
