# Stats 模块优化实施计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 优化 river-module-stats 统计模块，合并为统一的统计报表系统

**Architecture:** 创建统一的 DailyStatsController 提供分页/汇总/趋势 API，前端合并为单一统计报表页面，实现数据聚合 Job

**Tech Stack:** Java 17, Spring Boot 3.5, MyBatis Plus, Vue 3, Element Plus, ECharts, PostgreSQL 17

---

## Task 1: 扩展 DimensionTypeEnum

**Files:**
- Modify: `river-server/river-module-stats/src/main/java/com/river/module/stats/enums/DimensionTypeEnum.java`

**Step 1: 添加 MERCHANT 和 CATEGORY 维度**

```java
package com.river.module.stats.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 维度类型枚举
 */
@Getter
@AllArgsConstructor
public enum DimensionTypeEnum {

    CAMPAIGN(1, "活动"),
    SOURCE(2, "流量源"),
    OFFER(3, "Offer"),
    LANDING_PAGE(4, "落地页"),
    MERCHANT(5, "商家"),
    CATEGORY(6, "分类");

    private final Integer type;
    private final String name;

    public static DimensionTypeEnum getByType(Integer type) {
        for (DimensionTypeEnum value : values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return null;
    }

}
```

**Step 2: 编译验证**

Run: `cd river-server && mvn compile -pl river-module-stats -am -q`
Expected: BUILD SUCCESS

**Step 3: Commit**

```bash
git add river-server/river-module-stats/src/main/java/com/river/module/stats/enums/DimensionTypeEnum.java
git commit -m "feat(stats): add MERCHANT and CATEGORY dimension types"
```

---

## Task 2: 创建 DailyStatsPageReqVO

**Files:**
- Create: `river-server/river-module-stats/src/main/java/com/river/module/stats/controller/admin/daily/vo/DailyStatsPageReqVO.java`

**Step 1: 创建分页请求 VO**

```java
package com.river.module.stats.controller.admin.daily.vo;

import com.river.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

import static com.river.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Schema(description = "管理后台 - 日报统计分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class DailyStatsPageReqVO extends PageParam {

    @Schema(description = "维度类型", example = "1")
    private Integer dimensionType;

    @Schema(description = "维度 ID", example = "1024")
    private Long dimensionId;

    @Schema(description = "开始日期", example = "2024-01-01")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate startDate;

    @Schema(description = "结束日期", example = "2024-01-31")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate endDate;

}
```

**Step 2: 编译验证**

Run: `cd river-server && mvn compile -pl river-module-stats -am -q`
Expected: BUILD SUCCESS

**Step 3: Commit**

```bash
git add river-server/river-module-stats/src/main/java/com/river/module/stats/controller/admin/daily/vo/DailyStatsPageReqVO.java
git commit -m "feat(stats): add DailyStatsPageReqVO"
```

---

## Task 3: 创建 DailyStatsRespVO

**Files:**
- Create: `river-server/river-module-stats/src/main/java/com/river/module/stats/controller/admin/daily/vo/DailyStatsRespVO.java`

**Step 1: 创建响应 VO**

```java
package com.river.module.stats.controller.admin.daily.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 日报统计 Response VO")
@Data
public class DailyStatsRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "统计日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate date;

    @Schema(description = "维度类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer dimensionType;

    @Schema(description = "维度 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long dimensionId;

    @Schema(description = "维度名称", example = "Campaign-001")
    private String dimensionName;

    @Schema(description = "点击数", requiredMode = Schema.RequiredMode.REQUIRED, example = "1000")
    private Integer clicks;

    @Schema(description = "转化数", requiredMode = Schema.RequiredMode.REQUIRED, example = "50")
    private Integer conversions;

    @Schema(description = "收入", requiredMode = Schema.RequiredMode.REQUIRED, example = "999.99")
    private BigDecimal revenue;

    @Schema(description = "成本", requiredMode = Schema.RequiredMode.REQUIRED, example = "500.00")
    private BigDecimal cost;

    @Schema(description = "利润", requiredMode = Schema.RequiredMode.REQUIRED, example = "499.99")
    private BigDecimal profit;

    @Schema(description = "EPC", requiredMode = Schema.RequiredMode.REQUIRED, example = "0.9999")
    private BigDecimal epc;

    @Schema(description = "转化率", requiredMode = Schema.RequiredMode.REQUIRED, example = "5.00")
    private BigDecimal cr;

    @Schema(description = "ROI", requiredMode = Schema.RequiredMode.REQUIRED, example = "99.99")
    private BigDecimal roi;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
```

