package com.river.module.tracking.controller.admin.conversion.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 转化记录 Response VO")
@Data
public class ConversionRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "点击 ID", example = "01HXYZ...")
    private String clickId;

    @Schema(description = "联盟网络编码", example = "cj")
    private String networkCode;

    @Schema(description = "外部转化 ID", example = "ext_123")
    private String externalConversionId;

    @Schema(description = "转化类型", example = "1")
    private Integer conversionType;

    @Schema(description = "佣金", example = "10.00")
    private BigDecimal commission;

    @Schema(description = "货币", example = "USD")
    private String currency;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "网络回传数据")
    private String networkPayload;

    @Schema(description = "转化时间")
    private LocalDateTime conversionTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
