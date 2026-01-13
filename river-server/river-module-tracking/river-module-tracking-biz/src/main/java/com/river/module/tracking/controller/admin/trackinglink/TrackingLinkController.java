package com.river.module.tracking.controller.admin.trackinglink;

import com.river.framework.common.pojo.CommonResult;
import com.river.framework.common.pojo.PageResult;
import com.river.module.tracking.controller.admin.trackinglink.vo.TrackingLinkPageReqVO;
import com.river.module.tracking.controller.admin.trackinglink.vo.TrackingLinkRespVO;
import com.river.module.tracking.controller.admin.trackinglink.vo.TrackingLinkSaveReqVO;
import com.river.module.tracking.convert.TrackingLinkConvert;
import com.river.module.tracking.dal.dataobject.TrackingLinkDO;
import com.river.module.tracking.service.TrackingLinkService;
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

@Tag(name = "管理后台 - 追踪链接")
@RestController
@RequestMapping("/tracking/link")
@Validated
public class TrackingLinkController {

    @Resource
    private TrackingLinkService trackingLinkService;

    @PostMapping("/create")
    @Operation(summary = "创建追踪链接")
    @PreAuthorize("@ss.hasPermission('tracking:link:create')")
    public CommonResult<Long> createTrackingLink(@Valid @RequestBody TrackingLinkSaveReqVO createReqVO) {
        return success(trackingLinkService.createTrackingLink(TrackingLinkConvert.INSTANCE.convert(createReqVO)));
    }

    @PutMapping("/update")
    @Operation(summary = "更新追踪链接")
    @PreAuthorize("@ss.hasPermission('tracking:link:update')")
    public CommonResult<Boolean> updateTrackingLink(@Valid @RequestBody TrackingLinkSaveReqVO updateReqVO) {
        trackingLinkService.updateTrackingLink(TrackingLinkConvert.INSTANCE.convert(updateReqVO));
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除追踪链接")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('tracking:link:delete')")
    public CommonResult<Boolean> deleteTrackingLink(@RequestParam("id") Long id) {
        trackingLinkService.deleteTrackingLink(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取追踪链接")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('tracking:link:query')")
    public CommonResult<TrackingLinkRespVO> getTrackingLink(@RequestParam("id") Long id) {
        TrackingLinkDO trackingLink = trackingLinkService.getTrackingLink(id);
        return success(TrackingLinkConvert.INSTANCE.convert(trackingLink));
    }

    @GetMapping("/list")
    @Operation(summary = "获取追踪链接列表")
    @PreAuthorize("@ss.hasPermission('tracking:link:query')")
    public CommonResult<List<TrackingLinkRespVO>> getTrackingLinkList() {
        List<TrackingLinkDO> list = trackingLinkService.getTrackingLinkList();
        return success(TrackingLinkConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/page")
    @Operation(summary = "获取追踪链接分页")
    @PreAuthorize("@ss.hasPermission('tracking:link:query')")
    public CommonResult<PageResult<TrackingLinkRespVO>> getTrackingLinkPage(@Valid TrackingLinkPageReqVO pageReqVO) {
        PageResult<TrackingLinkDO> pageResult = trackingLinkService.getTrackingLinkPage(pageReqVO);
        return success(TrackingLinkConvert.INSTANCE.convertPage(pageResult));
    }

}
