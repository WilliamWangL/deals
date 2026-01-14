package com.river.module.blog.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 文章 Response VO")
@Data
public class AppPostRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "Best Deals 2024")
    private String title;

    @Schema(description = "URL Slug", requiredMode = Schema.RequiredMode.REQUIRED, example = "best-deals-2024")
    private String slug;

    @Schema(description = "内容 (Markdown)")
    private String content;

    @Schema(description = "摘要")
    private String excerpt;

    @Schema(description = "封面图 URL")
    private String coverImage;

    @Schema(description = "文章类型: 1=deal 2=review 3=tutorial 4=news", example = "1")
    private Integer type;

    @Schema(description = "作者名称")
    private String authorName;

    @Schema(description = "作者头像")
    private String authorAvatar;

    @Schema(description = "发布时间")
    private LocalDateTime publishedAt;

    @Schema(description = "浏览次数")
    private Integer viewCount;

    @Schema(description = "是否推荐")
    private Boolean featured;

    @Schema(description = "SEO 标题")
    private String metaTitle;

    @Schema(description = "SEO 描述")
    private String metaDescription;

}
