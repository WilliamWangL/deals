package com.river.module.tracking.dal.mysql;

import com.river.framework.common.pojo.PageResult;
import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.river.module.tracking.controller.admin.trackinglink.vo.TrackingLinkPageReqVO;
import com.river.module.tracking.dal.dataobject.TrackingLinkDO;
import com.river.module.tracking.enums.TrackingLinkStatusEnum;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collections;
import java.util.List;

@Mapper
public interface TrackingLinkMapper extends BaseMapperX<TrackingLinkDO> {

    default TrackingLinkDO selectBySlug(String slug) {
        return selectOne(new LambdaQueryWrapperX<TrackingLinkDO>()
                .eq(TrackingLinkDO::getSlug, slug)
                .eq(TrackingLinkDO::getStatus, TrackingLinkStatusEnum.ENABLED.getCode()));
    }

    default TrackingLinkDO selectByTarget(Integer targetType, Long targetId) {
        return selectOne(new LambdaQueryWrapperX<TrackingLinkDO>()
                .eq(TrackingLinkDO::getTargetType, targetType)
                .eq(TrackingLinkDO::getTargetId, targetId));
    }

    default List<TrackingLinkDO> selectByTargets(Integer targetType, List<Long> targetIds) {
        if (targetIds == null || targetIds.isEmpty()) {
            return Collections.emptyList();
        }
        return selectList(new LambdaQueryWrapperX<TrackingLinkDO>()
                .eq(TrackingLinkDO::getTargetType, targetType)
                .in(TrackingLinkDO::getTargetId, targetIds));
    }

    default PageResult<TrackingLinkDO> selectPage(TrackingLinkPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<TrackingLinkDO>()
                .eqIfPresent(TrackingLinkDO::getTargetType, reqVO.getTargetType())
                .eqIfPresent(TrackingLinkDO::getTargetId, reqVO.getTargetId())
                .likeIfPresent(TrackingLinkDO::getSlug, reqVO.getSlug())
                .eqIfPresent(TrackingLinkDO::getStatus, reqVO.getStatus())
                .orderByDesc(TrackingLinkDO::getId));
    }

}
