package com.river.module.coupon.service;

import com.river.module.coupon.api.statistics.CouponStatisticsApi;
import com.river.module.coupon.dal.mysql.CouponMapper;
import com.river.module.coupon.dal.mysql.DealMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public Map<Long, Long> getDealCountsByMerchantIds(List<Long> merchantIds) {
        if (merchantIds == null || merchantIds.isEmpty()) {
            return new HashMap<>();
        }
        Map<Long, Long> result = new HashMap<>();
        for (Long merchantId : merchantIds) {
            result.put(merchantId, dealMapper.selectCountByMerchantId(merchantId));
        }
        return result;
    }

    @Override
    public Map<Long, Long> getCouponCountsByMerchantIds(List<Long> merchantIds) {
        if (merchantIds == null || merchantIds.isEmpty()) {
            return new HashMap<>();
        }
        Map<Long, Long> result = new HashMap<>();
        for (Long merchantId : merchantIds) {
            result.put(merchantId, couponMapper.selectCountByMerchantId(merchantId));
        }
        return result;
    }

}
