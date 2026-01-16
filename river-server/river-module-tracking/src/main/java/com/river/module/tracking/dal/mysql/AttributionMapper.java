package com.river.module.tracking.dal.mysql;

import com.river.framework.common.pojo.PageResult;
import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.river.module.tracking.controller.admin.attribution.vo.AttributionPageReqVO;
import com.river.module.tracking.dal.dataobject.AttributionDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AttributionMapper extends BaseMapperX<AttributionDO> {

    default PageResult<AttributionDO> selectPage(AttributionPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AttributionDO>()
                .eqIfPresent(AttributionDO::getConversionId, reqVO.getConversionId())
                .eqIfPresent(AttributionDO::getClickId, reqVO.getClickId())
                .eqIfPresent(AttributionDO::getAttributionType, reqVO.getAttributionType())
                .betweenIfPresent(AttributionDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(AttributionDO::getCreateTime));
    }

}
