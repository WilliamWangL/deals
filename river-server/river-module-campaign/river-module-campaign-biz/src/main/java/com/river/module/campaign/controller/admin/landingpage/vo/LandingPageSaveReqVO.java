package com.river.module.campaign.controller.admin.landingpage.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 落地页 创建/修改 Request VO")
@Data
public class LandingPageSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "夏季促销页")
    @NotBlank(message = "名称不能为空")
    private String name;

    @Schema(description = "Slug", requiredMode = Schema.RequiredMode.REQUIRED, example = "summer-sale")
    @NotBlank(message = "Slug 不能为空")
    private String slug;

    @Schema(description = "类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "类型不能为空")
    private Integer type;

    @Schema(description = "外链 URL")
    private String url;

    @Schema(description = "关联 Offer ID")
    private Long offerId;

    @Schema(description = "页面内容（内置类型）")
    private String content;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;
}
