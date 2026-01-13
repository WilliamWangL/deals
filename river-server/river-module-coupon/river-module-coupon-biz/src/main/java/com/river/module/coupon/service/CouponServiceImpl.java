package com.river.module.coupon.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.coupon.controller.admin.coupon.vo.CouponPageReqVO;
import com.river.module.coupon.dal.dataobject.CouponDO;
import com.river.module.coupon.dal.mysql.CouponMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.river.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.river.module.coupon.enums.ErrorCodeConstants.*;

@Service
@Validated
public class CouponServiceImpl implements CouponService {

    @Resource
    private CouponMapper couponMapper;

    @Override
    public Long createCoupon(CouponDO coupon) {
        couponMapper.insert(coupon);
        return coupon.getId();
    }

    @Override
    public void updateCoupon(CouponDO coupon) {
        validateCouponExists(coupon.getId());
        couponMapper.updateById(coupon);
    }

    @Override
    public void deleteCoupon(Long id) {
        validateCouponExists(id);
        couponMapper.deleteById(id);
    }

    @Override
    public CouponDO getCoupon(Long id) {
        return couponMapper.selectById(id);
    }

    @Override
    public List<CouponDO> getCouponList() {
        return couponMapper.selectList();
    }

    @Override
    public PageResult<CouponDO> getCouponPage(CouponPageReqVO pageReqVO) {
        return couponMapper.selectPage(pageReqVO);
    }

    @Override
    public void validateCouponExists(Long id) {
        if (couponMapper.selectById(id) == null) {
            throw exception(COUPON_NOT_EXISTS);
        }
    }

}
