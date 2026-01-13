package com.river.module.stats.dal.mysql;

import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.river.module.stats.dal.dataobject.HourlyStatsDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface HourlyStatsMapper extends BaseMapperX<HourlyStatsDO> {

    default List<HourlyStatsDO> selectListByDimension(Integer dimensionType, Long dimensionId,
                                                       LocalDateTime startHour, LocalDateTime endHour) {
        return selectList(new LambdaQueryWrapperX<HourlyStatsDO>()
                .eq(HourlyStatsDO::getDimensionType, dimensionType)
                .eqIfPresent(HourlyStatsDO::getDimensionId, dimensionId)
                .geIfPresent(HourlyStatsDO::getHour, startHour)
                .leIfPresent(HourlyStatsDO::getHour, endHour)
                .orderByAsc(HourlyStatsDO::getHour));
    }

    default List<HourlyStatsDO> selectListByHourRange(LocalDateTime startHour, LocalDateTime endHour) {
        return selectList(new LambdaQueryWrapperX<HourlyStatsDO>()
                .geIfPresent(HourlyStatsDO::getHour, startHour)
                .leIfPresent(HourlyStatsDO::getHour, endHour));
    }

}
