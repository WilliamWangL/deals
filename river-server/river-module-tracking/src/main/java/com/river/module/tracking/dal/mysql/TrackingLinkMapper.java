package com.river.module.tracking.dal.mysql;

import com.river.framework.common.pojo.PageResult;
import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.river.module.tracking.controller.admin.trackinglink.vo.TrackingLinkPageReqVO;
import com.river.module.tracking.dal.dataobject.TrackingLinkDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TrackingLinkMapper extends BaseMapperX<TrackingLinkDO> {

    default PageResult<TrackingLinkDO> selectPage(TrackingLinkPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<TrackingLinkDO>()
                .eqIfPresent(TrackingLinkDO::getOfferId, reqVO.getOfferId())
                .likeIfPresent(TrackingLinkDO::getSlug, reqVO.getSlug())
                .eqIfPresent(TrackingLinkDO::getStatus, reqVO.getStatus())
                .orderByDesc(TrackingLinkDO::getId));
    }

    default TrackingLinkDO selectBySlug(String slug) {
        return selectOne(TrackingLinkDO::getSlug, slug);
    }

}
