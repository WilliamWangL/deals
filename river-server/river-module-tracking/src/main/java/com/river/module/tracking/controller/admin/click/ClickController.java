package com.river.module.tracking.controller.admin.click;

import com.river.framework.common.pojo.CommonResult;
import com.river.framework.common.pojo.PageResult;
import com.river.module.tracking.controller.admin.click.vo.ClickPageReqVO;
import com.river.module.tracking.controller.admin.click.vo.ClickRespVO;
import com.river.framework.common.util.object.BeanUtils;
import com.river.module.tracking.dal.dataobject.ClickDO;
import com.river.module.tracking.service.ClickService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.river.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 点击记录")
@RestController
@RequestMapping("/tracking/click")
@Validated
public class ClickController {

    @Resource
    private ClickService clickService;

    @GetMapping("/get")
    @Operation(summary = "获取点击记录")
    @Parameter(name = "clickId", description = "点击 ID", required = true, example = "01HXYZ...")
    @PreAuthorize("@ss.hasPermission('tracking:click:query')")
    public CommonResult<ClickRespVO> getClick(@RequestParam("clickId") String clickId) {
        ClickDO click = clickService.getClick(clickId);
        return success(BeanUtils.toBean(click, ClickRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获取点击记录分页")
    @PreAuthorize("@ss.hasPermission('tracking:click:query')")
    public CommonResult<PageResult<ClickRespVO>> getClickPage(@Valid ClickPageReqVO pageReqVO) {
        PageResult<ClickDO> pageResult = clickService.getClickPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ClickRespVO.class));
    }

}
