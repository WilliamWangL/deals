package com.river.module.campaign.controller.admin.trafficsource.vo;

import com.river.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 流量来源 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class TrafficSourcePageReqVO extends PageParam {

    @Schema(description = "编码")
    private String code;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "状态")
    private Integer status;
}
