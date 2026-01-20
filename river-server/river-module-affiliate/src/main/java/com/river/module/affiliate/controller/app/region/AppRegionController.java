package com.river.module.affiliate.controller.app.region;

import com.river.framework.common.pojo.CommonResult;
import com.river.module.affiliate.controller.app.region.vo.RegionRespVO;
import com.river.module.affiliate.service.region.RegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.river.framework.common.pojo.CommonResult.success;

/**
 * 用户 App - 地区查询 Controller
 * 迁移自 system 模块
 */
@Tag(name = "用户 App - 地区")
@RestController
@RequestMapping("/affiliate/region")
@Validated
@PermitAll
public class AppRegionController {

    @Resource
    private RegionService regionService;

    @GetMapping("/available")
    @Operation(summary = "获取有数据的国家列表")
    @PermitAll
    public CommonResult<List<RegionRespVO>> getAvailableRegions() {
        return success(regionService.getAvailableRegions());
    }
}
