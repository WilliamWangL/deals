package com.river.module.coupon.controller.app;

import com.river.framework.common.pojo.CommonResult;
import com.river.module.coupon.controller.app.vo.AppDealRespVO;
import com.river.module.coupon.dal.dataobject.DealDO;
import com.river.module.coupon.service.DealService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.river.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 App - Deal")
@RestController
@RequestMapping("/coupon/deal")
@Validated
public class AppDealController {

    @Resource
    private DealService dealService;

    @GetMapping("/list")
    @Operation(summary = "获取 Deal 列表")
    public CommonResult<List<AppDealRespVO>> getDealList(
            @RequestParam(value = "merchantId", required = false) Long merchantId,
            @RequestParam(value = "featured", required = false) Boolean featured) {
        List<DealDO> list = dealService.getDealList();
        List<AppDealRespVO> result = list.stream()
                .filter(d -> merchantId == null || d.getMerchantId().equals(merchantId))
                .filter(d -> featured == null || d.getFeatured().equals(featured))
                .map(this::convertToAppVO)
                .toList();
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
        return success(convertToAppVO(deal));
    }

    private AppDealRespVO convertToAppVO(DealDO deal) {
        if (deal == null) {
            return null;
        }
        AppDealRespVO vo = new AppDealRespVO();
        vo.setId(deal.getId());
        vo.setSlug(deal.getSlug());
        vo.setTitle(deal.getTitle());
        vo.setDescription(deal.getDescription());
        vo.setOriginalPrice(deal.getOriginalPrice());
        vo.setDealPrice(deal.getDealPrice());
        vo.setDiscountPercent(deal.getDiscountPercent());
        vo.setMerchantId(deal.getMerchantId());
        vo.setImageUrl(deal.getImageUrl());
        vo.setStartTime(deal.getStartTime());
        vo.setEndTime(deal.getEndTime());
        vo.setFeatured(deal.getFeatured());
        return vo;
    }

}
