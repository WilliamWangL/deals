package com.river.module.campaign.controller.admin.campaign.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - Campaign 创建/修改 Request VO")
@Data
public class CampaignSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "流量来源 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "流量来源不能为空")
    private Long trafficSourceId;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "夏季促销")
    @NotBlank(message = "名称不能为空")
    private String name;

    @Schema(description = "类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "类型不能为空")
    private Integer type;

    @Schema(description = "关联 Offer ID 列表（JSON）", example = "[1, 2, 3]")
    private String offerIds;

    @Schema(description = "落地页 ID", example = "1")
    private Long landingPageId;

    @Schema(description = "日预算", example = "100.00")
    private BigDecimal budgetDaily;

    @Schema(description = "总预算", example = "1000.00")
    private BigDecimal budgetTotal;

    @Schema(description = "外部 Campaign ID", example = "ext_123")
    private String externalCampaignId;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;
}
