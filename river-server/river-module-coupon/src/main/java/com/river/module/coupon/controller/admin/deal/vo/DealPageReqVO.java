package com.river.module.coupon.controller.admin.deal.vo;

import com.river.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Schema(description = "管理后台 - Deal 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DealPageReqVO extends PageParam {

    @Schema(description = "商家 ID", example = "1")
    private Long merchantId;

    @Schema(description = "Offer ID", example = "1")
    private Long offerId;

    @Schema(description = "标题", example = "50% Off")
    private String title;

    @Schema(description = "是否精选", example = "true")
    private Boolean featured;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "地区列表（支持多地区查询）")
    private List<String> regions;
}
