package com.river.module.campaign.controller.admin.landingpage.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 落地页 Response VO")
@Data
public class LandingPageRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "名称", example = "夏季促销页")
    private String name;

    @Schema(description = "Slug", example = "summer-sale")
    private String slug;

    @Schema(description = "类型", example = "1")
    private Integer type;

    @Schema(description = "外链 URL")
    private String url;

    @Schema(description = "关联 Offer ID")
    private Long offerId;

    @Schema(description = "页面内容")
    private String content;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
