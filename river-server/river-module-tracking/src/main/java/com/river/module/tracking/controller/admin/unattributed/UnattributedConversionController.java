package com.river.module.tracking.controller.admin.unattributed;

import com.river.framework.common.pojo.CommonResult;
import com.river.framework.common.pojo.PageResult;
import com.river.module.tracking.controller.admin.unattributed.vo.UnattributedConversionPageReqVO;
import com.river.module.tracking.controller.admin.unattributed.vo.UnattributedConversionRespVO;
import com.river.module.tracking.convert.UnattributedConversionConvert;
import com.river.module.tracking.dal.dataobject.UnattributedConversionDO;
import com.river.module.tracking.service.UnattributedConversionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.river.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 未归因转化")
@RestController
@RequestMapping("/tracking/unattributed-conversion")
@Validated
public class UnattributedConversionController {

    @Resource
    private UnattributedConversionService unattributedConversionService;

    @GetMapping("/get")
    @Operation(summary = "获取未归因转化")
    @Parameter(name = "id", description = "ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('tracking:unattributed:query')")
    public CommonResult<UnattributedConversionRespVO> getUnattributedConversion(@RequestParam("id") Long id) {
        UnattributedConversionDO unattributedConversion = unattributedConversionService.getUnattributedConversion(id);
        return success(UnattributedConversionConvert.INSTANCE.convert(unattributedConversion));
    }

    @GetMapping("/page")
    @Operation(summary = "获取未归因转化分页")
    @PreAuthorize("@ss.hasPermission('tracking:unattributed:query')")
    public CommonResult<PageResult<UnattributedConversionRespVO>> getUnattributedConversionPage(@Valid UnattributedConversionPageReqVO pageReqVO) {
        PageResult<UnattributedConversionDO> pageResult = unattributedConversionService.getUnattributedConversionPage(pageReqVO);
        return success(UnattributedConversionConvert.INSTANCE.convert(pageResult));
    }

}
