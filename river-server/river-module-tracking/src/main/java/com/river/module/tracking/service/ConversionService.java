package com.river.module.tracking.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.tracking.controller.admin.conversion.vo.ConversionPageReqVO;
import com.river.module.tracking.dal.dataobject.ConversionDO;

public interface ConversionService {

    Long createConversion(ConversionDO conversion);

    ConversionDO getConversion(Long id);

    PageResult<ConversionDO> getConversionPage(ConversionPageReqVO pageReqVO);

    void updateConversionStatus(Long id, Integer status);

    boolean isDuplicateConversion(String networkCode, String externalConversionId);

    void validateConversionExists(Long id);

}
