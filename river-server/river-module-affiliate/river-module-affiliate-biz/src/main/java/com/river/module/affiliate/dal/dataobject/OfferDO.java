package com.river.module.affiliate.dal.dataobject;

import com.river.framework.mybatis.core.dataobject.BaseDO;
import com.river.module.affiliate.enums.CommissionTypeEnum;
import com.river.module.affiliate.enums.OfferStatusEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * Offer/广告 DO
 */
@TableName("river_affiliate_offer")
@KeySequence("river_affiliate_offer_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferDO extends BaseDO {

    @TableId
    private Long id;

    /** 关联商家 ID */
    private Long merchantId;

    /** 关联联盟网络 ID */
    private Long networkId;

    /** 联盟侧 Offer ID */
    private String externalId;

    /** Offer 名称 */
    private String name;

    /** Offer 描述 */
    private String description;

    /** 佣金类型 {@link CommissionTypeEnum} */
    private Integer commissionType;

    /** 佣金数值 */
    private BigDecimal commissionValue;

    /** 佣金货币 */
    private String currency;

    /** Cookie 有效期（天） */
    private Integer cookieDays;

    /** 追踪链接模板 */
    private String trackingUrlTemplate;

    /** 落地页 URL */
    private String landingUrl;

    /** 状态 {@link OfferStatusEnum} */
    private Integer status;

    /** 支持的地区（JSON 数组） */
    private String regions;

    /** 分类 ID 列表（JSON 数组） */
    private String categoryIds;

    /** 标签（JSON 数组） */
    private String tags;

    /** 图片 URL */
    private String imageUrl;

    /** EPC（每次点击收益） */
    private BigDecimal epc;

    /** 转化率 */
    private BigDecimal conversionRate;

    /** 是否编辑推荐 */
    private Boolean featured;

    /** 热度分数 */
    private Integer hotScore;
}
