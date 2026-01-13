package com.river.module.tracking.dal.mysql;

import com.river.framework.common.pojo.PageResult;
import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.river.module.tracking.dal.dataobject.UnattributedConversionDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UnattributedConversionMapper extends BaseMapperX<UnattributedConversionDO> {

}
