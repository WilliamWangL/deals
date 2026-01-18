package com.river.module.coupon.controller.app;

import com.river.framework.common.pojo.CommonResult;
import com.river.framework.common.util.collection.CollectionUtils;
import com.river.framework.common.util.object.BeanUtils;
import com.river.module.coupon.controller.app.vo.AppDealMerchantRespVO;
import com.river.module.coupon.controller.app.vo.AppDealRespVO;
import com.river.module.coupon.dal.dataobject.DealDO;
import com.river.module.coupon.dal.dataobject.MerchantDO;
import com.river.module.coupon.dal.mysql.MerchantMapper;
import com.river.module.coupon.service.DealService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.river.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 App - Deal")
@RestController
@RequestMapping("/coupon/deal")
@Validated
public class AppDealController {

    @Resource
    private DealService dealService;

    @Resource
    private MerchantMapper merchantMapper;

    @GetMapping("/list")
    @Operation(summary = "获取 Deal 列表")
    public CommonResult<List<AppDealRespVO>> getDealList(
            @RequestParam(value = "merchantId", required = false) Long merchantId,
            @RequestParam(value = "featured", required = false) Boolean featured) {
        List<DealDO> list = dealService.getDealList();
        // 过滤逻辑
        List<DealDO> filtered = list.stream()
                .filter(d -> merchantId == null || d.getMerchantId().equals(merchantId))
                .filter(d -> featured == null || d.getFeatured().equals(featured))
                .toList();

        // 批量获取商家信息，避免 N+1 问题
        List<Long> merchantIds = filtered.stream()
                .map(DealDO::getMerchantId)
                .distinct()
                .toList();
        List<MerchantDO> merchants = merchantMapper.selectListByIds(merchantIds);
        Map<Long, MerchantDO> merchantMap = CollectionUtils.convertMap(merchants, MerchantDO::getId);

        // 先建立 index map，避免 O(n²) 复杂度
        Map<Long, DealDO> dealMap = CollectionUtils.convertMap(filtered, DealDO::getId);

        // 使用 BeanUtils 转换并填充商家信息
        List<AppDealRespVO> result = BeanUtils.toBean(filtered, AppDealRespVO.class, vo -> {
            DealDO deal = dealMap.get(vo.getId());
            if (deal != null) {
                MerchantDO merchant = merchantMap.get(deal.getMerchantId());
                if (merchant != null) {
                    vo.setMerchant(BeanUtils.toBean(merchant, AppDealMerchantRespVO.class));
                    vo.setMerchantName(merchant.getName());
                    vo.setMerchantLogo(merchant.getLogoUrl());
                }
            }
        });
        return success(result);
    }

    @GetMapping("/get-by-slug")
    @Operation(summary = "根据 slug 获取 Deal 详情")
    @Parameter(name = "slug", description = "Deal slug", required = true, example = "50-off-everything")
    public CommonResult<AppDealRespVO> getDealBySlug(@RequestParam("slug") String slug) {
        DealDO deal = dealService.getDealBySlug(slug);
        if (deal == null) {
            return success(null);
        }
        AppDealRespVO vo = BeanUtils.toBean(deal, AppDealRespVO.class);
        MerchantDO merchant = merchantMapper.selectById(deal.getMerchantId());
        if (merchant != null) {
            vo.setMerchant(BeanUtils.toBean(merchant, AppDealMerchantRespVO.class));
            vo.setMerchantName(merchant.getName());
            vo.setMerchantLogo(merchant.getLogoUrl());
        }
        return success(vo);
    }

}
