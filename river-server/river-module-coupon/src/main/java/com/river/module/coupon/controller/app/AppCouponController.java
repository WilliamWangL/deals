package com.river.module.coupon.controller.app;

import com.river.framework.common.pojo.CommonResult;
import com.river.framework.common.util.collection.CollectionUtils;
import com.river.framework.common.util.object.BeanUtils;
import com.river.module.coupon.controller.app.vo.AppCouponMerchantRespVO;
import com.river.module.coupon.controller.app.vo.AppCouponRespVO;
import com.river.module.coupon.dal.dataobject.CouponDO;
import com.river.module.coupon.dal.dataobject.MerchantDO;
import com.river.module.coupon.dal.mysql.MerchantMapper;
import com.river.module.coupon.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.river.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 App - 优惠券")
@RestController
@RequestMapping("/coupon/coupon")
@Validated
public class AppCouponController {

    @Resource
    private CouponService couponService;

    @Resource
    private MerchantMapper merchantMapper;

    @GetMapping("/list")
    @Operation(summary = "获取优惠券列表")
    public CommonResult<List<AppCouponRespVO>> getCouponList(
            @RequestParam(value = "merchantId", required = false) Long merchantId,
            @RequestParam(value = "verified", required = false) Boolean verified) {
        List<CouponDO> list = couponService.getCouponList();
        // 过滤逻辑
        List<CouponDO> filtered = list.stream()
                .filter(c -> merchantId == null || c.getMerchantId().equals(merchantId))
                .filter(c -> verified == null || c.getVerified().equals(verified))
                .toList();

        // 批量获取商家信息，避免 N+1 问题
        List<Long> merchantIds = filtered.stream()
                .map(CouponDO::getMerchantId)
                .distinct()
                .toList();
        List<MerchantDO> merchants = merchantMapper.selectListByIds(merchantIds);
        Map<Long, MerchantDO> merchantMap = CollectionUtils.convertMap(merchants, MerchantDO::getId);

        // 先建立 index map，避免 O(n²) 复杂度
        Map<Long, CouponDO> couponMap = CollectionUtils.convertMap(filtered, CouponDO::getId);

        // 使用 BeanUtils 转换并填充商家信息
        List<AppCouponRespVO> result = BeanUtils.toBean(filtered, AppCouponRespVO.class, vo -> {
            CouponDO coupon = couponMap.get(vo.getId());
            if (coupon != null) {
                // 填充 gotoUrl（从 CouponDO 映射）
                vo.setGotoUrl(coupon.getGotoUrl());
                // 填充 description（从 terms 字段映射）
                vo.setDescription(coupon.getTerms());
                MerchantDO merchant = merchantMap.get(coupon.getMerchantId());
                if (merchant != null) {
                    vo.setMerchant(BeanUtils.toBean(merchant, AppCouponMerchantRespVO.class));
                    vo.setMerchantName(merchant.getName());
                    vo.setMerchantLogo(merchant.getLogoUrl());
                }
            }
        });
        return success(result);
    }

}
