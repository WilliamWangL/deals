package com.river.module.affiliate.dal.dataobject;

import com.river.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * 商家/广告主 DO
 */
@TableName("river_affiliate_merchant")
@KeySequence("river_affiliate_merchant_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantDO extends TenantBaseDO {

    @TableId
    private Long id;

    /** 关联的联盟网络 ID */
    private Long networkId;

    /** 联盟侧商家 ID */
    private String externalId;

    /** 商家名称 */
    private String name;

    /** 商家域名 */
    private String domain;

    /** Logo URL */
    private String logoUrl;

    /** 商家描述 */
    private String description;

    /** 商家评级（1-5） */
    private BigDecimal rating;

    /** 状态：0-停用，1-启用 */
    private Integer status;

    /** 支持的国家/地区（JSON 数组） */
    private String regions;

    /** 分类 ID 列表（JSON 数组） */
    private String categoryIds;
}
