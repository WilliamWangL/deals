package com.river.module.campaign.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.river.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

@TableName("river_campaign_traffic_source")
@KeySequence("river_campaign_traffic_source_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrafficSourceDO extends BaseDO {

    @TableId
    private Long id;

    private String code;

    private String name;

    private String apiCredentials;

    private Integer status;
}
