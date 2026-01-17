package com.river.module.stats.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.stats.controller.admin.daily.vo.DailyStatsPageReqVO;
import com.river.module.stats.controller.admin.daily.vo.DailyStatsSummaryRespVO;
import com.river.module.stats.controller.admin.daily.vo.DailyStatsTrendRespVO;
import com.river.module.stats.dal.dataobject.DailyStatsDO;

import java.util.List;

/**
 * 日报统计 Service 接口
 */
public interface DailyStatsService {

    /**
     * 分页查询日报统计
     */
    PageResult<DailyStatsDO> getDailyStatsPage(DailyStatsPageReqVO pageReqVO);

    /**
     * 获取日报统计汇总
     */
    DailyStatsSummaryRespVO getDailyStatsSummary(DailyStatsPageReqVO reqVO);

    /**
     * 获取日报统计趋势
     */
    List<DailyStatsTrendRespVO> getDailyStatsTrend(DailyStatsPageReqVO reqVO);

}
