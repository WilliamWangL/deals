package com.river.module.blog.controller.admin.author.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 作者创建/修改 Request VO")
@Data
public class AuthorSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotBlank(message = "名称不能为空")
    private String name;

    @Schema(description = "Slug", requiredMode = Schema.RequiredMode.REQUIRED, example = "zhang-san")
    @NotBlank(message = "Slug 不能为空")
    private String slug;

    @Schema(description = "头像 URL")
    private String avatarUrl;

    @Schema(description = "简介")
    private String bio;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;
}
