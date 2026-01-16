package com.river.module.blog.controller.admin.post.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 文章创建/修改 Request VO")
@Data
public class PostSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "作者 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "作者不能为空")
    private Long authorId;

    @Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "最新优惠汇总")
    @NotBlank(message = "标题不能为空")
    private String title;

    @Schema(description = "Slug", requiredMode = Schema.RequiredMode.REQUIRED, example = "best-deals-2024")
    @NotBlank(message = "Slug 不能为空")
    private String slug;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "摘要")
    private String excerpt;

    @Schema(description = "封面图")
    private String coverImage;

    @Schema(description = "类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "类型不能为空")
    private Integer type;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "发布时间")
    private LocalDateTime publishedAt;

    @Schema(description = "SEO 标题")
    private String metaTitle;

    @Schema(description = "SEO 描述")
    private String metaDescription;

    @Schema(description = "规范链接")
    private String canonicalUrl;

    @Schema(description = "是否推荐", example = "false")
    private Boolean featured;
}
