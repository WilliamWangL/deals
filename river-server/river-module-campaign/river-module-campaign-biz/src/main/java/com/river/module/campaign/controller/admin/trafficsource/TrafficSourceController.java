package com.river.module.campaign.controller.admin.trafficsource;

import com.river.framework.common.pojo.CommonResult;
import com.river.framework.common.pojo.PageResult;
import com.river.module.campaign.controller.admin.trafficsource.vo.TrafficSourcePageReqVO;
import com.river.module.campaign.controller.admin.trafficsource.vo.TrafficSourceRespVO;
import com.river.module.campaign.controller.admin.trafficsource.vo.TrafficSourceSaveReqVO;
import com.river.module.campaign.convert.TrafficSourceConvert;
import com.river.module.campaign.dal.dataobject.TrafficSourceDO;
import com.river.module.campaign.service.TrafficSourceService;
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

@Tag(name = "管理后台 - 流量来源")
@RestController
@RequestMapping("/campaign/traffic-source")
@Validated
public class TrafficSourceController {

    @Resource
    private TrafficSourceService trafficSourceService;

    @PostMapping("/create")
    @Operation(summary = "创建流量来源")
    @PreAuthorize("@ss.hasPermission('campaign:traffic-source:create')")
    public CommonResult<Long> createTrafficSource(@Valid @RequestBody TrafficSourceSaveReqVO createReqVO) {
        return success(trafficSourceService.createTrafficSource(TrafficSourceConvert.INSTANCE.convert(createReqVO)));
    }

    @PutMapping("/update")
    @Operation(summary = "更新流量来源")
    @PreAuthorize("@ss.hasPermission('campaign:traffic-source:update')")
    public CommonResult<Boolean> updateTrafficSource(@Valid @RequestBody TrafficSourceSaveReqVO updateReqVO) {
        trafficSourceService.updateTrafficSource(TrafficSourceConvert.INSTANCE.convert(updateReqVO));
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除流量来源")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('campaign:traffic-source:delete')")
    public CommonResult<Boolean> deleteTrafficSource(@RequestParam("id") Long id) {
        trafficSourceService.deleteTrafficSource(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取流量来源")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('campaign:traffic-source:query')")
    public CommonResult<TrafficSourceRespVO> getTrafficSource(@RequestParam("id") Long id) {
        TrafficSourceDO trafficSource = trafficSourceService.getTrafficSource(id);
        return success(TrafficSourceConvert.INSTANCE.convert(trafficSource));
    }

    @GetMapping("/list")
    @Operation(summary = "获取流量来源列表")
    @PreAuthorize("@ss.hasPermission('campaign:traffic-source:query')")
    public CommonResult<List<TrafficSourceRespVO>> getTrafficSourceList() {
        List<TrafficSourceDO> list = trafficSourceService.getTrafficSourceList();
        return success(TrafficSourceConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/page")
    @Operation(summary = "获取流量来源分页")
    @PreAuthorize("@ss.hasPermission('campaign:traffic-source:query')")
    public CommonResult<PageResult<TrafficSourceRespVO>> getTrafficSourcePage(@Valid TrafficSourcePageReqVO pageReqVO) {
        PageResult<TrafficSourceDO> pageResult = trafficSourceService.getTrafficSourcePage(pageReqVO);
        return success(TrafficSourceConvert.INSTANCE.convertPage(pageResult));
    }
}