**Step 2: 编译验证**

Run: `cd river-server && mvn compile -pl river-module-stats -am -q`
Expected: BUILD SUCCESS

**Step 3: Commit**

```bash
git add river-server/river-module-stats/src/main/java/com/river/module/stats/controller/admin/daily/vo/DailyStatsRespVO.java
git commit -m "feat(stats): add DailyStatsRespVO"
```

---

## Task 4: 创建 DailyStatsSummaryRespVO

**Files:**
- Create: `river-server/river-module-stats/src/main/java/com/river/module/stats/controller/admin/daily/vo/DailyStatsSummaryRespVO.java`

**Step 1: 创建汇总响应 VO**

```java
package com.river.module.stats.controller.admin.daily.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 日报统计汇总 Response VO")
@Data
public class DailyStatsSummaryRespVO {

    @Schema(description = "总点击数", requiredMode = Schema.RequiredMode.REQUIRED, example = "10000")
    private Integer totalClicks;

    @Schema(description = "总转化数", requiredMode = Schema.RequiredMode.REQUIRED, example = "500")
    private Integer totalConversions;

    @Schema(description = "总收入", requiredMode = Schema.RequiredMode.REQUIRED, example = "9999.99")
    private BigDecimal totalRevenue;

    @Schema(description = "总成本", requiredMode = Schema.RequiredMode.REQUIRED, example = "5000.00")
    private BigDecimal totalCost;

    @Schema(description = "总利润", requiredMode = Schema.RequiredMode.REQUIRED, example = "4999.99")
    private BigDecimal totalProfit;

    @Schema(description = "平均 EPC", requiredMode = Schema.RequiredMode.REQUIRED, example = "0.99")
    private BigDecimal avgEpc;

    @Schema(description = "平均转化率", requiredMode = Schema.RequiredMode.REQUIRED, example = "5.00")
    private BigDecimal avgCr;

    @Schema(description = "平均 ROI", requiredMode = Schema.RequiredMode.REQUIRED, example = "99.99")
    private BigDecimal avgRoi;

}
```

**Step 2: 编译验证**

Run: `cd river-server && mvn compile -pl river-module-stats -am -q`
Expected: BUILD SUCCESS

**Step 3: Commit**

```bash
git add river-server/river-module-stats/src/main/java/com/river/module/stats/controller/admin/daily/vo/DailyStatsSummaryRespVO.java
git commit -m "feat(stats): add DailyStatsSummaryRespVO"
```

---

## Task 5: 创建 DailyStatsTrendRespVO

**Files:**
- Create: `river-server/river-module-stats/src/main/java/com/river/module/stats/controller/admin/daily/vo/DailyStatsTrendRespVO.java`

**Step 1: 创建趋势响应 VO**

```java
package com.river.module.stats.controller.admin.daily.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "管理后台 - 日报统计趋势 Response VO")
@Data
public class DailyStatsTrendRespVO {

    @Schema(description = "日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate date;

    @Schema(description = "点击数", requiredMode = Schema.RequiredMode.REQUIRED, example = "1000")
    private Integer clicks;

    @Schema(description = "转化数", requiredMode = Schema.RequiredMode.REQUIRED, example = "50")
    private Integer conversions;

    @Schema(description = "收入", requiredMode = Schema.RequiredMode.REQUIRED, example = "999.99")
    private BigDecimal revenue;

    @Schema(description = "成本", requiredMode = Schema.RequiredMode.REQUIRED, example = "500.00")
    private BigDecimal cost;

    @Schema(description = "利润", requiredMode = Schema.RequiredMode.REQUIRED, example = "499.99")
    private BigDecimal profit;

}
```

**Step 2: 编译验证**

Run: `cd river-server && mvn compile -pl river-module-stats -am -q`
Expected: BUILD SUCCESS

**Step 3: Commit**

```bash
git add river-server/river-module-stats/src/main/java/com/river/module/stats/controller/admin/daily/vo/DailyStatsTrendRespVO.java
git commit -m "feat(stats): add DailyStatsTrendRespVO"
```

