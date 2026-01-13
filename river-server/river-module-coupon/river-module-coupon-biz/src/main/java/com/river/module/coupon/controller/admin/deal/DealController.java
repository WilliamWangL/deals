package com.river.module.coupon.controller.admin.deal;

import com.river.framework.common.pojo.CommonResult;
import com.river.framework.common.pojo.PageResult;
import com.river.module.coupon.controller.admin.deal.vo.DealPageReqVO;
import com.river.module.coupon.controller.admin.deal.vo.DealRespVO;
import com.river.module.coupon.controller.admin.deal.vo.DealSaveReqVO;
import com.river.module.coupon.convert.DealConvert;
import com.river.module.coupon.dal.dataobject.DealDO;
import com.river.module.coupon.service.DealService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.river.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - Deal")
@RestController
@RequestMapping("/coupon/deal")
@Validated
public class DealController {

    @Resource
    private DealService dealService;

    @PostMapping("/create")
    @Operation(summary = "创建 Deal")
    @PreAuthorize("@ss.hasPermission('coupon:deal:create')")
    public CommonResult<Long> createDeal(@Valid @RequestBody DealSaveReqVO createReqVO) {
        return success(dealService.createDeal(DealConvert.INSTANCE.convert(createReqVO)));
    }

    @PutMapping("/update")
    @Operation(summary = "更新 Deal")
    @PreAuthorize("@ss.hasPermission('coupon:deal:update')")
    public CommonResult<Boolean> updateDeal(@Valid @RequestBody DealSaveReqVO updateReqVO) {
        dealService.updateDeal(DealConvert.INSTANCE.convert(updateReqVO));
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除 Deal")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('coupon:deal:delete')")
    public CommonResult<Boolean> deleteDeal(@RequestParam("id") Long id) {
        dealService.deleteDeal(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取 Deal")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('coupon:deal:query')")
    public CommonResult<DealRespVO> getDeal(@RequestParam("id") Long id) {
        DealDO deal = dealService.getDeal(id);
        return success(DealConvert.INSTANCE.convert(deal));
    }

    @GetMapping("/list")
    @Operation(summary = "获取 Deal 列表")
    @PreAuthorize("@ss.hasPermission('coupon:deal:query')")
    public CommonResult<List<DealRespVO>> getDealList() {
        List<DealDO> list = dealService.getDealList();
        return success(DealConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/page")
    @Operation(summary = "获取 Deal 分页")
    @PreAuthorize("@ss.hasPermission('coupon:deal:query')")
    public CommonResult<PageResult<DealRespVO>> getDealPage(@Valid DealPageReqVO pageReqVO) {
        PageResult<DealDO> pageResult = dealService.getDealPage(pageReqVO);
        return success(DealConvert.INSTANCE.convertPage(pageResult));
    }
}
