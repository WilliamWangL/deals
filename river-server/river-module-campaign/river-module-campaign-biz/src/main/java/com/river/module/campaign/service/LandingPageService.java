package com.river.module.campaign.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.campaign.controller.admin.landingpage.vo.LandingPagePageReqVO;
import com.river.module.campaign.dal.dataobject.LandingPageDO;
import jakarta.validation.Valid;

import java.util.List;

public interface LandingPageService {

    Long createLandingPage(@Valid LandingPageDO landingPage);

    void updateLandingPage(@Valid LandingPageDO landingPage);

    void deleteLandingPage(Long id);

    LandingPageDO getLandingPage(Long id);

    LandingPageDO getLandingPageBySlug(String slug);

    List<LandingPageDO> getLandingPageList();

    PageResult<LandingPageDO> getLandingPagePage(LandingPagePageReqVO pageReqVO);

    void validateLandingPageExists(Long id);
}