---

## Task 6: 更新 StatsConvert

**Files:**
- Modify: `river-server/river-module-stats/src/main/java/com/river/module/stats/convert/StatsConvert.java`

**Step 1: 添加新的转换方法**

```java
package com.river.module.stats.convert;

import com.river.framework.common.pojo.PageResult;
import com.river.module.stats.controller.admin.daily.vo.DailyStatsRespVO;
import com.river.module.stats.controller.admin.daily.vo.DailyStatsTrendRespVO;
import com.river.module.stats.controller.admin.dashboard.vo.DashboardTrendRespVO;
import com.river.module.stats.dal.dataobject.DailyStatsDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface StatsConvert {

    StatsConvert INSTANCE = Mappers.getMapper(StatsConvert.class);

    List<DashboardTrendRespVO> convertToTrendList(List<DailyStatsDO> list);

    DailyStatsRespVO convert(DailyStatsDO bean);

    List<DailyStatsRespVO> convertList(List<DailyStatsDO> list);

    PageResult<DailyStatsRespVO> convertPage(PageResult<DailyStatsDO> page);

    DailyStatsTrendRespVO convertToTrend(DailyStatsDO bean);

    List<DailyStatsTrendRespVO> convertToTrendRespList(List<DailyStatsDO> list);

}
```

**Step 2: 编译验证**

Run: `cd river-server && mvn compile -pl river-module-stats -am -q`
Expected: BUILD SUCCESS

**Step 3: Commit**

```bash
git add river-server/river-module-stats/src/main/java/com/river/module/stats/convert/StatsConvert.java
git commit -m "feat(stats): add convert methods for daily stats"
```

---

## Task 7: 扩展 DailyStatsService 接口

**Files:**
- Modify: `river-server/river-module-stats/src/main/java/com/river/module/stats/service/DailyStatsService.java`

**Step 1: 添加新方法签名**

```java
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

    // ========== 原有方法（保留兼容） ==========

    DashboardSummaryRespVO getDashboardSummary(LocalDate startDate, LocalDate endDate);

    List<DashboardTrendRespVO> getDashboardTrend(DashboardTrendReqVO reqVO);

    PageResult<DailyStatsDO> getOfferStatsPage(OfferStatsPageReqVO pageReqVO);

    List<DailyStatsDO> getOfferStatsList(Integer dimensionType, Long dimensionId,
                                          LocalDate startDate, LocalDate endDate);

    PageResult<DailyStatsDO> getCampaignStatsPage(CampaignStatsPageReqVO pageReqVO);

    List<CampaignRoiRespVO> getCampaignRoiList(LocalDate startDate, LocalDate endDate);

    List<SourceStatsRespVO> getSourceStatsList(SourceStatsPageReqVO reqVO);

}
```

**Step 2: 编译验证**

Run: `cd river-server && mvn compile -pl river-module-stats -am -q`
Expected: FAIL (实现类缺少新方法)

**Step 3: Commit 接口变更**

```bash
git add river-server/river-module-stats/src/main/java/com/river/module/stats/service/DailyStatsService.java
git commit -m "feat(stats): add unified daily stats API methods to interface"
```

---

## Task 8: 实现 DailyStatsService 新方法

**Files:**
- Modify: `river-server/river-module-stats/src/main/java/com/river/module/stats/service/DailyStatsServiceImpl.java`

**Step 1: 添加新方法实现**

在 `DailyStatsServiceImpl` 类中添加以下方法（在类的开头，原有方法之前）：

```java
    // ========== 新增：统一日报统计 API ==========

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
        return StatsConvert.INSTANCE.convertToTrendRespList(stats);
    }
```

**Step 2: 添加 import**

在文件顶部添加：

```java
import com.river.module.stats.controller.admin.daily.vo.DailyStatsPageReqVO;
import com.river.module.stats.controller.admin.daily.vo.DailyStatsSummaryRespVO;
import com.river.module.stats.controller.admin.daily.vo.DailyStatsTrendRespVO;
```

**Step 3: 编译验证**

Run: `cd river-server && mvn compile -pl river-module-stats -am -q`
Expected: FAIL (Mapper 缺少新方法)

**Step 4: Commit**

