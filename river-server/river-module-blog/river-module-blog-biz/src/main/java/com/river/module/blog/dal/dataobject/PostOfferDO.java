package com.river.module.blog.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.river.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

@TableName("river_blog_post_offer")
@KeySequence("river_blog_post_offer_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostOfferDO extends BaseDO {

    @TableId
    private Long id;

    private Long postId;

    private Long offerId;

    private String anchorText;

    private Integer position;
}
