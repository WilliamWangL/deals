package com.river.module.campaign.controller.admin.currency.vo;

import com.river.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 货币 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CurrencyPageReqVO extends PageParam {

    @Schema(description = "货币代码")
    private String code;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "状态")
    private Integer status;
}
