package com.river.module.stats.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.stats.controller.admin.campaign.vo.CampaignRoiRespVO;
import com.river.module.stats.controller.admin.campaign.vo.CampaignStatsPageReqVO;
import com.river.module.stats.controller.admin.dashboard.vo.DashboardSummaryRespVO;
import com.river.module.stats.controller.admin.dashboard.vo.DashboardTrendReqVO;
import com.river.module.stats.controller.admin.dashboard.vo.DashboardTrendRespVO;
import com.river.module.stats.controller.admin.offer.vo.OfferStatsPageReqVO;
import com.river.module.stats.controller.admin.source.vo.SourceStatsPageReqVO;
import com.river.module.stats.controller.admin.source.vo.SourceStatsRespVO;
import com.river.module.stats.convert.StatsConvert;
import com.river.module.stats.dal.dataobject.DailyStatsDO;
import com.river.module.stats.dal.mysql.DailyStatsMapper;
import com.river.module.stats.enums.DimensionTypeEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Validated
public class DailyStatsServiceImpl implements DailyStatsService {

    @Resource
    private DailyStatsMapper dailyStatsMapper;

    @Override
    public DashboardSummaryRespVO getDashboardSummary(LocalDate startDate, LocalDate endDate) {
        List<DailyStatsDO> stats = dailyStatsMapper.selectListByDateRange(startDate, endDate);

        DashboardSummaryRespVO resp = new DashboardSummaryRespVO();
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
        } else {
            resp.setAvgEpc(BigDecimal.ZERO);
        }

