package com.river.module.campaign.controller.admin.campaign;

import com.river.framework.common.pojo.CommonResult;
import com.river.framework.common.pojo.PageResult;
import com.river.module.campaign.controller.admin.campaign.vo.CampaignPageReqVO;
import com.river.module.campaign.controller.admin.campaign.vo.CampaignRespVO;
import com.river.module.campaign.controller.admin.campaign.vo.CampaignSaveReqVO;
import com.river.framework.common.util.object.BeanUtils;
import com.river.module.campaign.dal.dataobject.CampaignDO;
import com.river.module.campaign.service.CampaignService;
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

@Tag(name = "管理后台 - Campaign")
@RestController
@RequestMapping("/campaign/campaign")
@Validated
public class CampaignController {

    @Resource
    private CampaignService campaignService;

    @PostMapping("/create")
    @Operation(summary = "创建 Campaign")
    @PreAuthorize("@ss.hasPermission('campaign:campaign:create')")
    public CommonResult<Long> createCampaign(@Valid @RequestBody CampaignSaveReqVO createReqVO) {
        return success(campaignService.createCampaign(BeanUtils.toBean(createReqVO, CampaignDO.class)));
    }

    @PutMapping("/update")
    @Operation(summary = "更新 Campaign")
    @PreAuthorize("@ss.hasPermission('campaign:campaign:update')")
    public CommonResult<Boolean> updateCampaign(@Valid @RequestBody CampaignSaveReqVO updateReqVO) {
        campaignService.updateCampaign(BeanUtils.toBean(updateReqVO, CampaignDO.class));
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除 Campaign")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('campaign:campaign:delete')")
    public CommonResult<Boolean> deleteCampaign(@RequestParam("id") Long id) {
        campaignService.deleteCampaign(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取 Campaign")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('campaign:campaign:query')")
    public CommonResult<CampaignRespVO> getCampaign(@RequestParam("id") Long id) {
        CampaignDO campaign = campaignService.getCampaign(id);
        return success(BeanUtils.toBean(campaign, CampaignRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获取 Campaign 列表")
    @PreAuthorize("@ss.hasPermission('campaign:campaign:query')")
    public CommonResult<List<CampaignRespVO>> getCampaignList() {
        List<CampaignDO> list = campaignService.getCampaignList();
        return success(BeanUtils.toBean(list, CampaignRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获取 Campaign 分页")
    @PreAuthorize("@ss.hasPermission('campaign:campaign:query')")
    public CommonResult<PageResult<CampaignRespVO>> getCampaignPage(@Valid CampaignPageReqVO pageReqVO) {
        PageResult<CampaignDO> pageResult = campaignService.getCampaignPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, CampaignRespVO.class));
    }
}
