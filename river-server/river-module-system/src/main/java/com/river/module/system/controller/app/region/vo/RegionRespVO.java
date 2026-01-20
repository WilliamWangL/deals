package com.river.module.system.controller.app.region.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "用户 App - 可用地区 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegionRespVO {

    @Schema(description = "国家代码", example = "US")
    private String code;

    @Schema(description = "国家名称", example = "United States")
    private String name;

    @Schema(description = "数据数量", example = "45")
    private Integer count;
}
