package com.river.module.campaign.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.campaign.controller.admin.adgroup.vo.AdGroupPageReqVO;
import com.river.module.campaign.dal.dataobject.AdGroupDO;
import jakarta.validation.Valid;

import java.util.List;

public interface AdGroupService {

    Long createAdGroup(@Valid AdGroupDO adGroup);

    void updateAdGroup(@Valid AdGroupDO adGroup);

    void deleteAdGroup(Long id);

    AdGroupDO getAdGroup(Long id);

    List<AdGroupDO> getAdGroupListByCampaignId(Long campaignId);

    PageResult<AdGroupDO> getAdGroupPage(AdGroupPageReqVO pageReqVO);

    void validateAdGroupExists(Long id);
}
