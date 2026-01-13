package com.river.module.tracking.dal.mysql;

import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.module.tracking.dal.dataobject.AttributionDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AttributionMapper extends BaseMapperX<AttributionDO> {

}
