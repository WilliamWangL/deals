package com.river.module.campaign.controller.admin.campaign.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - Campaign Response VO")
@Data
public class CampaignRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "流量来源 ID", example = "1")
    private Long trafficSourceId;

    @Schema(description = "名称", example = "夏季促销")
    private String name;

    @Schema(description = "类型", example = "1")
    private Integer type;

    @Schema(description = "关联 Offer ID 列表（JSON）")
    private String offerIds;

    @Schema(description = "落地页 ID", example = "1")
    private Long landingPageId;

    @Schema(description = "日预算", example = "100.00")
    private BigDecimal budgetDaily;

    @Schema(description = "总预算", example = "1000.00")
    private BigDecimal budgetTotal;

    @Schema(description = "外部 Campaign ID", example = "ext_123")
    private String externalCampaignId;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
