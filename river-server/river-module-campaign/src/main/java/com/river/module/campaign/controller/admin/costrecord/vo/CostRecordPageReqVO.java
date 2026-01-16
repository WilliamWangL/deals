package com.river.module.campaign.controller.admin.costrecord.vo;

import com.river.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Schema(description = "管理后台 - 成本记录 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CostRecordPageReqVO extends PageParam {

    @Schema(description = "Campaign ID")
    private Long campaignId;

    @Schema(description = "广告组 ID")
    private Long adGroupId;

    @Schema(description = "来源")
    private Integer source;

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;
}
