package com.river.module.tracking.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.tracking.controller.admin.conversion.vo.ConversionPageReqVO;
import com.river.module.tracking.dal.dataobject.ConversionDO;
import com.river.module.tracking.dal.mysql.ConversionMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.river.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.river.module.tracking.enums.ErrorCodeConstants.CONVERSION_NOT_EXISTS;

@Service
@Validated
public class ConversionServiceImpl implements ConversionService {

    @Resource
    private ConversionMapper conversionMapper;

    @Override
    public Long createConversion(ConversionDO conversion) {
        conversionMapper.insert(conversion);
        return conversion.getId();
    }

    @Override
    public ConversionDO getConversion(Long id) {
        return conversionMapper.selectById(id);
    }

    @Override
    public PageResult<ConversionDO> getConversionPage(ConversionPageReqVO pageReqVO) {
        return conversionMapper.selectPage(pageReqVO);
    }

    @Override
    public void updateConversionStatus(Long id, Integer status) {
        validateConversionExists(id);
        conversionMapper.updateById(ConversionDO.builder().id(id).status(status).build());
    }

    @Override
    public boolean isDuplicateConversion(String networkCode, String externalConversionId) {
        return conversionMapper.selectByNetworkAndExternalId(networkCode, externalConversionId, null) != null;
    }

    @Override
    public void validateConversionExists(Long id) {
        if (conversionMapper.selectById(id) == null) {
            throw exception(CONVERSION_NOT_EXISTS);
        }
    }

}
