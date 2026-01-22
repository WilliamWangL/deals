package com.river.module.tracking.controller.admin.conversion;

import com.river.framework.common.pojo.CommonResult;
import com.river.framework.common.pojo.PageResult;
import com.river.module.tracking.controller.admin.conversion.vo.ConversionPageReqVO;
import com.river.module.tracking.controller.admin.conversion.vo.ConversionRespVO;
import com.river.framework.common.util.object.BeanUtils;
import com.river.module.tracking.dal.dataobject.ConversionDO;
import com.river.module.tracking.service.ConversionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.river.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 转化记录")
@RestController
@RequestMapping("/tracking/conversion")
@Validated
public class ConversionController {

    @Resource
    private ConversionService conversionService;

    @GetMapping("/get")
    @Operation(summary = "获取转化记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('tracking:conversion:query')")
    public CommonResult<ConversionRespVO> getConversion(@RequestParam("id") Long id) {
        ConversionDO conversion = conversionService.getConversion(id);
        return success(BeanUtils.toBean(conversion, ConversionRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获取转化记录分页")
    @PreAuthorize("@ss.hasPermission('tracking:conversion:query')")
    public CommonResult<PageResult<ConversionRespVO>> getConversionPage(@Valid ConversionPageReqVO pageReqVO) {
        PageResult<ConversionDO> pageResult = conversionService.getConversionPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ConversionRespVO.class));
    }

    @PutMapping("/update-status")
    @Operation(summary = "更新转化状态")
    @PreAuthorize("@ss.hasPermission('tracking:conversion:update')")
    public CommonResult<Boolean> updateConversionStatus(@RequestParam("id") Long id,
                                                        @RequestParam("status") Integer status) {
        conversionService.updateConversionStatus(id, status);
        return success(true);
    }

}
