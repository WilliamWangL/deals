package com.river.module.tracking.dal.mysql;

import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.river.module.tracking.dal.dataobject.TrackingLinkDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TrackingLinkMapper extends BaseMapperX<TrackingLinkDO> {

    default TrackingLinkDO selectBySlug(String slug) {
        return selectOne(new LambdaQueryWrapperX<TrackingLinkDO>()
                .eq(TrackingLinkDO::getSlug, slug)
                .eq(TrackingLinkDO::getStatus, 1));
    }

    default TrackingLinkDO selectByTarget(Integer targetType, Long targetId) {
        return selectOne(new LambdaQueryWrapperX<TrackingLinkDO>()
                .eq(TrackingLinkDO::getTargetType, targetType)
                .eq(TrackingLinkDO::getTargetId, targetId));
    }
}
