package com.river.module.campaign.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.campaign.controller.admin.campaign.vo.CampaignPageReqVO;
import com.river.module.campaign.dal.dataobject.CampaignDO;
import jakarta.validation.Valid;

import java.util.List;

public interface CampaignService {

    Long createCampaign(@Valid CampaignDO campaign);

    void updateCampaign(@Valid CampaignDO campaign);

    void deleteCampaign(Long id);

    CampaignDO getCampaign(Long id);

    List<CampaignDO> getCampaignList();

    PageResult<CampaignDO> getCampaignPage(CampaignPageReqVO pageReqVO);

    void validateCampaignExists(Long id);
}
