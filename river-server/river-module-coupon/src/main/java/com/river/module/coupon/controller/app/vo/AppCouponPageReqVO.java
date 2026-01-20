package com.river.module.coupon.controller.app.vo;

import com.river.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "用户 App - 优惠券分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppCouponPageReqVO extends PageParam {

    @Schema(description = "商家 ID")
    private Long merchantId;

    @Schema(description = "是否已验证")
    private Boolean verified;

}
