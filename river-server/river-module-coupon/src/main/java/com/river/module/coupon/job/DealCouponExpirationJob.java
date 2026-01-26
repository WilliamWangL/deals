package com.river.module.coupon.job;

import com.river.framework.quartz.core.handler.JobHandler;
import com.river.framework.tenant.core.job.TenantJob;
import com.river.module.coupon.service.CouponService;
import com.river.module.coupon.service.DealService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Deal/Coupon 过期状态更新任务
 *
 * 功能：
 * - Deal: status=ENABLE(0) 且 endTime < NOW() → 更新为 DISABLE(1)
 * - Coupon: status=ACTIVE(1) 且 endTime < NOW() → 更新为 EXPIRED(2)
 * - endTime 为空的数据不处理
 */
@Slf4j
@Component("dealCouponExpirationJob")
public class DealCouponExpirationJob implements JobHandler {

    @Resource
    private DealService dealService;

    @Resource
    private CouponService couponService;

    @Override
    @TenantJob
    public String execute(String param) throws Exception {
        log.info("[DealCouponExpirationJob] Starting Deal/Coupon expiration check");

        int dealCount = dealService.updateExpiredDeals();
        int couponCount = couponService.updateExpiredCoupons();

        String result = String.format("处理完成，过期 Deal %d 条，Coupon %d 条", dealCount, couponCount);
        log.info("[DealCouponExpirationJob] {}", result);
        return result;
    }

}
