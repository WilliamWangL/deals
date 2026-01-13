package com.river.module.campaign.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.campaign.controller.admin.trafficsource.vo.TrafficSourcePageReqVO;
import com.river.module.campaign.dal.dataobject.TrafficSourceDO;
import jakarta.validation.Valid;

import java.util.List;

public interface TrafficSourceService {

    Long createTrafficSource(@Valid TrafficSourceDO trafficSource);

    void updateTrafficSource(@Valid TrafficSourceDO trafficSource);

    void deleteTrafficSource(Long id);

    TrafficSourceDO getTrafficSource(Long id);

    List<TrafficSourceDO> getTrafficSourceList();

    PageResult<TrafficSourceDO> getTrafficSourcePage(TrafficSourcePageReqVO pageReqVO);

    void validateTrafficSourceExists(Long id);
}
