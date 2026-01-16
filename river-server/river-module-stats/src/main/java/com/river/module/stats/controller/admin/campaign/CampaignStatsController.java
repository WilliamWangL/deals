package com.river.module.stats.controller.admin.campaign;

import com.river.framework.common.pojo.CommonResult;
import com.river.framework.common.pojo.PageResult;
import com.river.module.stats.controller.admin.campaign.vo.CampaignRoiRespVO;
import com.river.module.stats.controller.admin.campaign.vo.CampaignStatsPageReqVO;
import com.river.module.stats.controller.admin.offer.vo.OfferStatsRespVO;
import com.river.module.stats.convert.StatsConvert;
import com.river.module.stats.dal.dataobject.DailyStatsDO;
import com.river.module.stats.service.DailyStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

import static com.river.framework.common.pojo.CommonResult.success;
import static com.river.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Tag(name = "管理后台 - Campaign 统计")
@RestController
@RequestMapping("/stats/campaign")
@Validated
public class CampaignStatsController {

    @Resource
    private DailyStatsService dailyStatsService;

    @GetMapping("/page")
    @Operation(summary = "获取 Campaign 统计分页")
    @PreAuthorize("@ss.hasPermission('stats:campaign:query')")
    public CommonResult<PageResult<OfferStatsRespVO>> getCampaignStatsPage(@Valid CampaignStatsPageReqVO pageReqVO) {
        PageResult<DailyStatsDO> pageResult = dailyStatsService.getCampaignStatsPage(pageReqVO);
        return success(StatsConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/roi")
    @Operation(summary = "获取 Campaign ROI 分析")
    @PreAuthorize("@ss.hasPermission('stats:campaign:query')")
    public CommonResult<List<CampaignRoiRespVO>> getCampaignRoi(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY) LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        return success(dailyStatsService.getCampaignRoiList(startDate, endDate));
    }

}
