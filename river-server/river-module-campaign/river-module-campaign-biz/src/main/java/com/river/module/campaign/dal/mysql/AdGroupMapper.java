package com.river.module.campaign.dal.mysql;

import com.river.framework.common.pojo.PageResult;
import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.river.module.campaign.controller.admin.adgroup.vo.AdGroupPageReqVO;
import com.river.module.campaign.dal.dataobject.AdGroupDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdGroupMapper extends BaseMapperX<AdGroupDO> {

    default PageResult<AdGroupDO> selectPage(AdGroupPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AdGroupDO>()
                .likeIfPresent(AdGroupDO::getName, reqVO.getName())
                .eqIfPresent(AdGroupDO::getCampaignId, reqVO.getCampaignId())
                .eqIfPresent(AdGroupDO::getStatus, reqVO.getStatus())
                .orderByDesc(AdGroupDO::getId));
    }
}
