package com.river.module.campaign.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.river.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

@TableName("river_campaign_currency")
@KeySequence("river_campaign_currency_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyDO extends BaseDO {

    @TableId
    private Long id;

    private String code;

    private String name;

    private String symbol;

    private Integer decimalPlaces;

    private Integer status;
}
