package com.river.module.tracking.dal.dataobject;

import com.river.framework.mybatis.core.dataobject.BaseDO;
import com.river.module.tracking.enums.TrackingLinkStatusEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("river_tracking_link")
@KeySequence("river_tracking_link_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingLinkDO extends BaseDO {

    @TableId
    private Long id;

    private Long offerId;

    private String slug;

    private String presetSub1;

    private String presetSub2;

    private String presetSub3;

    private String presetSub4;

    private String presetSub5;

    private String utmParams;

    /** {@link TrackingLinkStatusEnum} */
    private Integer status;

}
