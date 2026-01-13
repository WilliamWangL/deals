package com.river.module.affiliate.controller.admin.network.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 联盟网络创建/修改 Request VO")
@Data
public class AffiliateNetworkSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "联盟编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "admitad")
    @NotBlank(message = "联盟编码不能为空")
    private String code;

    @Schema(description = "联盟名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "Admitad")
    @NotBlank(message = "联盟名称不能为空")
    private String name;

    @Schema(description = "联盟类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "联盟类型不能为空")
    private Integer type;

    @Schema(description = "API 基础地址", example = "https://api.admitad.com")
    private String apiBaseUrl;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "联盟官网", example = "https://www.admitad.com")
    private String websiteUrl;

    @Schema(description = "Logo URL")
    private String logoUrl;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "备注")
    private String remark;
}