```bash
git add river-server/river-module-stats/src/main/java/com/river/module/stats/service/DailyStatsServiceImpl.java
git commit -m "feat(stats): implement unified daily stats API methods"
```

---

## Task 9: 扩展 DailyStatsMapper

**Files:**
- Modify: `river-server/river-module-stats/src/main/java/com/river/module/stats/dal/mysql/DailyStatsMapper.java`

**Step 1: 添加新的查询方法**

```java
package com.river.module.stats.dal.mysql;

import com.river.framework.common.pojo.PageResult;
import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.river.module.stats.controller.admin.daily.vo.DailyStatsPageReqVO;
import com.river.module.stats.controller.admin.offer.vo.OfferStatsPageReqVO;
import com.river.module.stats.dal.dataobject.DailyStatsDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface DailyStatsMapper extends BaseMapperX<DailyStatsDO> {

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

}
```

**Step 2: 编译验证**

Run: `cd river-server && mvn compile -pl river-module-stats -am -q`
Expected: BUILD SUCCESS

**Step 3: Commit**

```bash
git add river-server/river-module-stats/src/main/java/com/river/module/stats/dal/mysql/DailyStatsMapper.java
git commit -m "feat(stats): add selectDailyStatsPage and selectListByCondition to mapper"
```

---

## Task 10: 创建 DailyStatsController

**Files:**
- Create: `river-server/river-module-stats/src/main/java/com/river/module/stats/controller/admin/daily/DailyStatsController.java`

**Step 1: 创建 Controller**

```java
package com.river.module.stats.controller.admin.daily;

import com.river.framework.common.pojo.CommonResult;
import com.river.framework.common.pojo.PageResult;
import com.river.framework.excel.core.util.ExcelUtils;
import com.river.module.stats.controller.admin.daily.vo.*;
import com.river.module.stats.convert.StatsConvert;
import com.river.module.stats.dal.dataobject.DailyStatsDO;
import com.river.module.stats.service.DailyStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static com.river.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 日报统计")
@RestController
@RequestMapping("/stats/daily")
@Validated
public class DailyStatsController {

    @Resource
    private DailyStatsService dailyStatsService;

    @PostMapping("/page")
    @Operation(summary = "获取日报统计分页")
    @PreAuthorize("@ss.hasPermission('stats:daily:query')")
    public CommonResult<PageResult<DailyStatsRespVO>> getDailyStatsPage(@Valid @RequestBody DailyStatsPageReqVO pageReqVO) {
        PageResult<DailyStatsDO> pageResult = dailyStatsService.getDailyStatsPage(pageReqVO);
        return success(StatsConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/summary")
    @Operation(summary = "获取日报统计汇总")
    @PreAuthorize("@ss.hasPermission('stats:daily:query')")
    public CommonResult<DailyStatsSummaryRespVO> getDailyStatsSummary(@Valid DailyStatsPageReqVO reqVO) {
        return success(dailyStatsService.getDailyStatsSummary(reqVO));
    }

    @GetMapping("/trend")
    @Operation(summary = "获取日报统计趋势")
    @PreAuthorize("@ss.hasPermission('stats:daily:query')")
    public CommonResult<List<DailyStatsTrendRespVO>> getDailyStatsTrend(@Valid DailyStatsPageReqVO reqVO) {
        return success(dailyStatsService.getDailyStatsTrend(reqVO));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出日报统计 Excel")
    @PreAuthorize("@ss.hasPermission('stats:daily:export')")
    public void exportDailyStatsExcel(@Valid DailyStatsPageReqVO reqVO,
                                       HttpServletResponse response) throws IOException {
        reqVO.setPageSize(PageResult.PAGE_SIZE_NONE);
        PageResult<DailyStatsDO> pageResult = dailyStatsService.getDailyStatsPage(reqVO);
        List<DailyStatsRespVO> list = StatsConvert.INSTANCE.convertList(pageResult.getList());
        ExcelUtils.write(response, "日报统计.xls", "数据", DailyStatsRespVO.class, list);
    }

}
```

**Step 2: 编译验证**

Run: `cd river-server && mvn compile -pl river-module-stats -am -q`
Expected: BUILD SUCCESS

**Step 3: Commit**

