package com.river.module.stats.controller.admin.dashboard;

import com.river.framework.common.pojo.CommonResult;
import com.river.module.stats.controller.admin.dashboard.vo.DashboardSummaryRespVO;
import com.river.module.stats.controller.admin.dashboard.vo.DashboardTrendReqVO;
import com.river.module.stats.controller.admin.dashboard.vo.DashboardTrendRespVO;
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

@Tag(name = "管理后台 - 统计仪表盘")
@RestController
@RequestMapping("/stats/dashboard")
@Validated
public class DashboardController {

    @Resource
    private DailyStatsService dailyStatsService;

    @GetMapping("/summary")
    @Operation(summary = "获取仪表盘汇总数据")
    @PreAuthorize("@ss.hasPermission('stats:dashboard:query')")
    public CommonResult<DashboardSummaryRespVO> getDashboardSummary(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY) LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        return success(dailyStatsService.getDashboardSummary(startDate, endDate));
    }

    @GetMapping("/trend")
    @Operation(summary = "获取仪表盘趋势数据")
    @PreAuthorize("@ss.hasPermission('stats:dashboard:query')")
    public CommonResult<List<DashboardTrendRespVO>> getDashboardTrend(@Valid DashboardTrendReqVO reqVO) {
        return success(dailyStatsService.getDashboardTrend(reqVO));
    }

}
