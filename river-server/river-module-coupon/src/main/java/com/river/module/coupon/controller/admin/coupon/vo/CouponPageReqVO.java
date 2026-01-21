package com.river.module.coupon.controller.admin.coupon.vo;

import com.river.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Schema(description = "管理后台 - 优惠券分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CouponPageReqVO extends PageParam {

    @Schema(description = "商家 ID", example = "1")
    private Long merchantId;

    @Schema(description = "Offer ID", example = "1")
    private Long offerId;

    @Schema(description = "优惠码", example = "SAVE20")
    private String code;

    @Schema(description = "折扣类型", example = "1")
    private Integer discountType;

    @Schema(description = "来源", example = "1")
    private Integer source;

    @Schema(description = "是否已验证", example = "true")
    private Boolean verified;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "地区列表（支持多地区查询）")
    private List<String> regions;
}
