package com.river.module.campaign.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.campaign.controller.admin.landingpage.vo.LandingPagePageReqVO;
import com.river.module.campaign.dal.dataobject.LandingPageDO;
import com.river.module.campaign.dal.mysql.LandingPageMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.river.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.river.module.campaign.enums.ErrorCodeConstants.*;

@Service
@Validated
public class LandingPageServiceImpl implements LandingPageService {

    @Resource
    private LandingPageMapper landingPageMapper;

    @Override
    public Long createLandingPage(LandingPageDO landingPage) {
        validateSlugUnique(null, landingPage.getSlug());
        landingPageMapper.insert(landingPage);
        return landingPage.getId();
    }

    @Override
    public void updateLandingPage(LandingPageDO landingPage) {
        validateLandingPageExists(landingPage.getId());
        validateSlugUnique(landingPage.getId(), landingPage.getSlug());
        landingPageMapper.updateById(landingPage);
    }

    @Override
    public void deleteLandingPage(Long id) {
        validateLandingPageExists(id);
        landingPageMapper.deleteById(id);
    }

    @Override
    public LandingPageDO getLandingPage(Long id) {
        return landingPageMapper.selectById(id);
    }

    @Override
    public LandingPageDO getLandingPageBySlug(String slug) {
        return landingPageMapper.selectBySlug(slug);
    }

    @Override
    public List<LandingPageDO> getLandingPageList() {
        return landingPageMapper.selectList();
    }

    @Override
    public PageResult<LandingPageDO> getLandingPagePage(LandingPagePageReqVO pageReqVO) {
        return landingPageMapper.selectPage(pageReqVO);
    }

    @Override
    public void validateLandingPageExists(Long id) {
        if (landingPageMapper.selectById(id) == null) {
            throw exception(LANDING_PAGE_NOT_EXISTS);
        }
    }

    private void validateSlugUnique(Long id, String slug) {
        LandingPageDO existing = landingPageMapper.selectBySlug(slug);
        if (existing != null && !existing.getId().equals(id)) {
            throw exception(LANDING_PAGE_SLUG_DUPLICATE);
        }
    }
}
