package com.river.module.campaign.dal.mysql;

import com.river.framework.common.pojo.PageResult;
import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.river.module.campaign.controller.admin.costrecord.vo.CostRecordPageReqVO;
import com.river.module.campaign.dal.dataobject.CostRecordDO;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    /**
     * 按 Campaign 聚合指定日期的成本
     */
    default Map<Long, BigDecimal> selectCostGroupByCampaign(LocalDate date) {
        List<CostRecordDO> costs = selectList(new LambdaQueryWrapperX<CostRecordDO>()
                .eq(CostRecordDO::getDate, date));
        return costs.stream()
                .collect(Collectors.toMap(
                        CostRecordDO::getCampaignId,
                        CostRecordDO::getCost,
                        BigDecimal::add));
    }
}
