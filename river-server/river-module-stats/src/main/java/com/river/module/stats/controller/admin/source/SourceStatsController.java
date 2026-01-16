package com.river.module.stats.controller.admin.source;

import com.river.framework.common.pojo.CommonResult;
import com.river.module.stats.controller.admin.source.vo.SourceStatsPageReqVO;
import com.river.module.stats.controller.admin.source.vo.SourceStatsRespVO;
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

import java.util.List;

import static com.river.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 渠道统计")
@RestController
@RequestMapping("/stats/source")
@Validated
public class SourceStatsController {

    @Resource
    private DailyStatsService dailyStatsService;

    @GetMapping("/list")
    @Operation(summary = "获取渠道统计列表")
    @PreAuthorize("@ss.hasPermission('stats:source:query')")
    public CommonResult<List<SourceStatsRespVO>> getSourceStatsList(@Valid SourceStatsPageReqVO reqVO) {
        return success(dailyStatsService.getSourceStatsList(reqVO));
    }

}
