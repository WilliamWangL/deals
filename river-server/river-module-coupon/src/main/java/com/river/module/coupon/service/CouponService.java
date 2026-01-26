package com.river.module.coupon.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.coupon.controller.admin.coupon.vo.CouponPageReqVO;
import com.river.module.coupon.dal.dataobject.CouponDO;

import java.util.List;

public interface CouponService {

    Long createCoupon(CouponDO coupon);

    void updateCoupon(CouponDO coupon);

    void deleteCoupon(Long id);

    CouponDO getCoupon(Long id);

    List<CouponDO> getCouponList();

    PageResult<CouponDO> getCouponPage(CouponPageReqVO pageReqVO);

    void validateCouponExists(Long id);

    int updateExpiredCoupons();

}