```bash
git add river-server/river-module-stats/src/main/java/com/river/module/stats/controller/admin/daily/DailyStatsController.java
git commit -m "feat(stats): add unified DailyStatsController"
```

---

## Task 11: 删除冗余 Controller

**Files:**
- Delete: `river-server/river-module-stats/src/main/java/com/river/module/stats/controller/admin/dashboard/`
- Delete: `river-server/river-module-stats/src/main/java/com/river/module/stats/controller/admin/campaign/`
- Delete: `river-server/river-module-stats/src/main/java/com/river/module/stats/controller/admin/offer/`
- Delete: `river-server/river-module-stats/src/main/java/com/river/module/stats/controller/admin/source/`

**Step 1: 删除冗余目录**

```bash
rm -rf river-server/river-module-stats/src/main/java/com/river/module/stats/controller/admin/dashboard
rm -rf river-server/river-module-stats/src/main/java/com/river/module/stats/controller/admin/campaign
rm -rf river-server/river-module-stats/src/main/java/com/river/module/stats/controller/admin/offer
rm -rf river-server/river-module-stats/src/main/java/com/river/module/stats/controller/admin/source
```

**Step 2: 编译验证**

Run: `cd river-server && mvn compile -pl river-module-stats -am -q`
Expected: BUILD SUCCESS

**Step 3: Commit**

```bash
git add -A river-server/river-module-stats/src/main/java/com/river/module/stats/controller/admin/
git commit -m "refactor(stats): remove redundant controllers (dashboard/campaign/offer/source)"
```

---

## Task 12: 更新数据库菜单

**Step 1: 删除旧菜单**

执行 SQL:

```sql
-- 删除 stats 下的旧子菜单
UPDATE system_menu SET deleted = 1 WHERE id IN (20191, 20192, 20193, 20194, 20195);
```

**Step 2: 新增统计报表菜单**

执行 SQL:

```sql
-- 新增统计报表菜单
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (20196, '统计报表', 'stats:daily:query', 2, 1, 20190, 'report', 'ep:data-analysis', 'river/stats/index', 'StatsReport', 0, true, true, true, '1', NOW(), '1', NOW(), 0);
```

**Step 3: 验证菜单**

```sql
SELECT id, name, path, component FROM system_menu WHERE parent_id = 20190 AND deleted = 0;
```

Expected: 只有一条 `统计报表` 记录

---

## Task 13: 更新字典 DIMENSION_TYPE

**Step 1: 查询现有字典**

```sql
SELECT * FROM system_dict_data WHERE dict_type = 'dimension_type' ORDER BY sort;
```

**Step 2: 添加新维度**

```sql
-- 添加 MERCHANT 和 CATEGORY 字典项
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted)
VALUES
(5, '商家', '5', 'dimension_type', 0, 'default', '', '', '1', NOW(), '1', NOW(), 0),
(6, '分类', '6', 'dimension_type', 0, 'default', '', '', '1', NOW(), '1', NOW(), 0);
```

---

## Task 14: 更新前端 API 模块

**Files:**
- Modify: `river-ui-admin/src/api/river/stats/index.ts`

**Step 1: 重写 API 模块**

```typescript
import request from '@/config/axios'

// ==================== 日报统计 ====================

export interface DailyStatsVO {
  id: number
  date: string
  dimensionType: number
  dimensionId: number
  dimensionName?: string
  clicks: number
  conversions: number
  revenue: number
  cost: number
  profit: number
  epc: number
  cr: number
  roi: number
  createTime?: string
}

export interface DailyStatsPageReqVO {
  pageNo: number
  pageSize: number
  dimensionType?: number
  dimensionId?: number
  startDate?: string
  endDate?: string
}

export interface DailyStatsSummaryVO {
  totalClicks: number
  totalConversions: number
  totalRevenue: number
  totalCost: number
  totalProfit: number
  avgEpc: number
  avgCr: number
  avgRoi: number
}

export interface DailyStatsTrendVO {
  date: string
  clicks: number
  conversions: number
  revenue: number
  cost: number
  profit: number
}

export const DailyStatsApi = {
  // 分页查询
  getPage: async (params: DailyStatsPageReqVO) => {
    return await request.post({ url: '/stats/daily/page', data: params })
  },
  // 获取汇总
  getSummary: async (params: Partial<DailyStatsPageReqVO>) => {
    return await request.get({ url: '/stats/daily/summary', params })
  },
  // 获取趋势
  getTrend: async (params: Partial<DailyStatsPageReqVO>) => {
    return await request.get({ url: '/stats/daily/trend', params })
  },
  // 导出 Excel
  exportExcel: async (params: Partial<DailyStatsPageReqVO>) => {
    return await request.download({ url: '/stats/daily/export-excel', params })
  }
}
```

