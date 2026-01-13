package com.river.module.campaign.controller.admin.adgroup.vo;

import com.river.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 广告组 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AdGroupPageReqVO extends PageParam {

    @Schema(description = "名称")
    private String name;

    @Schema(description = "Campaign ID")
    private Long campaignId;

    @Schema(description = "状态")
    private Integer status;
}
