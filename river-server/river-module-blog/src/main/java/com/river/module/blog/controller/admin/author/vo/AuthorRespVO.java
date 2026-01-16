package com.river.module.blog.controller.admin.author.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 作者 Response VO")
@Data
public class AuthorRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "名称", example = "张三")
    private String name;

    @Schema(description = "Slug", example = "zhang-san")
    private String slug;

    @Schema(description = "头像 URL")
    private String avatarUrl;

    @Schema(description = "简介")
    private String bio;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
