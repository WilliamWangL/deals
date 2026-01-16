package com.river.module.campaign.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.campaign.controller.admin.campaign.vo.CampaignPageReqVO;
import com.river.module.campaign.dal.dataobject.CampaignDO;
import com.river.module.campaign.dal.mysql.CampaignMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.river.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.river.module.campaign.enums.ErrorCodeConstants.*;

@Service
@Validated
public class CampaignServiceImpl implements CampaignService {

    @Resource
    private CampaignMapper campaignMapper;

    @Resource
    private TrafficSourceService trafficSourceService;

    @Override
    public Long createCampaign(CampaignDO campaign) {
        trafficSourceService.validateTrafficSourceExists(campaign.getTrafficSourceId());
        campaignMapper.insert(campaign);
        return campaign.getId();
    }

    @Override
    public void updateCampaign(CampaignDO campaign) {
        validateCampaignExists(campaign.getId());
        trafficSourceService.validateTrafficSourceExists(campaign.getTrafficSourceId());
        campaignMapper.updateById(campaign);
    }

    @Override
    public void deleteCampaign(Long id) {
        validateCampaignExists(id);
        campaignMapper.deleteById(id);
    }

    @Override
    public CampaignDO getCampaign(Long id) {
        return campaignMapper.selectById(id);
    }

    @Override
    public List<CampaignDO> getCampaignList() {
        return campaignMapper.selectList();
    }

    @Override
    public PageResult<CampaignDO> getCampaignPage(CampaignPageReqVO pageReqVO) {
        return campaignMapper.selectPage(pageReqVO);
    }

    @Override
    public void validateCampaignExists(Long id) {
        if (campaignMapper.selectById(id) == null) {
            throw exception(CAMPAIGN_NOT_EXISTS);
        }
    }
}
