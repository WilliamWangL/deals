package com.river.module.campaign.controller.admin.trafficsource.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 流量来源 Response VO")
@Data
public class TrafficSourceRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "编码", example = "google")
    private String code;

    @Schema(description = "名称", example = "Google Ads")
    private String name;

    @Schema(description = "API 凭证配置（JSON）")
    private String apiCredentials;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
