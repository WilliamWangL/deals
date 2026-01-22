package com.river.module.stats.dal.mysql;

import com.river.framework.common.pojo.PageResult;
import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.river.module.stats.controller.admin.daily.vo.DailyStatsPageReqVO;
import com.river.module.stats.dal.dataobject.DailyStatsDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface DailyStatsMapper extends BaseMapperX<DailyStatsDO> {

    /**
     * 分页查询日报统计
     */
    default PageResult<DailyStatsDO> selectDailyStatsPage(DailyStatsPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DailyStatsDO>()
                .eqIfPresent(DailyStatsDO::getDimensionType, reqVO.getDimensionType())
                .eqIfPresent(DailyStatsDO::getDimensionId, reqVO.getDimensionId())
                .eqIfPresent(DailyStatsDO::getOfferId, reqVO.getOfferId())
                .eqIfPresent(DailyStatsDO::getCampaignId, reqVO.getCampaignId())
                .eqIfPresent(DailyStatsDO::getTrafficSourceId, reqVO.getTrafficSourceId())
                .geIfPresent(DailyStatsDO::getDate, reqVO.getStartDate())
                .leIfPresent(DailyStatsDO::getDate, reqVO.getEndDate())
                .orderByDesc(DailyStatsDO::getDate));
    }

    /**
     * 根据条件查询日报统计列表
     */
    default List<DailyStatsDO> selectListByCondition(Integer dimensionType, Long dimensionId,
                                                       LocalDate startDate, LocalDate endDate) {
        return selectList(new LambdaQueryWrapperX<DailyStatsDO>()
                .eqIfPresent(DailyStatsDO::getDimensionType, dimensionType)
                .eqIfPresent(DailyStatsDO::getDimensionId, dimensionId)
                .geIfPresent(DailyStatsDO::getDate, startDate)
                .leIfPresent(DailyStatsDO::getDate, endDate)
                .orderByAsc(DailyStatsDO::getDate));
    }

    /**
     * 根据日期范围查询日报统计列表
     */
    default List<DailyStatsDO> selectListByDateRange(LocalDate startDate, LocalDate endDate) {
        return selectList(new LambdaQueryWrapperX<DailyStatsDO>()
                .geIfPresent(DailyStatsDO::getDate, startDate)
                .leIfPresent(DailyStatsDO::getDate, endDate));
    }

    /**
     * 插入或更新日报统计
     * 根据 date + dimensionType + dimensionId 唯一键判断
     */
    default void upsertStats(DailyStatsDO stats) {
        DailyStatsDO existing = selectOne(new LambdaQueryWrapperX<DailyStatsDO>()
                .eq(DailyStatsDO::getDate, stats.getDate())
                .eq(DailyStatsDO::getDimensionType, stats.getDimensionType())
                .eq(DailyStatsDO::getDimensionId, stats.getDimensionId()));

        if (existing != null) {
            stats.setId(existing.getId());
            updateById(stats);
        } else {
            insert(stats);
        }
    }

}
