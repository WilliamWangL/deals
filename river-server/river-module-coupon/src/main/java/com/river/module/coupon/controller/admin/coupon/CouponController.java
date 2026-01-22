package com.river.module.coupon.controller.admin.coupon;

import com.river.framework.common.pojo.CommonResult;
import com.river.framework.common.pojo.PageResult;
import com.river.module.coupon.controller.admin.coupon.vo.CouponPageReqVO;
import com.river.module.coupon.controller.admin.coupon.vo.CouponRespVO;
import com.river.module.coupon.controller.admin.coupon.vo.CouponSaveReqVO;
import com.river.framework.common.util.object.BeanUtils;
import com.river.module.coupon.dal.dataobject.CouponDO;
import com.river.module.coupon.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.river.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 优惠券")
@RestController
@RequestMapping("/coupon/coupon")
@Validated
public class CouponController {

    @Resource
    private CouponService couponService;

    @PostMapping("/create")
    @Operation(summary = "创建优惠券")
    @PreAuthorize("@ss.hasPermission('coupon:coupon:create')")
    public CommonResult<Long> createCoupon(@Valid @RequestBody CouponSaveReqVO createReqVO) {
        return success(couponService.createCoupon(BeanUtils.toBean(createReqVO, CouponDO.class)));
    }

    @PutMapping("/update")
    @Operation(summary = "更新优惠券")
    @PreAuthorize("@ss.hasPermission('coupon:coupon:update')")
    public CommonResult<Boolean> updateCoupon(@Valid @RequestBody CouponSaveReqVO updateReqVO) {
        couponService.updateCoupon(BeanUtils.toBean(updateReqVO, CouponDO.class));
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除优惠券")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('coupon:coupon:delete')")
    public CommonResult<Boolean> deleteCoupon(@RequestParam("id") Long id) {
        couponService.deleteCoupon(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取优惠券")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('coupon:coupon:query')")
    public CommonResult<CouponRespVO> getCoupon(@RequestParam("id") Long id) {
        CouponDO coupon = couponService.getCoupon(id);
        return success(BeanUtils.toBean(coupon, CouponRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获取优惠券列表")
    @PreAuthorize("@ss.hasPermission('coupon:coupon:query')")
    public CommonResult<List<CouponRespVO>> getCouponList() {
        List<CouponDO> list = couponService.getCouponList();
        return success(BeanUtils.toBean(list, CouponRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获取优惠券分页")
    @PreAuthorize("@ss.hasPermission('coupon:coupon:query')")
    public CommonResult<PageResult<CouponRespVO>> getCouponPage(@Valid CouponPageReqVO pageReqVO) {
        PageResult<CouponDO> pageResult = couponService.getCouponPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, CouponRespVO.class));
    }
}
