package com.river.module.coupon.controller.app;

import com.river.framework.common.pojo.CommonResult;
import com.river.module.coupon.controller.app.vo.AppCouponRespVO;
import com.river.module.coupon.dal.dataobject.CouponDO;
import com.river.module.coupon.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.river.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 App - 优惠券")
@RestController
@RequestMapping("/coupon/coupon")
@Validated
public class AppCouponController {

    @Resource
    private CouponService couponService;

    @GetMapping("/list")
    @Operation(summary = "获取优惠券列表")
    public CommonResult<List<AppCouponRespVO>> getCouponList(
            @RequestParam(value = "merchantId", required = false) Long merchantId,
            @RequestParam(value = "verified", required = false) Boolean verified) {
        List<CouponDO> list = couponService.getCouponList();
        List<AppCouponRespVO> result = list.stream()
                .filter(c -> merchantId == null || c.getMerchantId().equals(merchantId))
                .filter(c -> verified == null || c.getVerified().equals(verified))
                .map(this::convertToAppVO)
                .toList();
        return success(result);
    }

    private AppCouponRespVO convertToAppVO(CouponDO coupon) {
        if (coupon == null) {
            return null;
        }
        AppCouponRespVO vo = new AppCouponRespVO();
        vo.setId(coupon.getId());
        vo.setCode(coupon.getCode());
        vo.setDescription(coupon.getTerms());
        vo.setDiscountType(coupon.getDiscountType());
        vo.setDiscountValue(coupon.getDiscountValue());
        vo.setMinPurchase(coupon.getMinPurchase());
        vo.setMerchantId(coupon.getMerchantId());
        vo.setEndTime(coupon.getEndTime());
        vo.setVerified(coupon.getVerified());
        return vo;
    }

}
