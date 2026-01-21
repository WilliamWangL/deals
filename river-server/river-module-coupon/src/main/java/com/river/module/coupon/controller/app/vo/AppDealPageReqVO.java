package com.river.module.coupon.controller.app.vo;

import com.river.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Schema(description = "用户 App - Deal 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppDealPageReqVO extends PageParam {

    @Schema(description = "商家 ID")
    private Long merchantId;

    @Schema(description = "是否精选")
    private Boolean featured;

    @Schema(description = "地区列表（支持多地区查询）")
    private List<String> regions;

}
