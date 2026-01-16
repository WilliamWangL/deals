package com.river.module.blog.controller.admin.tag.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 标签 Response VO")
@Data
public class TagRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "名称", example = "优惠")
    private String name;

    @Schema(description = "Slug", example = "deals")
    private String slug;

    @Schema(description = "文章数量", example = "10")
    private Integer postCount;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
