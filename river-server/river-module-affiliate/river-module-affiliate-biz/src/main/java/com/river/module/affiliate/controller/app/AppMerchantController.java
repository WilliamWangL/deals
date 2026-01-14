package com.river.module.affiliate.controller.app;

import com.river.framework.common.pojo.CommonResult;
import com.river.framework.common.pojo.PageResult;
import com.river.module.affiliate.controller.app.vo.AppMerchantRespVO;
import com.river.module.affiliate.dal.dataobject.MerchantDO;
import com.river.module.affiliate.service.MerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.river.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 App - 商家")
@RestController
@RequestMapping("/affiliate/merchant")
@Validated
public class AppMerchantController {

    @Resource
    private MerchantService merchantService;

    @GetMapping("/list")
    @Operation(summary = "获取商家列表")
    public CommonResult<List<AppMerchantRespVO>> getMerchantList() {
        List<MerchantDO> list = merchantService.getMerchantList();
        return success(convertToAppVOList(list));
    }

    @GetMapping("/get-by-slug")
    @Operation(summary = "根据 slug 获取商家详情")
    @Parameter(name = "slug", description = "商家 slug", required = true, example = "amazon")
    public CommonResult<AppMerchantRespVO> getMerchantBySlug(@RequestParam("slug") String slug) {
        MerchantDO merchant = merchantService.getMerchantBySlug(slug);
        if (merchant == null) {
            return success(null);
        }
        return success(convertToAppVO(merchant));
    }

    private AppMerchantRespVO convertToAppVO(MerchantDO merchant) {
        if (merchant == null) {
            return null;
        }
        AppMerchantRespVO vo = new AppMerchantRespVO();
        vo.setId(merchant.getId());
        vo.setName(merchant.getName());
        vo.setSlug(merchant.getSlug());
        vo.setDomain(merchant.getDomain());
        vo.setLogoUrl(merchant.getLogoUrl());
        vo.setDescription(merchant.getDescription());
        vo.setRating(merchant.getRating());
        vo.setRegions(parseRegions(merchant.getRegions()));
        vo.setDealCount(0);
        vo.setCouponCount(0);
        return vo;
    }

    private List<String> parseRegions(String regions) {
        if (regions == null || regions.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.asList(regions.split(","));
    }

    private List<AppMerchantRespVO> convertToAppVOList(List<MerchantDO> list) {
        return list.stream().map(this::convertToAppVO).toList();
    }

}
