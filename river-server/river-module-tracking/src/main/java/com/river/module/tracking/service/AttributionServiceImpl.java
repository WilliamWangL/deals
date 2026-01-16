package com.river.module.tracking.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.tracking.controller.admin.attribution.vo.AttributionPageReqVO;
import com.river.module.tracking.dal.dataobject.AttributionDO;
import com.river.module.tracking.dal.mysql.AttributionMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.river.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.river.module.tracking.enums.ErrorCodeConstants.ATTRIBUTION_NOT_EXISTS;

@Service
@Validated
@Slf4j
public class AttributionServiceImpl implements AttributionService {

    @Resource
    private AttributionMapper attributionMapper;

    @Override
    public AttributionDO getAttribution(Long id) {
        return attributionMapper.selectById(id);
    }

    @Override
    public PageResult<AttributionDO> getAttributionPage(AttributionPageReqVO pageReqVO) {
        return attributionMapper.selectPage(pageReqVO);
    }

}
