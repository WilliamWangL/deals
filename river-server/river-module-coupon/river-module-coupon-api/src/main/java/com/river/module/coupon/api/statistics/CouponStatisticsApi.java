package com.river.module.coupon.api.statistics;

/**
 * 优惠券模块统计 API 接口
 * 供其他模块（如 affiliate）调用
 */
public interface CouponStatisticsApi {

    /**
     * 根据商家 ID 获取 Deal 数量
     */
    Long getDealCountByMerchantId(Long merchantId);

    /**
     * 根据商家 ID 获取 Coupon 数量
     */
    Long getCouponCountByMerchantId(Long merchantId);

}
