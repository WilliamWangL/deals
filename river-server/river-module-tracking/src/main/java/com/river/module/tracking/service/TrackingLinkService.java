package com.river.module.tracking.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.tracking.controller.admin.trackinglink.vo.TrackingLinkPageReqVO;
import com.river.module.tracking.dal.dataobject.TrackingLinkDO;
import jakarta.validation.Valid;

import java.util.List;

public interface TrackingLinkService {

    Long createTrackingLink(@Valid TrackingLinkDO trackingLink);

    void updateTrackingLink(@Valid TrackingLinkDO trackingLink);

    void deleteTrackingLink(Long id);

    TrackingLinkDO getTrackingLink(Long id);

    TrackingLinkDO getTrackingLinkBySlug(String slug);

    List<TrackingLinkDO> getTrackingLinkList();

    PageResult<TrackingLinkDO> getTrackingLinkPage(TrackingLinkPageReqVO pageReqVO);

    void validateTrackingLinkExists(Long id);

}
