package com.river.module.campaign.controller.admin.landingpage;

import com.river.framework.common.pojo.CommonResult;
import com.river.framework.common.pojo.PageResult;
import com.river.module.campaign.controller.admin.landingpage.vo.LandingPagePageReqVO;
import com.river.module.campaign.controller.admin.landingpage.vo.LandingPageRespVO;
import com.river.module.campaign.controller.admin.landingpage.vo.LandingPageSaveReqVO;
import com.river.framework.common.util.object.BeanUtils;
import com.river.module.campaign.dal.dataobject.LandingPageDO;
import com.river.module.campaign.service.LandingPageService;
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

@Tag(name = "管理后台 - 落地页")
@RestController
@RequestMapping("/campaign/landing-page")
@Validated
public class LandingPageController {

    @Resource
    private LandingPageService landingPageService;

    @PostMapping("/create")
    @Operation(summary = "创建落地页")
    @PreAuthorize("@ss.hasPermission('campaign:landing-page:create')")
    public CommonResult<Long> createLandingPage(@Valid @RequestBody LandingPageSaveReqVO createReqVO) {
        return success(landingPageService.createLandingPage(BeanUtils.toBean(createReqVO, LandingPageDO.class)));
    }

    @PutMapping("/update")
    @Operation(summary = "更新落地页")
    @PreAuthorize("@ss.hasPermission('campaign:landing-page:update')")
    public CommonResult<Boolean> updateLandingPage(@Valid @RequestBody LandingPageSaveReqVO updateReqVO) {
        landingPageService.updateLandingPage(BeanUtils.toBean(updateReqVO, LandingPageDO.class));
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除落地页")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('campaign:landing-page:delete')")
    public CommonResult<Boolean> deleteLandingPage(@RequestParam("id") Long id) {
        landingPageService.deleteLandingPage(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取落地页")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('campaign:landing-page:query')")
    public CommonResult<LandingPageRespVO> getLandingPage(@RequestParam("id") Long id) {
        LandingPageDO landingPage = landingPageService.getLandingPage(id);
        return success(BeanUtils.toBean(landingPage, LandingPageRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获取落地页列表")
    @PreAuthorize("@ss.hasPermission('campaign:landing-page:query')")
    public CommonResult<List<LandingPageRespVO>> getLandingPageList() {
        List<LandingPageDO> list = landingPageService.getLandingPageList();
        return success(BeanUtils.toBean(list, LandingPageRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获取落地页分页")
    @PreAuthorize("@ss.hasPermission('campaign:landing-page:query')")
    public CommonResult<PageResult<LandingPageRespVO>> getLandingPagePage(@Valid LandingPagePageReqVO pageReqVO) {
        PageResult<LandingPageDO> pageResult = landingPageService.getLandingPagePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, LandingPageRespVO.class));
    }
}
