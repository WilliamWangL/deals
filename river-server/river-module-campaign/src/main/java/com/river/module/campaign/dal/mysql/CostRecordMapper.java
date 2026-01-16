package com.river.module.campaign.dal.mysql;

import com.river.framework.common.pojo.PageResult;
import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.river.module.campaign.controller.admin.costrecord.vo.CostRecordPageReqVO;
import com.river.module.campaign.dal.dataobject.CostRecordDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CostRecordMapper extends BaseMapperX<CostRecordDO> {

    default PageResult<CostRecordDO> selectPage(CostRecordPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CostRecordDO>()
                .eqIfPresent(CostRecordDO::getCampaignId, reqVO.getCampaignId())
                .eqIfPresent(CostRecordDO::getAdGroupId, reqVO.getAdGroupId())
                .eqIfPresent(CostRecordDO::getSource, reqVO.getSource())
                .betweenIfPresent(CostRecordDO::getDate, reqVO.getStartDate(), reqVO.getEndDate())
                .orderByDesc(CostRecordDO::getDate));
    }
}
