package com.river.module.affiliate.controller.app.region.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户 App - 可用地区 Response VO
 */
@Schema(description = "用户 App - 可用地区 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegionRespVO {

    @Schema(description = "国家代码", example = "GLOBAL")
    private String code;

    @Schema(description = "国家名称", example = "Global")
    private String name;
}
