package com.river.module.campaign.dal.mysql;

import com.river.framework.common.pojo.PageResult;
import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.river.module.campaign.controller.admin.trafficsource.vo.TrafficSourcePageReqVO;
import com.river.module.campaign.dal.dataobject.TrafficSourceDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TrafficSourceMapper extends BaseMapperX<TrafficSourceDO> {

    default PageResult<TrafficSourceDO> selectPage(TrafficSourcePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<TrafficSourceDO>()
                .likeIfPresent(TrafficSourceDO::getName, reqVO.getName())
                .likeIfPresent(TrafficSourceDO::getCode, reqVO.getCode())
                .eqIfPresent(TrafficSourceDO::getStatus, reqVO.getStatus())
                .orderByDesc(TrafficSourceDO::getId));
    }

    default TrafficSourceDO selectByCode(String code) {
        return selectOne(TrafficSourceDO::getCode, code);
    }
}
