package com.river.module.tracking.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.tracking.controller.admin.unattributed.vo.UnattributedConversionPageReqVO;
import com.river.module.tracking.dal.dataobject.UnattributedConversionDO;
import com.river.module.tracking.dal.mysql.UnattributedConversionMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.river.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.river.module.tracking.enums.ErrorCodeConstants.UNATTRIBUTED_CONVERSION_NOT_EXISTS;

@Service
@Validated
@Slf4j
public class UnattributedConversionServiceImpl implements UnattributedConversionService {

    @Resource
    private UnattributedConversionMapper unattributedConversionMapper;

    @Override
    public UnattributedConversionDO getUnattributedConversion(Long id) {
        return unattributedConversionMapper.selectById(id);
    }

    @Override
    public PageResult<UnattributedConversionDO> getUnattributedConversionPage(UnattributedConversionPageReqVO pageReqVO) {
        return unattributedConversionMapper.selectPage(pageReqVO);
    }

}
