package com.river.module.campaign.dal.mysql;

import com.river.framework.common.pojo.PageResult;
import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.river.module.campaign.controller.admin.campaign.vo.CampaignPageReqVO;
import com.river.module.campaign.dal.dataobject.CampaignDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CampaignMapper extends BaseMapperX<CampaignDO> {

    default PageResult<CampaignDO> selectPage(CampaignPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CampaignDO>()
                .likeIfPresent(CampaignDO::getName, reqVO.getName())
                .eqIfPresent(CampaignDO::getTrafficSourceId, reqVO.getTrafficSourceId())
                .eqIfPresent(CampaignDO::getType, reqVO.getType())
                .eqIfPresent(CampaignDO::getStatus, reqVO.getStatus())
                .orderByDesc(CampaignDO::getId));
    }
}
