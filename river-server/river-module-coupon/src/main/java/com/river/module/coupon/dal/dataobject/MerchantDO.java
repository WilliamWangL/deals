package com.river.module.coupon.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 商家 DO（只读）
 *
 * 用于 coupon 模块查询商家信息，避免与 affiliate 模块的循环依赖
 */
@TableName("river_affiliate_merchant")
@Data
public class MerchantDO {

    @TableId
    private Long id;

    /** 商家名称 */
    private String name;

    /** URL友好标识 */
    private String slug;

    /** Logo URL */
    private String logoUrl;

    /** 是否删除 */
    @TableLogic
    private Boolean deleted;

}