**Step 2: 验证 TypeScript**

Run: `cd river-ui-admin && pnpm ts:check`
Expected: 无错误

**Step 3: Commit**

```bash
git add river-ui-admin/src/api/river/stats/index.ts
git commit -m "feat(ui): update stats API to use unified endpoints"
```

---

## Task 15: 创建统计报表页面

**Files:**
- Create: `river-ui-admin/src/views/river/stats/index.vue`

**Step 1: 创建页面**

```vue
<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="68px"
    >
      <el-form-item label="维度类型" prop="dimensionType">
        <el-select
          v-model="queryParams.dimensionType"
          placeholder="全部维度"
          clearable
          class="!w-160px"
          @change="handleDimensionTypeChange"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.DIMENSION_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="维度" prop="dimensionId" v-if="queryParams.dimensionType">
        <el-select
          v-model="queryParams.dimensionId"
          placeholder="全部"
          clearable
          filterable
          class="!w-200px"
        >
          <el-option
            v-for="item in dimensionOptions"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="日期范围" prop="date">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery">
          <Icon icon="ep:search" class="mr-5px" /> 搜索
        </el-button>
        <el-button @click="resetQuery">
          <Icon icon="ep:refresh" class="mr-5px" /> 重置
        </el-button>
        <el-button type="success" plain @click="handleExport" :loading="exportLoading">
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 统计卡片 -->
  <ContentWrap>
    <el-row :gutter="16">
      <el-col :xs="24" :sm="12" :md="4" :lg="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #ecf5ff">
              <Icon icon="ep:mouse" color="#409eff" :size="24" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ formatNumber(summary.totalClicks) }}</div>
              <div class="stat-label">点击数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="4" :lg="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f0f9ff">
              <Icon icon="ep:check" color="#67c23a" :size="24" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ formatNumber(summary.totalConversions) }}</div>
              <div class="stat-label">转化数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="4" :lg="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #fdf6ec">
              <Icon icon="ep:coin" color="#e6a23c" :size="24" />
            </div>
            <div class="stat-info">
              <div class="stat-value text-warning">${{ formatMoney(summary.totalRevenue) }}</div>
              <div class="stat-label">收入</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="4" :lg="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #fef0f0">
              <Icon icon="ep:money" color="#f56c6c" :size="24" />
            </div>
            <div class="stat-info">
              <div class="stat-value text-danger">${{ formatMoney(summary.totalCost) }}</div>
              <div class="stat-label">成本</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="4" :lg="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #e1f3d8">
              <Icon icon="ep:trend-charts" color="#67c23a" :size="24" />
            </div>
            <div class="stat-info">
              <div class="stat-value" :class="summary.totalProfit >= 0 ? 'text-success' : 'text-danger'">
                ${{ formatMoney(summary.totalProfit) }}
              </div>
              <div class="stat-label">利润</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </ContentWrap>

  <!-- 趋势图表 -->
  <ContentWrap>
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>趋势图</span>
        </div>
      </template>
      <div ref="chartRef" style="height: 300px"></div>
    </el-card>
  </ContentWrap>

  <!-- 数据表格 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="日期" prop="date" width="120">
        <template #default="scope">
          {{ formatDate(scope.row.date) }}
        </template>
      </el-table-column>
      <el-table-column label="维度类型" prop="dimensionType" width="100">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.DIMENSION_TYPE" :value="scope.row.dimensionType" />
        </template>
      </el-table-column>
      <el-table-column label="维度ID" prop="dimensionId" width="100" />
      <el-table-column label="点击数" prop="clicks" width="100" align="right">
        <template #default="scope">
          <span class="text-primary">{{ formatNumber(scope.row.clicks) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="转化数" prop="conversions" width="100" align="right">
        <template #default="scope">
          <span class="text-success">{{ formatNumber(scope.row.conversions) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="收入" prop="revenue" width="120" align="right">
        <template #default="scope">
          <span class="text-warning">${{ formatMoney(scope.row.revenue) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="成本" prop="cost" width="120" align="right">
        <template #default="scope">
          <span class="text-danger">${{ formatMoney(scope.row.cost) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="利润" prop="profit" width="120" align="right">
        <template #default="scope">
          <span :class="scope.row.profit >= 0 ? 'text-success' : 'text-danger'">
            ${{ formatMoney(scope.row.profit) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="EPC" prop="epc" width="100" align="right">
        <template #default="scope">
          <span class="text-primary">${{ scope.row.epc?.toFixed(4) || '0.0000' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="CR" prop="cr" width="80" align="right">
        <template #default="scope">
          <span class="text-info">{{ scope.row.cr?.toFixed(2) || '0.00' }}%</span>
        </template>
      </el-table-column>
      <el-table-column label="ROI" prop="roi" width="100" align="right">
        <template #default="scope">
          <span :class="scope.row.roi >= 0 ? 'text-success' : 'text-danger'">
            {{ scope.row.roi?.toFixed(2) || '0.00' }}%
          </span>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>
</template>

<script setup lang="ts">
import { DailyStatsApi, DailyStatsSummaryVO, DailyStatsTrendVO } from '@/api/river/stats'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import * as echarts from 'echarts'
import download from '@/utils/download'

defineOptions({ name: 'StatsReport' })

const message = useMessage()

const loading = ref(true)
const total = ref(0)
const list = ref([])
const summary = ref<DailyStatsSummaryVO>({
  totalClicks: 0,
  totalConversions: 0,
  totalRevenue: 0,
  totalCost: 0,
  totalProfit: 0,
  avgEpc: 0,
  avgCr: 0,
  avgRoi: 0
})
const trendData = ref<DailyStatsTrendVO[]>([])
const dimensionOptions = ref<{ id: number; name: string }[]>([])
const chartRef = ref<HTMLDivElement>()
const exportLoading = ref(false)
const dateRange = ref<[string, string]>()

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  dimensionType: undefined as number | undefined,
  dimensionId: undefined as number | undefined,
  startDate: undefined as string | undefined,
  endDate: undefined as string | undefined
})
const queryFormRef = ref()

let chartInstance: echarts.ECharts | null = null

// 格式化数字
const formatNumber = (num: number) => {
  return num?.toLocaleString() || '0'
}

// 格式化金额
const formatMoney = (amount: number) => {
  return amount?.toFixed(2) || '0.00'
}

// 格式化日期
const formatDate = (date: string) => {
  if (!date) return '-'
  return date.substring(0, 10)
}

// 维度类型变更
const handleDimensionTypeChange = async () => {
  queryParams.dimensionId = undefined
  dimensionOptions.value = []

  if (!queryParams.dimensionType) return

  // TODO: 根据维度类型加载对应选项
  // 这里需要调用对应的 simple-list API
}

// 初始化图表
const initChart = () => {
  if (!chartRef.value) return
  chartInstance = echarts.init(chartRef.value)
  window.addEventListener('resize', () => chartInstance?.resize())
}

// 更新图表
const updateChart = () => {
  if (!chartInstance || !trendData.value.length) {
    chartInstance?.clear()
    return
  }

  const dates = trendData.value.map(item => item.date)
  const clicks = trendData.value.map(item => item.clicks || 0)
  const conversions = trendData.value.map(item => item.conversions || 0)
  const revenue = trendData.value.map(item => item.revenue || 0)
  const cost = trendData.value.map(item => item.cost || 0)

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' }
    },
    legend: {
      data: ['点击数', '转化数', '收入', '成本']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dates,
      boundaryGap: false
    },
    yAxis: [
      {
        type: 'value',
        name: '数量',
        position: 'left'
      },
      {
        type: 'value',
        name: '金额($)',
        position: 'right'
      }
    ],
    series: [
      {
        name: '点击数',
        type: 'line',
        data: clicks,
        smooth: true,
        itemStyle: { color: '#409eff' }
      },
      {
        name: '转化数',
        type: 'line',
        data: conversions,
        smooth: true,
        itemStyle: { color: '#67c23a' }
      },
      {
        name: '收入',
        type: 'bar',
        yAxisIndex: 1,
        data: revenue,
        itemStyle: { color: '#e6a23c' }
      },
      {
        name: '成本',
        type: 'bar',
        yAxisIndex: 1,
        data: cost,
        itemStyle: { color: '#f56c6c' }
      }
    ]
  }

  chartInstance.setOption(option)
}

// 查询列表
const getList = async () => {
  loading.value = true
  try {
    // 处理日期范围
    if (dateRange.value && dateRange.value.length === 2) {
      queryParams.startDate = dateRange.value[0]
      queryParams.endDate = dateRange.value[1]
    } else {
      queryParams.startDate = undefined
      queryParams.endDate = undefined
    }

    // 并行请求
    const [pageData, summaryData, trendDataResult] = await Promise.all([
      DailyStatsApi.getPage(queryParams),
      DailyStatsApi.getSummary(queryParams),
      DailyStatsApi.getTrend(queryParams)
    ])

    list.value = pageData.list
    total.value = pageData.total
    summary.value = summaryData
    trendData.value = trendDataResult

    nextTick(() => updateChart())
  } finally {
    loading.value = false
  }
}

// 搜索
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

// 重置
const resetQuery = () => {
  queryFormRef.value.resetFields()
  dateRange.value = undefined
  queryParams.dimensionType = undefined
  queryParams.dimensionId = undefined
  dimensionOptions.value = []
  handleQuery()
}

// 导出
const handleExport = async () => {
  try {
    await message.exportConfirm()
    exportLoading.value = true
    const data = await DailyStatsApi.exportExcel(queryParams)
    download.excel(data, '统计报表.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

// 初始化
onMounted(() => {
  // 默认查询最近 7 天
  const end = new Date()
  const start = new Date()
  start.setDate(end.getDate() - 7)
  dateRange.value = [
    start.toISOString().substring(0, 10),
    end.toISOString().substring(0, 10)
  ]

  initChart()
  getList()
})

onUnmounted(() => {
  chartInstance?.dispose()
})
</script>

<style scoped>
.stat-card {
  margin-bottom: 16px;
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 20px;
  font-weight: 600;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

.text-primary {
  color: #409eff;
}

.text-success {
  color: #67c23a;
}

.text-warning {
  color: #e6a23c;
}

.text-danger {
  color: #f56c6c;
}

.text-info {
  color: #909399;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
```

