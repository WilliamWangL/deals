package com.river.module.campaign.controller.admin.costrecord;

import com.river.framework.common.pojo.CommonResult;
import com.river.framework.common.pojo.PageResult;
import com.river.module.campaign.controller.admin.costrecord.vo.CostRecordPageReqVO;
import com.river.module.campaign.controller.admin.costrecord.vo.CostRecordRespVO;
import com.river.module.campaign.controller.admin.costrecord.vo.CostRecordSaveReqVO;
import com.river.module.campaign.convert.CostRecordConvert;
import com.river.module.campaign.dal.dataobject.CostRecordDO;
import com.river.module.campaign.service.CostRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.river.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 成本记录")
@RestController
@RequestMapping("/campaign/cost-record")
@Validated
public class CostRecordController {

    @Resource
    private CostRecordService costRecordService;

    @PostMapping("/create")
    @Operation(summary = "创建成本记录")
    @PreAuthorize("@ss.hasPermission('campaign:cost-record:create')")
    public CommonResult<Long> createCostRecord(@Valid @RequestBody CostRecordSaveReqVO createReqVO) {
        return success(costRecordService.createCostRecord(CostRecordConvert.INSTANCE.convert(createReqVO)));
    }

    @PutMapping("/update")
    @Operation(summary = "更新成本记录")
    @PreAuthorize("@ss.hasPermission('campaign:cost-record:update')")
    public CommonResult<Boolean> updateCostRecord(@Valid @RequestBody CostRecordSaveReqVO updateReqVO) {
        costRecordService.updateCostRecord(CostRecordConvert.INSTANCE.convert(updateReqVO));
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除成本记录")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('campaign:cost-record:delete')")
    public CommonResult<Boolean> deleteCostRecord(@RequestParam("id") Long id) {
        costRecordService.deleteCostRecord(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取成本记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('campaign:cost-record:query')")
    public CommonResult<CostRecordRespVO> getCostRecord(@RequestParam("id") Long id) {
        CostRecordDO costRecord = costRecordService.getCostRecord(id);
        return success(CostRecordConvert.INSTANCE.convert(costRecord));
    }

    @GetMapping("/page")
    @Operation(summary = "获取成本记录分页")
    @PreAuthorize("@ss.hasPermission('campaign:cost-record:query')")
    public CommonResult<PageResult<CostRecordRespVO>> getCostRecordPage(@Valid CostRecordPageReqVO pageReqVO) {
        PageResult<CostRecordDO> pageResult = costRecordService.getCostRecordPage(pageReqVO);
        return success(CostRecordConvert.INSTANCE.convertPage(pageResult));
    }
}
