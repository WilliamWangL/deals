package com.river.module.affiliate.controller.admin.offer.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - Offer 创建/修改 Request VO")
@Data
public class OfferSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "关联商家 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "商家不能为空")
    private Long merchantId;

    @Schema(description = "关联联盟网络 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "联盟网络不能为空")
    private Long networkId;

    @Schema(description = "联盟侧 Offer ID", example = "12345")
    private String externalId;

    @Schema(description = "Offer 名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "新用户首单优惠")
    @NotBlank(message = "Offer 名称不能为空")
    private String name;

    @Schema(description = "Offer 描述")
    private String description;

    @Schema(description = "佣金类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "佣金类型不能为空")
    private Integer commissionType;

    @Schema(description = "佣金数值", example = "10.00")
    private BigDecimal commissionValue;

    @Schema(description = "佣金货币", example = "USD")
    private String currency;

    @Schema(description = "Cookie 有效期（天）", example = "30")
    private Integer cookieDays;

    @Schema(description = "追踪链接模板")
    private String trackingUrlTemplate;

    @Schema(description = "落地页 URL")
    private String landingUrl;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "支持的地区（JSON 数组）", example = "[\"US\", \"UK\"]")
    private String regions;

    @Schema(description = "分类 ID 列表（JSON 数组）", example = "[1, 2, 3]")
    private String categoryIds;

    @Schema(description = "标签（JSON 数组）", example = "[\"hot\", \"new\"]")
    private String tags;

    @Schema(description = "图片 URL")
    private String imageUrl;

    @Schema(description = "EPC（每次点击收益）", example = "0.50")
    private BigDecimal epc;

    @Schema(description = "转化率", example = "0.05")
    private BigDecimal conversionRate;

    @Schema(description = "是否编辑推荐", example = "false")
    private Boolean featured;

    @Schema(description = "热度分数", example = "100")
    private Integer hotScore;
}