**Step 2: 验证 TypeScript**

Run: `cd river-ui-admin && pnpm ts:check`
Expected: 无错误

**Step 3: Commit**

```bash
git add river-ui-admin/src/views/river/stats/index.vue
git commit -m "feat(ui): create unified stats report page"
```

---

## Task 16: 删除旧前端页面

**Files:**
- Delete: `river-ui-admin/src/views/river/stats/daily/`
- Delete: `river-ui-admin/src/views/river/stats/hourly/`

**Step 1: 删除旧页面**

```bash
rm -rf river-ui-admin/src/views/river/stats/daily
rm -rf river-ui-admin/src/views/river/stats/hourly
```

**Step 2: Commit**

```bash
git add -A river-ui-admin/src/views/river/stats/
git commit -m "refactor(ui): remove old daily/hourly stats pages"
```

---

## Task 17: 编译验证

**Step 1: 后端编译**

Run: `cd river-server && mvn clean compile -q`
Expected: BUILD SUCCESS

**Step 2: 前端编译**

Run: `cd river-ui-admin && pnpm build:local`
Expected: BUILD SUCCESS

**Step 3: Commit 所有变更（如有遗漏）**

```bash
git status
# 如有未提交文件，执行提交
```

---

## 实施顺序总结

| 阶段 | 任务 | 说明 |
|------|------|------|
| 后端 VO | Task 2-5 | 创建请求/响应 VO |
| 后端核心 | Task 1, 6-10 | 枚举、转换器、Service、Mapper、Controller |
| 后端清理 | Task 11 | 删除冗余 Controller |
| 数据库 | Task 12-13 | 菜单和字典更新 |
| 前端 | Task 14-16 | API、页面、清理 |
| 验证 | Task 17 | 编译验证 |

**注意**：聚合 Job 实现（StatsAggregationJobImpl）作为单独的后续任务，因为它依赖于 tracking 模块的数据。
