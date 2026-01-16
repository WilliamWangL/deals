package com.river.module.affiliate.controller.admin.merchant.vo;

import com.river.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 商家分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class MerchantPageReqVO extends PageParam {

    @Schema(description = "关联的联盟网络 ID")
    private Long networkId;

    @Schema(description = "商家名称")
    private String name;

    @Schema(description = "商家域名")
    private String domain;

    @Schema(description = "状态")
    private Integer status;
}
