package com.river.module.stats.controller.admin.offer;

import com.river.framework.common.pojo.CommonResult;
import com.river.framework.common.pojo.PageResult;
import com.river.module.stats.controller.admin.offer.vo.OfferStatsPageReqVO;
import com.river.module.stats.controller.admin.offer.vo.OfferStatsRespVO;
import com.river.module.stats.convert.StatsConvert;
import com.river.module.stats.dal.dataobject.DailyStatsDO;
import com.river.module.stats.service.DailyStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.river.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - Offer 统计")
@RestController
@RequestMapping("/stats/offer")
@Validated
public class OfferStatsController {

    @Resource
    private DailyStatsService dailyStatsService;

    @GetMapping("/page")
    @Operation(summary = "获取 Offer 统计分页")
    @PreAuthorize("@ss.hasPermission('stats:offer:query')")
    public CommonResult<PageResult<OfferStatsRespVO>> getOfferStatsPage(@Valid OfferStatsPageReqVO pageReqVO) {
        PageResult<DailyStatsDO> pageResult = dailyStatsService.getOfferStatsPage(pageReqVO);
        return success(StatsConvert.INSTANCE.convertPage(pageResult));
    }

}
