package com.river.module.affiliate.controller.admin.offer.vo;

import com.river.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - Offer 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class OfferPageReqVO extends PageParam {

    @Schema(description = "关联商家 ID")
    private Long merchantId;

    @Schema(description = "关联联盟网络 ID")
    private Long networkId;

    @Schema(description = "Offer 名称")
    private String name;

    @Schema(description = "佣金类型")
    private Integer commissionType;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "是否编辑推荐")
    private Boolean featured;
}
