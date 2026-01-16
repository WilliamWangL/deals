package com.river.module.tracking.controller.admin.attribution;

import com.river.framework.common.pojo.CommonResult;
import com.river.framework.common.pojo.PageResult;
import com.river.module.tracking.controller.admin.attribution.vo.AttributionPageReqVO;
import com.river.module.tracking.controller.admin.attribution.vo.AttributionRespVO;
import com.river.module.tracking.convert.AttributionConvert;
import com.river.module.tracking.dal.dataobject.AttributionDO;
import com.river.module.tracking.service.AttributionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.river.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 归因记录")
@RestController
@RequestMapping("/tracking/attribution")
@Validated
public class AttributionController {

    @Resource
    private AttributionService attributionService;

    @GetMapping("/get")
    @Operation(summary = "获取归因记录")
    @Parameter(name = "id", description = "ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('tracking:attribution:query')")
    public CommonResult<AttributionRespVO> getAttribution(@RequestParam("id") Long id) {
        AttributionDO attribution = attributionService.getAttribution(id);
        return success(AttributionConvert.INSTANCE.convert(attribution));
    }

    @GetMapping("/page")
    @Operation(summary = "获取归因记录分页")
    @PreAuthorize("@ss.hasPermission('tracking:attribution:query')")
    public CommonResult<PageResult<AttributionRespVO>> getAttributionPage(@Valid AttributionPageReqVO pageReqVO) {
        PageResult<AttributionDO> pageResult = attributionService.getAttributionPage(pageReqVO);
        return success(AttributionConvert.INSTANCE.convert(pageResult));
    }

}
