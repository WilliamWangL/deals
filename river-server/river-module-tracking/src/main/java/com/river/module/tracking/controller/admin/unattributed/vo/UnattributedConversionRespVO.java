package com.river.module.tracking.controller.admin.unattributed.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 未归因转化 Response VO")
@Data
public class UnattributedConversionRespVO {

    @Schema(description = "主键 ID", example = "1")
    private Long id;

    @Schema(description = "网络代码", example = "impact")
    private String networkCode;

    @Schema(description = "外部转化 ID")
    private String externalConversionId;

    @Schema(description = "转化类型", example = "2")
    private Integer conversionType;

    @Schema(description = "佣金", example = "10.50")
    private BigDecimal commission;

    @Schema(description = "货币", example = "USD")
    private String currency;

    @Schema(description = "网络负载数据")
    private String networkPayload;

    @Schema(description = "原始请求数据")
    private String rawRequest;

    @Schema(description = "归因失败原因")
    private String attributionFailReason;

    @Schema(description = "转化时间")
    private LocalDateTime conversionTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
