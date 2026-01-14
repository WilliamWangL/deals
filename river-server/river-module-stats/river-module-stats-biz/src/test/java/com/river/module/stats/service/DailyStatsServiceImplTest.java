package com.river.module.stats.service;

import com.river.framework.test.core.ut.BaseMockitoUnitTest;
import com.river.module.stats.controller.admin.dashboard.vo.DashboardSummaryRespVO;
import com.river.module.stats.dal.dataobject.DailyStatsDO;
import com.river.module.stats.dal.mysql.DailyStatsMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * {@link DailyStatsServiceImpl} 的单元测试
 */
class DailyStatsServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private DailyStatsServiceImpl dailyStatsService;

    @Mock
    private DailyStatsMapper dailyStatsMapper;

    @Test
    void testGetDashboardSummary_WithData() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        
        DailyStatsDO stats1 = new DailyStatsDO();
        stats1.setClicks(100);
        stats1.setConversions(10);
        stats1.setRevenue(new BigDecimal("1000.00"));
        stats1.setCost(new BigDecimal("500.00"));
        stats1.setProfit(new BigDecimal("500.00"));
        
        DailyStatsDO stats2 = new DailyStatsDO();
        stats2.setClicks(200);
        stats2.setConversions(20);
        stats2.setRevenue(new BigDecimal("2000.00"));
        stats2.setCost(new BigDecimal("1000.00"));
        stats2.setProfit(new BigDecimal("1000.00"));
        
        List<DailyStatsDO> statsList = Arrays.asList(stats1, stats2);
        when(dailyStatsMapper.selectListByDateRange(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(statsList);

        // When
        DashboardSummaryRespVO result = dailyStatsService.getDashboardSummary(startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(300, result.getTotalClicks());
        assertEquals(30, result.getTotalConversions());
        assertEquals(new BigDecimal("3000.00"), result.getTotalRevenue());
        assertEquals(new BigDecimal("1500.00"), result.getTotalCost());
        assertEquals(new BigDecimal("1500.00"), result.getTotalProfit());
        verify(dailyStatsMapper).selectListByDateRange(startDate, endDate);
    }

    @Test
    void testGetDashboardSummary_EmptyData() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        when(dailyStatsMapper.selectListByDateRange(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        // When
        DashboardSummaryRespVO result = dailyStatsService.getDashboardSummary(startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotalClicks());
        assertEquals(0, result.getTotalConversions());
        assertEquals(BigDecimal.ZERO, result.getTotalRevenue());
        assertEquals(BigDecimal.ZERO, result.getTotalCost());
        assertEquals(BigDecimal.ZERO, result.getTotalProfit());
        verify(dailyStatsMapper).selectListByDateRange(startDate, endDate);
    }

    @Test
    void testGetDashboardSummary_CalculatesEpcCorrectly() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        
        DailyStatsDO stats = new DailyStatsDO();
        stats.setClicks(100);
        stats.setConversions(10);
        stats.setRevenue(new BigDecimal("1000.00"));
        stats.setCost(new BigDecimal("500.00"));
        stats.setProfit(new BigDecimal("500.00"));
        
        when(dailyStatsMapper.selectListByDateRange(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.singletonList(stats));

        // When
        DashboardSummaryRespVO result = dailyStatsService.getDashboardSummary(startDate, endDate);

        // Then
        assertNotNull(result);
        // EPC = Revenue / Clicks = 1000 / 100 = 10.0000
        assertEquals(new BigDecimal("10.0000"), result.getAvgEpc());
        verify(dailyStatsMapper).selectListByDateRange(startDate, endDate);
    }

}
