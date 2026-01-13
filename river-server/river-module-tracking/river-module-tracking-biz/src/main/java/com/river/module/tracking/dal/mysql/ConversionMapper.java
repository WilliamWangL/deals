package com.river.module.tracking.dal.mysql;

import com.river.framework.common.pojo.PageResult;
import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.river.module.tracking.controller.admin.conversion.vo.ConversionPageReqVO;
import com.river.module.tracking.dal.dataobject.ConversionDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConversionMapper extends BaseMapperX<ConversionDO> {

    default PageResult<ConversionDO> selectPage(ConversionPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ConversionDO>()
                .likeIfPresent(ConversionDO::getClickId, reqVO.getClickId())
                .eqIfPresent(ConversionDO::getNetworkCode, reqVO.getNetworkCode())
                .eqIfPresent(ConversionDO::getConversionType, reqVO.getConversionType())
                .eqIfPresent(ConversionDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ConversionDO::getConversionTime, reqVO.getConversionTime())
                .orderByDesc(ConversionDO::getId));
    }

    default ConversionDO selectByNetworkAndExternalId(String networkCode, String externalConversionId, Long tenantId) {
        return selectOne(new LambdaQueryWrapperX<ConversionDO>()
                .eq(ConversionDO::getNetworkCode, networkCode)
                .eq(ConversionDO::getExternalConversionId, externalConversionId));
    }

}
