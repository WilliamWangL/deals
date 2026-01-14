package com.river.module.coupon.api.statistics;

import com.river.module.coupon.dal.mysql.CouponMapper;
import com.river.module.coupon.dal.mysql.DealMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class CouponStatisticsApiImpl implements CouponStatisticsApi {

    @Resource
    private DealMapper dealMapper;

    @Resource
    private CouponMapper couponMapper;

    @Override
    public Long getDealCountByMerchantId(Long merchantId) {
        return dealMapper.selectCountByMerchantId(merchantId);
    }

    @Override
    public Long getCouponCountByMerchantId(Long merchantId) {
        return couponMapper.selectCountByMerchantId(merchantId);
    }

}
