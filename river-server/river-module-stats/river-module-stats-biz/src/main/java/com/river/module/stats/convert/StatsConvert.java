package com.river.module.stats.convert;

import com.river.framework.common.pojo.PageResult;
import com.river.module.stats.controller.admin.dashboard.vo.DashboardTrendRespVO;
import com.river.module.stats.controller.admin.offer.vo.OfferStatsRespVO;
import com.river.module.stats.dal.dataobject.DailyStatsDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface StatsConvert {

    StatsConvert INSTANCE = Mappers.getMapper(StatsConvert.class);

    OfferStatsRespVO convert(DailyStatsDO bean);

    List<OfferStatsRespVO> convertList(List<DailyStatsDO> list);

    PageResult<OfferStatsRespVO> convertPage(PageResult<DailyStatsDO> page);

    DashboardTrendRespVO convertToTrend(DailyStatsDO bean);

    List<DashboardTrendRespVO> convertToTrendList(List<DailyStatsDO> list);

}
