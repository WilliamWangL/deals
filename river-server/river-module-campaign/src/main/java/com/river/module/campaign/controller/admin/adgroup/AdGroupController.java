package com.river.module.campaign.controller.admin.adgroup;

import com.river.framework.common.pojo.CommonResult;
import com.river.framework.common.pojo.PageResult;
import com.river.module.campaign.controller.admin.adgroup.vo.AdGroupPageReqVO;
import com.river.module.campaign.controller.admin.adgroup.vo.AdGroupRespVO;
import com.river.module.campaign.controller.admin.adgroup.vo.AdGroupSaveReqVO;
import com.river.framework.common.util.object.BeanUtils;
import com.river.module.campaign.dal.dataobject.AdGroupDO;
import com.river.module.campaign.service.AdGroupService;
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

@Tag(name = "管理后台 - 广告组")
@RestController
@RequestMapping("/campaign/ad-group")
@Validated
public class AdGroupController {

    @Resource
    private AdGroupService adGroupService;

    @PostMapping("/create")
    @Operation(summary = "创建广告组")
    @PreAuthorize("@ss.hasPermission('campaign:ad-group:create')")
    public CommonResult<Long> createAdGroup(@Valid @RequestBody AdGroupSaveReqVO createReqVO) {
        return success(adGroupService.createAdGroup(BeanUtils.toBean(createReqVO, AdGroupDO.class)));
    }

    @PutMapping("/update")
    @Operation(summary = "更新广告组")
    @PreAuthorize("@ss.hasPermission('campaign:ad-group:update')")
    public CommonResult<Boolean> updateAdGroup(@Valid @RequestBody AdGroupSaveReqVO updateReqVO) {
        adGroupService.updateAdGroup(BeanUtils.toBean(updateReqVO, AdGroupDO.class));
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除广告组")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('campaign:ad-group:delete')")
    public CommonResult<Boolean> deleteAdGroup(@RequestParam("id") Long id) {
        adGroupService.deleteAdGroup(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取广告组")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('campaign:ad-group:query')")
    public CommonResult<AdGroupRespVO> getAdGroup(@RequestParam("id") Long id) {
        AdGroupDO adGroup = adGroupService.getAdGroup(id);
        return success(BeanUtils.toBean(adGroup, AdGroupRespVO.class));
    }

    @GetMapping("/list-by-campaign")
    @Operation(summary = "获取 Campaign 下的广告组列表")
    @Parameter(name = "campaignId", description = "Campaign 编号", required = true)
    @PreAuthorize("@ss.hasPermission('campaign:ad-group:query')")
    public CommonResult<List<AdGroupRespVO>> getAdGroupListByCampaign(@RequestParam("campaignId") Long campaignId) {
        List<AdGroupDO> list = adGroupService.getAdGroupListByCampaignId(campaignId);
        return success(BeanUtils.toBean(list, AdGroupRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获取广告组分页")
    @PreAuthorize("@ss.hasPermission('campaign:ad-group:query')")
    public CommonResult<PageResult<AdGroupRespVO>> getAdGroupPage(@Valid AdGroupPageReqVO pageReqVO) {
        PageResult<AdGroupDO> pageResult = adGroupService.getAdGroupPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AdGroupRespVO.class));
    }
}
