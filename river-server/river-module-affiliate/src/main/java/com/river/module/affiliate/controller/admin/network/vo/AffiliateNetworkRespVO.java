package com.river.module.affiliate.controller.admin.network.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 联盟网络 Response VO")
@Data
public class AffiliateNetworkRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "联盟编码", example = "admitad")
    private String code;

    @Schema(description = "联盟名称", example = "Admitad")
    private String name;

    @Schema(description = "联盟类型", example = "1")
    private Integer type;

    @Schema(description = "API 基础地址")
    private String apiBaseUrl;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "联盟官网")
    private String websiteUrl;

    @Schema(description = "Logo URL")
    private String logoUrl;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
