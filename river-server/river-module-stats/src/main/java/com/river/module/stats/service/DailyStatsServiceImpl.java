package com.river.module.stats.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.stats.controller.admin.daily.vo.DailyStatsPageReqVO;
import com.river.module.stats.controller.admin.daily.vo.DailyStatsSummaryRespVO;
import com.river.module.stats.controller.admin.daily.vo.DailyStatsTrendRespVO;
import com.river.module.stats.convert.StatsConvert;
import com.river.module.stats.dal.dataobject.DailyStatsDO;
import com.river.module.stats.dal.mysql.DailyStatsMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 日报统计 Service 实现类
 */
@Service
@Validated
public class DailyStatsServiceImpl implements DailyStatsService {

    @Resource
    private DailyStatsMapper dailyStatsMapper;

    @Override
    public PageResult<DailyStatsDO> getDailyStatsPage(DailyStatsPageReqVO pageReqVO) {
        return dailyStatsMapper.selectDailyStatsPage(pageReqVO);
    }

    @Override
    public DailyStatsSummaryRespVO getDailyStatsSummary(DailyStatsPageReqVO reqVO) {
        List<DailyStatsDO> stats = dailyStatsMapper.selectListByCondition(
                reqVO.getDimensionType(),
                reqVO.getDimensionId(),
                reqVO.getStartDate(),
                reqVO.getEndDate());

        DailyStatsSummaryRespVO resp = new DailyStatsSummaryRespVO();
        resp.setTotalClicks(stats.stream().mapToInt(s -> s.getClicks() != null ? s.getClicks() : 0).sum());
        resp.setTotalConversions(stats.stream().mapToInt(s -> s.getConversions() != null ? s.getConversions() : 0).sum());
        resp.setTotalRevenue(stats.stream()
                .map(s -> s.getRevenue() != null ? s.getRevenue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        resp.setTotalCost(stats.stream()
                .map(s -> s.getCost() != null ? s.getCost() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        resp.setTotalProfit(stats.stream()
                .map(s -> s.getProfit() != null ? s.getProfit() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        if (resp.getTotalClicks() > 0) {
            resp.setAvgEpc(resp.getTotalRevenue().divide(BigDecimal.valueOf(resp.getTotalClicks()), 4, RoundingMode.HALF_UP));
            resp.setAvgCr(BigDecimal.valueOf(resp.getTotalConversions())
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(resp.getTotalClicks()), 2, RoundingMode.HALF_UP));
        } else {
            resp.setAvgEpc(BigDecimal.ZERO);
            resp.setAvgCr(BigDecimal.ZERO);
        }

        if (resp.getTotalCost().compareTo(BigDecimal.ZERO) > 0) {
            resp.setAvgRoi(resp.getTotalProfit()
                    .multiply(BigDecimal.valueOf(100))
                    .divide(resp.getTotalCost(), 2, RoundingMode.HALF_UP));
        } else {
            resp.setAvgRoi(BigDecimal.ZERO);
        }

        return resp;
    }

    @Override
    public List<DailyStatsTrendRespVO> getDailyStatsTrend(DailyStatsPageReqVO reqVO) {
        List<DailyStatsDO> stats = dailyStatsMapper.selectListByCondition(
                reqVO.getDimensionType(),
                reqVO.getDimensionId(),
                reqVO.getStartDate(),
                reqVO.getEndDate());
        return StatsConvert.INSTANCE.convertToDailyTrendList(stats);
    }

}
