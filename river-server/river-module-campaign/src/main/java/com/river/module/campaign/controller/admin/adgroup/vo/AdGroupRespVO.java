package com.river.module.campaign.controller.admin.adgroup.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 广告组 Response VO")
@Data
public class AdGroupRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "Campaign ID", example = "1")
    private Long campaignId;

    @Schema(description = "名称", example = "广告组1")
    private String name;

    @Schema(description = "定向设置（JSON）")
    private String targeting;

    @Schema(description = "出价策略")
    private String bidStrategy;

    @Schema(description = "外部广告组 ID")
    private String externalAdGroupId;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
