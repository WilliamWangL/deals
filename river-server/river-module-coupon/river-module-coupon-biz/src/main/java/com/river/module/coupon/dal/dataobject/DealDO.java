package com.river.module.coupon.dal.dataobject;

import com.river.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("river_coupon_deal")
@KeySequence("river_coupon_deal_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealDO extends BaseDO {

    @TableId
    private Long id;

    private Long merchantId;

    private Long offerId;

    private String title;

    private String description;

    private BigDecimal originalPrice;

    private BigDecimal dealPrice;

    private Integer discountPercent;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer stockLimit;

    private String imageUrl;

    private Integer hotScore;

    private Boolean featured;

    private Integer status;
}
