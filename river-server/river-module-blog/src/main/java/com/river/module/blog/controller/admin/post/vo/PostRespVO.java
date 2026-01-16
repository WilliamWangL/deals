package com.river.module.blog.controller.admin.post.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 文章 Response VO")
@Data
public class PostRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "作者 ID", example = "1")
    private Long authorId;

    @Schema(description = "标题", example = "最新优惠汇总")
    private String title;

    @Schema(description = "Slug", example = "best-deals-2024")
    private String slug;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "摘要")
    private String excerpt;

    @Schema(description = "封面图")
    private String coverImage;

    @Schema(description = "类型", example = "1")
    private Integer type;

    @Schema(description = "状态", example = "0")
    private Integer status;

    @Schema(description = "发布时间")
    private LocalDateTime publishedAt;

    @Schema(description = "SEO 标题")
    private String metaTitle;

    @Schema(description = "SEO 描述")
    private String metaDescription;

    @Schema(description = "规范链接")
    private String canonicalUrl;

    @Schema(description = "浏览次数")
    private Integer viewCount;

    @Schema(description = "是否推荐", example = "false")
    private Boolean featured;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
