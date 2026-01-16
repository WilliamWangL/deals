package com.river.module.tracking.dal.mysql;

import com.river.framework.common.pojo.PageResult;
import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.river.module.tracking.controller.admin.click.vo.ClickPageReqVO;
import com.river.module.tracking.dal.dataobject.ClickDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ClickMapper extends BaseMapperX<ClickDO> {

    default PageResult<ClickDO> selectPage(ClickPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ClickDO>()
                .eqIfPresent(ClickDO::getOfferId, reqVO.getOfferId())
                .eqIfPresent(ClickDO::getCampaignId, reqVO.getCampaignId())
                .likeIfPresent(ClickDO::getSub1, reqVO.getSub1())
                .likeIfPresent(ClickDO::getIp, reqVO.getIp())
                .eqIfPresent(ClickDO::getCountry, reqVO.getCountry())
                .betweenIfPresent(ClickDO::getClickTime, reqVO.getClickTime())
                .orderByDesc(ClickDO::getClickTime));
    }

    default ClickDO selectByClickId(String clickId) {
        return selectOne(ClickDO::getClickId, clickId);
    }

}
