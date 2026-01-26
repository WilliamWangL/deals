package com.river.module.coupon.controller.app.vo;

import com.river.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Schema(description = "用户 App - 优惠券分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppCouponPageReqVO extends PageParam {

    @Schema(description = "商家 ID")
    private Long merchantId;

    @Schema(description = "是否已验证")
    private Boolean verified;

    @Schema(description = "地区列表（支持多地区查询）")
    private List<String> regions;

    @Schema(description = "分类 ID")
    private Long categoryId;

}
