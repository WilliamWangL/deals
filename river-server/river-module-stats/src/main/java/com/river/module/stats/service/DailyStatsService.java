package com.river.module.stats.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.stats.controller.admin.campaign.vo.CampaignRoiRespVO;
import com.river.module.stats.controller.admin.campaign.vo.CampaignStatsPageReqVO;
import com.river.module.stats.controller.admin.daily.vo.DailyStatsPageReqVO;
import com.river.module.stats.controller.admin.daily.vo.DailyStatsSummaryRespVO;
import com.river.module.stats.controller.admin.daily.vo.DailyStatsTrendRespVO;
import com.river.module.stats.controller.admin.dashboard.vo.DashboardSummaryRespVO;
import com.river.module.stats.controller.admin.dashboard.vo.DashboardTrendReqVO;
import com.river.module.stats.controller.admin.dashboard.vo.DashboardTrendRespVO;
import com.river.module.stats.controller.admin.offer.vo.OfferStatsPageReqVO;
import com.river.module.stats.controller.admin.source.vo.SourceStatsPageReqVO;
import com.river.module.stats.controller.admin.source.vo.SourceStatsRespVO;
import com.river.module.stats.dal.dataobject.DailyStatsDO;

import java.time.LocalDate;
import java.util.List;

public interface DailyStatsService {

    DashboardSummaryRespVO getDashboardSummary(LocalDate startDate, LocalDate endDate);

    List<DashboardTrendRespVO> getDashboardTrend(DashboardTrendReqVO reqVO);

    PageResult<DailyStatsDO> getOfferStatsPage(OfferStatsPageReqVO pageReqVO);

    List<DailyStatsDO> getOfferStatsList(Integer dimensionType, Long dimensionId,
                                          LocalDate startDate, LocalDate endDate);

    PageResult<DailyStatsDO> getCampaignStatsPage(CampaignStatsPageReqVO pageReqVO);

    List<CampaignRoiRespVO> getCampaignRoiList(LocalDate startDate, LocalDate endDate);

    List<SourceStatsRespVO> getSourceStatsList(SourceStatsPageReqVO reqVO);

    // ========== 新增：统一日报统计 API ==========

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
