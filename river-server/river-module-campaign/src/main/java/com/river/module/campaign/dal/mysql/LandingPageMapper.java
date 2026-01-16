package com.river.module.campaign.dal.mysql;

import com.river.framework.common.pojo.PageResult;
import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.river.module.campaign.controller.admin.landingpage.vo.LandingPagePageReqVO;
import com.river.module.campaign.dal.dataobject.LandingPageDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LandingPageMapper extends BaseMapperX<LandingPageDO> {

    default PageResult<LandingPageDO> selectPage(LandingPagePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<LandingPageDO>()
                .likeIfPresent(LandingPageDO::getName, reqVO.getName())
                .eqIfPresent(LandingPageDO::getType, reqVO.getType())
                .eqIfPresent(LandingPageDO::getStatus, reqVO.getStatus())
                .orderByDesc(LandingPageDO::getId));
    }

    default LandingPageDO selectBySlug(String slug) {
        return selectOne(LandingPageDO::getSlug, slug);
    }
}
