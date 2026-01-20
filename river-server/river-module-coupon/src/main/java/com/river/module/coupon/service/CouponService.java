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

    /**
     * 根据地区获得优惠券分页
     *
     * @param pageReqVO 分页查询
     * @param region 地区代码
     * @return 优惠券分页
     */
    PageResult<CouponDO> getCouponPageByRegion(CouponPageReqVO pageReqVO, String region);

    /**
     * 根据地区获得优惠券列表
     *
     * @param region 地区代码
     * @return 优惠券列表
     */
    List<CouponDO> getCouponListByRegion(String region);

}
