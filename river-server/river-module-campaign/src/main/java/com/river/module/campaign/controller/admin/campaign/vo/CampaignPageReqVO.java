package com.river.module.campaign.controller.admin.campaign.vo;

import com.river.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - Campaign 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CampaignPageReqVO extends PageParam {

    @Schema(description = "名称")
    private String name;

    @Schema(description = "流量来源 ID")
    private Long trafficSourceId;

    @Schema(description = "类型")
    private Integer type;

    @Schema(description = "状态")
    private Integer status;
}
