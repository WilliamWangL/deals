package com.river.module.tracking.dal.dataobject;

import com.river.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

@TableName("river_tracking_click")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClickDO extends BaseDO {

    @TableId
    private String clickId;

    private Long offerId;

    private Long campaignId;

    private Long landingPageId;

    private String sub1;

    private String sub2;

    private String sub3;

    private String sub4;

    private String sub5;

    private String ip;

    private String userAgent;

    private String referer;

    private String deviceType;

    private String country;

    private LocalDateTime clickTime;

}
