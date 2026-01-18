package com.river.module.stats.service.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DimensionAggregateDTO {
    private Long dimensionId;
    private String dimensionName;
    private Integer clicks;
    private Integer conversions;
    private BigDecimal revenue;
}
