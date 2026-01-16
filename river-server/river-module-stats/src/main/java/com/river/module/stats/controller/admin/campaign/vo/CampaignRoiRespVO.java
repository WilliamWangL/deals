package com.river.module.stats.controller.admin.campaign.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - Campaign ROI 分析 Response VO")
@Data
public class CampaignRoiRespVO {

    @Schema(description = "Campaign ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long campaignId;

    @Schema(description = "Campaign 名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "夏季促销")
    private String campaignName;

    @Schema(description = "总点击数", requiredMode = Schema.RequiredMode.REQUIRED, example = "10000")
    private Integer totalClicks;

    @Schema(description = "总转化数", requiredMode = Schema.RequiredMode.REQUIRED, example = "500")
    private Integer totalConversions;

    @Schema(description = "总收入", requiredMode = Schema.RequiredMode.REQUIRED, example = "9999.99")
    private BigDecimal totalRevenue;

    @Schema(description = "总成本", requiredMode = Schema.RequiredMode.REQUIRED, example = "5000.00")
    private BigDecimal totalCost;

    @Schema(description = "总利润", requiredMode = Schema.RequiredMode.REQUIRED, example = "4999.99")
    private BigDecimal totalProfit;

    @Schema(description = "ROI", requiredMode = Schema.RequiredMode.REQUIRED, example = "99.99")
    private BigDecimal roi;

    @Schema(description = "EPC", requiredMode = Schema.RequiredMode.REQUIRED, example = "0.99")
    private BigDecimal epc;

    @Schema(description = "转化率", requiredMode = Schema.RequiredMode.REQUIRED, example = "5.00")
    private BigDecimal cr;

}
