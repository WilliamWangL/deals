package com.river.module.campaign.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.campaign.controller.admin.trafficsource.vo.TrafficSourcePageReqVO;
import com.river.module.campaign.dal.dataobject.TrafficSourceDO;
import com.river.module.campaign.dal.mysql.TrafficSourceMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.river.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.river.module.campaign.enums.ErrorCodeConstants.*;

@Service
@Validated
public class TrafficSourceServiceImpl implements TrafficSourceService {

    @Resource
    private TrafficSourceMapper trafficSourceMapper;

    @Override
    public Long createTrafficSource(TrafficSourceDO trafficSource) {
        validateCodeUnique(null, trafficSource.getCode());
        trafficSourceMapper.insert(trafficSource);
        return trafficSource.getId();
    }

    @Override
    public void updateTrafficSource(TrafficSourceDO trafficSource) {
        validateTrafficSourceExists(trafficSource.getId());
        validateCodeUnique(trafficSource.getId(), trafficSource.getCode());
        trafficSourceMapper.updateById(trafficSource);
    }

    @Override
    public void deleteTrafficSource(Long id) {
        validateTrafficSourceExists(id);
        trafficSourceMapper.deleteById(id);
    }

    @Override
    public TrafficSourceDO getTrafficSource(Long id) {
        return trafficSourceMapper.selectById(id);
    }

    @Override
    public List<TrafficSourceDO> getTrafficSourceList() {
        return trafficSourceMapper.selectList();
    }

    @Override
    public PageResult<TrafficSourceDO> getTrafficSourcePage(TrafficSourcePageReqVO pageReqVO) {
        return trafficSourceMapper.selectPage(pageReqVO);
    }

    @Override
    public void validateTrafficSourceExists(Long id) {
        if (trafficSourceMapper.selectById(id) == null) {
            throw exception(TRAFFIC_SOURCE_NOT_EXISTS);
        }
    }

    private void validateCodeUnique(Long id, String code) {
        TrafficSourceDO existing = trafficSourceMapper.selectByCode(code);
        if (existing != null && !existing.getId().equals(id)) {
            throw exception(TRAFFIC_SOURCE_CODE_DUPLICATE);
        }
    }
}
