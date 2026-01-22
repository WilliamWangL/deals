package com.river.module.stats.controller.admin.daily;

import com.river.framework.common.pojo.CommonResult;
import com.river.framework.common.pojo.PageParam;
import com.river.framework.common.pojo.PageResult;
import com.river.framework.excel.core.util.ExcelUtils;
import com.river.module.stats.controller.admin.daily.vo.*;
import com.river.framework.common.util.object.BeanUtils;
import com.river.module.stats.dal.dataobject.DailyStatsDO;
import com.river.module.stats.service.DailyStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static com.river.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 日报统计")
@RestController
@RequestMapping("/stats/daily")
@Validated
public class DailyStatsController {

    @Resource
    private DailyStatsService dailyStatsService;

    @PostMapping("/page")
    @Operation(summary = "获取日报统计分页")
    @PreAuthorize("@ss.hasPermission('stats:daily:query')")
    public CommonResult<PageResult<DailyStatsRespVO>> getDailyStatsPage(@Valid @RequestBody DailyStatsPageReqVO pageReqVO) {
        PageResult<DailyStatsDO> pageResult = dailyStatsService.getDailyStatsPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, DailyStatsRespVO.class));
    }

    @GetMapping("/summary")
    @Operation(summary = "获取日报统计汇总")
    @PreAuthorize("@ss.hasPermission('stats:daily:query')")
    public CommonResult<DailyStatsSummaryRespVO> getDailyStatsSummary(@Valid DailyStatsPageReqVO reqVO) {
        return success(dailyStatsService.getDailyStatsSummary(reqVO));
    }

    @GetMapping("/trend")
    @Operation(summary = "获取日报统计趋势")
    @PreAuthorize("@ss.hasPermission('stats:daily:query')")
    public CommonResult<List<DailyStatsTrendRespVO>> getDailyStatsTrend(@Valid DailyStatsPageReqVO reqVO) {
        return success(dailyStatsService.getDailyStatsTrend(reqVO));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出日报统计 Excel")
    @PreAuthorize("@ss.hasPermission('stats:daily:export')")
    public void exportDailyStatsExcel(@Valid DailyStatsPageReqVO reqVO,
                                       HttpServletResponse response) throws IOException {
        reqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        PageResult<DailyStatsDO> pageResult = dailyStatsService.getDailyStatsPage(reqVO);
        List<DailyStatsRespVO> list = BeanUtils.toBean(pageResult.getList(), DailyStatsRespVO.class);
        ExcelUtils.write(response, "日报统计.xls", "数据", DailyStatsRespVO.class, list);
    }

}
