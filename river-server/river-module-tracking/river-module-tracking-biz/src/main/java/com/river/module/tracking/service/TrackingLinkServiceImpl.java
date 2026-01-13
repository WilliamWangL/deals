package com.river.module.tracking.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.tracking.controller.admin.trackinglink.vo.TrackingLinkPageReqVO;
import com.river.module.tracking.dal.dataobject.TrackingLinkDO;
import com.river.module.tracking.dal.mysql.TrackingLinkMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.river.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.river.module.tracking.enums.ErrorCodeConstants.*;

@Service
@Validated
public class TrackingLinkServiceImpl implements TrackingLinkService {

    @Resource
    private TrackingLinkMapper trackingLinkMapper;

    @Override
    public Long createTrackingLink(TrackingLinkDO trackingLink) {
        validateSlugUnique(null, trackingLink.getSlug());
        trackingLinkMapper.insert(trackingLink);
        return trackingLink.getId();
    }

    @Override
    public void updateTrackingLink(TrackingLinkDO trackingLink) {
        validateTrackingLinkExists(trackingLink.getId());
        validateSlugUnique(trackingLink.getId(), trackingLink.getSlug());
        trackingLinkMapper.updateById(trackingLink);
    }

    @Override
    public void deleteTrackingLink(Long id) {
        validateTrackingLinkExists(id);
        trackingLinkMapper.deleteById(id);
    }

    @Override
    public TrackingLinkDO getTrackingLink(Long id) {
        return trackingLinkMapper.selectById(id);
    }

    @Override
    public TrackingLinkDO getTrackingLinkBySlug(String slug) {
        return trackingLinkMapper.selectBySlug(slug);
    }

    @Override
    public List<TrackingLinkDO> getTrackingLinkList() {
        return trackingLinkMapper.selectList();
    }

    @Override
    public PageResult<TrackingLinkDO> getTrackingLinkPage(TrackingLinkPageReqVO pageReqVO) {
        return trackingLinkMapper.selectPage(pageReqVO);
    }

    @Override
    public void validateTrackingLinkExists(Long id) {
        if (trackingLinkMapper.selectById(id) == null) {
            throw exception(TRACKING_LINK_NOT_EXISTS);
        }
    }

    private void validateSlugUnique(Long id, String slug) {
        TrackingLinkDO existing = trackingLinkMapper.selectBySlug(slug);
        if (existing != null && !existing.getId().equals(id)) {
            throw exception(TRACKING_LINK_SLUG_DUPLICATE);
        }
    }

}
