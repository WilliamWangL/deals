package com.river.module.campaign.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.river.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

import java.math.BigDecimal;

@TableName("river_campaign_campaign")
@KeySequence("river_campaign_campaign_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignDO extends BaseDO {

    @TableId
    private Long id;

    private Long trafficSourceId;

    private String name;

    private Integer type;

    private String offerIds;

    private Long landingPageId;

    private BigDecimal budgetDaily;

    private BigDecimal budgetTotal;

    private String externalCampaignId;

    private Integer status;
}
