package com.river.module.coupon.api.statistics;

import java.util.List;
import java.util.Map;

public interface CouponStatisticsApi {

    Long getDealCountByMerchantId(Long merchantId);

    Long getCouponCountByMerchantId(Long merchantId);

    Map<Long, Long> getDealCountsByMerchantIds(List<Long> merchantIds);

    Map<Long, Long> getCouponCountsByMerchantIds(List<Long> merchantIds);

}