        if (resp.getTotalClicks() > 0) {
            resp.setAvgCr(BigDecimal.valueOf(resp.getTotalConversions())
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(resp.getTotalClicks()), 2, RoundingMode.HALF_UP));
        } else {
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
    public List<DashboardTrendRespVO> getDashboardTrend(DashboardTrendReqVO reqVO) {
        List<DailyStatsDO> stats = dailyStatsMapper.selectListByDimension(
                reqVO.getDimensionType(),
                reqVO.getDimensionId(),
                reqVO.getStartDate(),
                reqVO.getEndDate());
        return StatsConvert.INSTANCE.convertToTrendList(stats);
    }

    @Override
    public PageResult<DailyStatsDO> getOfferStatsPage(OfferStatsPageReqVO pageReqVO) {
        return dailyStatsMapper.selectPage(pageReqVO);
    }

    @Override
    public List<DailyStatsDO> getOfferStatsList(Integer dimensionType, Long dimensionId,
                                                 LocalDate startDate, LocalDate endDate) {
        return dailyStatsMapper.selectListByDimension(dimensionType, dimensionId, startDate, endDate);
    }

    @Override
    public PageResult<DailyStatsDO> getCampaignStatsPage(CampaignStatsPageReqVO pageReqVO) {
        OfferStatsPageReqVO reqVO = new OfferStatsPageReqVO();
        reqVO.setPageNo(pageReqVO.getPageNo());
        reqVO.setPageSize(pageReqVO.getPageSize());
        reqVO.setDimensionType(DimensionTypeEnum.CAMPAIGN.getType());
        reqVO.setDimensionId(pageReqVO.getCampaignId());
        reqVO.setStartDate(pageReqVO.getStartDate());
        reqVO.setEndDate(pageReqVO.getEndDate());
        return dailyStatsMapper.selectPage(reqVO);
    }

    @Override
    public List<CampaignRoiRespVO> getCampaignRoiList(LocalDate startDate, LocalDate endDate) {
        List<DailyStatsDO> stats = dailyStatsMapper.selectListByDimensionType(
                DimensionTypeEnum.CAMPAIGN.getType(), startDate, endDate);

        Map<Long, List<DailyStatsDO>> grouped = stats.stream()
                .collect(Collectors.groupingBy(DailyStatsDO::getDimensionId));

        List<CampaignRoiRespVO> result = new ArrayList<>();
        for (Map.Entry<Long, List<DailyStatsDO>> entry : grouped.entrySet()) {
            CampaignRoiRespVO roi = new CampaignRoiRespVO();
            roi.setCampaignId(entry.getKey());
            roi.setCampaignName("Campaign-" + entry.getKey());

            List<DailyStatsDO> campaignStats = entry.getValue();
            roi.setTotalClicks(campaignStats.stream().mapToInt(s -> s.getClicks() != null ? s.getClicks() : 0).sum());
            roi.setTotalConversions(campaignStats.stream().mapToInt(s -> s.getConversions() != null ? s.getConversions() : 0).sum());
            roi.setTotalRevenue(campaignStats.stream()
                    .map(s -> s.getRevenue() != null ? s.getRevenue() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            roi.setTotalCost(campaignStats.stream()
                    .map(s -> s.getCost() != null ? s.getCost() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            roi.setTotalProfit(campaignStats.stream()
                    .map(s -> s.getProfit() != null ? s.getProfit() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));

            if (roi.getTotalCost().compareTo(BigDecimal.ZERO) > 0) {
                roi.setRoi(roi.getTotalProfit()
                        .multiply(BigDecimal.valueOf(100))
                        .divide(roi.getTotalCost(), 2, RoundingMode.HALF_UP));
            } else {
                roi.setRoi(BigDecimal.ZERO);
            }

            if (roi.getTotalClicks() > 0) {
                roi.setEpc(roi.getTotalRevenue().divide(BigDecimal.valueOf(roi.getTotalClicks()), 4, RoundingMode.HALF_UP));
                roi.setCr(BigDecimal.valueOf(roi.getTotalConversions())
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(roi.getTotalClicks()), 2, RoundingMode.HALF_UP));
            } else {
                roi.setEpc(BigDecimal.ZERO);
                roi.setCr(BigDecimal.ZERO);
            }

            result.add(roi);
        }

        return result;
    }

    @Override
    public List<SourceStatsRespVO> getSourceStatsList(SourceStatsPageReqVO reqVO) {
        List<DailyStatsDO> stats = dailyStatsMapper.selectListByDimensionType(
                DimensionTypeEnum.SOURCE.getType(), reqVO.getStartDate(), reqVO.getEndDate());

        Map<Long, List<DailyStatsDO>> grouped = stats.stream()
                .collect(Collectors.groupingBy(DailyStatsDO::getDimensionId));

        List<SourceStatsRespVO> result = new ArrayList<>();
        for (Map.Entry<Long, List<DailyStatsDO>> entry : grouped.entrySet()) {
            SourceStatsRespVO source = new SourceStatsRespVO();
            source.setSourceId(entry.getKey());
            source.setSourceName("Source-" + entry.getKey());

            List<DailyStatsDO> sourceStats = entry.getValue();
            source.setTotalClicks(sourceStats.stream().mapToInt(s -> s.getClicks() != null ? s.getClicks() : 0).sum());
            source.setTotalConversions(sourceStats.stream().mapToInt(s -> s.getConversions() != null ? s.getConversions() : 0).sum());
            source.setTotalRevenue(sourceStats.stream()
                    .map(s -> s.getRevenue() != null ? s.getRevenue() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            source.setTotalCost(sourceStats.stream()
                    .map(s -> s.getCost() != null ? s.getCost() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            source.setTotalProfit(sourceStats.stream()
                    .map(s -> s.getProfit() != null ? s.getProfit() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));

            if (source.getTotalClicks() > 0) {
                source.setEpc(source.getTotalRevenue().divide(BigDecimal.valueOf(source.getTotalClicks()), 4, RoundingMode.HALF_UP));
                source.setCr(BigDecimal.valueOf(source.getTotalConversions())
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(source.getTotalClicks()), 2, RoundingMode.HALF_UP));
            } else {
                source.setEpc(BigDecimal.ZERO);
                source.setCr(BigDecimal.ZERO);
            }

            result.add(source);
        }

        return result;
    }

}
