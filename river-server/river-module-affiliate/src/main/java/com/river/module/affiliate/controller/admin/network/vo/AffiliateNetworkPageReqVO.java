package com.river.module.affiliate.controller.admin.network.vo;

import com.river.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 联盟网络分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AffiliateNetworkPageReqVO extends PageParam {

    @Schema(description = "联盟编码")
    private String code;

    @Schema(description = "联盟名称")
    private String name;

    @Schema(description = "状态")
    private Integer status;
}
