package com.river.module.campaign.controller.admin.trafficsource.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 流量来源 创建/修改 Request VO")
@Data
public class TrafficSourceSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "google")
    @NotBlank(message = "编码不能为空")
    private String code;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "Google Ads")
    @NotBlank(message = "名称不能为空")
    private String name;

    @Schema(description = "API 凭证配置（JSON）")
    private String apiCredentials;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;
}
