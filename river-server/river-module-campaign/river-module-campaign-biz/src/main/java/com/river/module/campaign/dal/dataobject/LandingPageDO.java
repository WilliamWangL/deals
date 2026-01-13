package com.river.module.campaign.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.river.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

@TableName("river_campaign_landing_page")
@KeySequence("river_campaign_landing_page_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LandingPageDO extends TenantBaseDO {

    @TableId
    private Long id;

    private String name;

    private String slug;

    private Integer type;

    private String url;

    private Long offerId;

    private String content;

    private Integer status;
}
