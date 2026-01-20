package com.river.module.affiliate.controller.app.vo;

import com.river.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户 App - 商家分页 Request VO
 */
@Schema(description = "用户 App - 商家分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppMerchantPageReqVO extends PageParam {

    @Schema(description = "商家名称（模糊查询）")
    private String name;

}
