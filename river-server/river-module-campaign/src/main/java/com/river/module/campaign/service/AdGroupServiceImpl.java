package com.river.module.campaign.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.campaign.controller.admin.adgroup.vo.AdGroupPageReqVO;
import com.river.module.campaign.dal.dataobject.AdGroupDO;
import com.river.module.campaign.dal.mysql.AdGroupMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.river.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.river.module.campaign.enums.ErrorCodeConstants.*;

@Service
@Validated
public class AdGroupServiceImpl implements AdGroupService {

    @Resource
    private AdGroupMapper adGroupMapper;

    @Resource
    private CampaignService campaignService;

    @Override
    public Long createAdGroup(AdGroupDO adGroup) {
        campaignService.validateCampaignExists(adGroup.getCampaignId());
        adGroupMapper.insert(adGroup);
        return adGroup.getId();
    }

    @Override
    public void updateAdGroup(AdGroupDO adGroup) {
        validateAdGroupExists(adGroup.getId());
        campaignService.validateCampaignExists(adGroup.getCampaignId());
        adGroupMapper.updateById(adGroup);
    }

    @Override
    public void deleteAdGroup(Long id) {
        validateAdGroupExists(id);
        adGroupMapper.deleteById(id);
    }

    @Override
    public AdGroupDO getAdGroup(Long id) {
        return adGroupMapper.selectById(id);
    }

    @Override
    public List<AdGroupDO> getAdGroupListByCampaignId(Long campaignId) {
        return adGroupMapper.selectList(AdGroupDO::getCampaignId, campaignId);
    }

    @Override
    public PageResult<AdGroupDO> getAdGroupPage(AdGroupPageReqVO pageReqVO) {
        return adGroupMapper.selectPage(pageReqVO);
    }

    @Override
    public void validateAdGroupExists(Long id) {
        if (adGroupMapper.selectById(id) == null) {
            throw exception(AD_GROUP_NOT_EXISTS);
        }
    }
}
