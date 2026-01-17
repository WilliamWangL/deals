package com.river.module.stats.dal.mysql;

import com.river.framework.common.pojo.PageResult;
import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.river.module.stats.controller.admin.daily.vo.DailyStatsPageReqVO;
import com.river.module.stats.controller.admin.offer.vo.OfferStatsPageReqVO;
import com.river.module.stats.dal.dataobject.DailyStatsDO;
import com.river.module.stats.enums.DimensionTypeEnum;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface DailyStatsMapper extends BaseMapperX<DailyStatsDO> {

    default PageResult<DailyStatsDO> selectPage(OfferStatsPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DailyStatsDO>()
                .eqIfPresent(DailyStatsDO::getDimensionType, reqVO.getDimensionType())
                .eqIfPresent(DailyStatsDO::getDimensionId, reqVO.getDimensionId())
                .geIfPresent(DailyStatsDO::getDate, reqVO.getStartDate())
                .leIfPresent(DailyStatsDO::getDate, reqVO.getEndDate())
                .orderByDesc(DailyStatsDO::getDate));
    }

    default List<DailyStatsDO> selectListByDimension(Integer dimensionType, Long dimensionId,
                                                      LocalDate startDate, LocalDate endDate) {
        return selectList(new LambdaQueryWrapperX<DailyStatsDO>()
                .eq(DailyStatsDO::getDimensionType, dimensionType)
                .eqIfPresent(DailyStatsDO::getDimensionId, dimensionId)
                .geIfPresent(DailyStatsDO::getDate, startDate)
                .leIfPresent(DailyStatsDO::getDate, endDate)
                .orderByAsc(DailyStatsDO::getDate));
    }

    default List<DailyStatsDO> selectListByDateRange(LocalDate startDate, LocalDate endDate) {
        return selectList(new LambdaQueryWrapperX<DailyStatsDO>()
                .geIfPresent(DailyStatsDO::getDate, startDate)
                .leIfPresent(DailyStatsDO::getDate, endDate));
    }

    default List<DailyStatsDO> selectListByDimensionType(Integer dimensionType, LocalDate startDate, LocalDate endDate) {
        return selectList(new LambdaQueryWrapperX<DailyStatsDO>()
                .eq(DailyStatsDO::getDimensionType, dimensionType)
                .geIfPresent(DailyStatsDO::getDate, startDate)
                .leIfPresent(DailyStatsDO::getDate, endDate)
                .orderByDesc(DailyStatsDO::getProfit));
    }

    // ========== 新增：统一日报统计 API ==========

    default PageResult<DailyStatsDO> selectDailyStatsPage(DailyStatsPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DailyStatsDO>()
                .eqIfPresent(DailyStatsDO::getDimensionType, reqVO.getDimensionType())
                .eqIfPresent(DailyStatsDO::getDimensionId, reqVO.getDimensionId())
                .geIfPresent(DailyStatsDO::getDate, reqVO.getStartDate())
                .leIfPresent(DailyStatsDO::getDate, reqVO.getEndDate())
                .orderByDesc(DailyStatsDO::getDate));
    }

    default List<DailyStatsDO> selectListByCondition(Integer dimensionType, Long dimensionId,
                                                       LocalDate startDate, LocalDate endDate) {
        return selectList(new LambdaQueryWrapperX<DailyStatsDO>()
                .eqIfPresent(DailyStatsDO::getDimensionType, dimensionType)
                .eqIfPresent(DailyStatsDO::getDimensionId, dimensionId)
                .geIfPresent(DailyStatsDO::getDate, startDate)
                .leIfPresent(DailyStatsDO::getDate, endDate)
                .orderByAsc(DailyStatsDO::getDate));
    }

}
