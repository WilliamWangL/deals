package com.river.module.campaign.controller.admin.adgroup.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 广告组 创建/修改 Request VO")
@Data
public class AdGroupSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "Campaign ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "Campaign 不能为空")
    private Long campaignId;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "广告组1")
    @NotBlank(message = "名称不能为空")
    private String name;

    @Schema(description = "定向设置（JSON）")
    private String targeting;

    @Schema(description = "出价策略")
    private String bidStrategy;

    @Schema(description = "外部广告组 ID")
    private String externalAdGroupId;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;
}
