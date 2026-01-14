# Phase 5: 数据统计与仪表盘实施计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 实现 river-module-stats 模块，提供多维度数据聚合、实时报表和管理后台仪表盘。

**Architecture:**
- 新建 Maven 模块 river-module-stats
- 聚合 affiliate (收入) 和 campaign (成本) 数据
- 使用定时任务进行每日/每小时数据预聚合
- 实体：DailyStats, HourlyStats

**Tech Stack:** Java 17, Spring Boot 3.5, MyBatis-Plus, Quartz (Scheduled Tasks), ECharts (Frontend)

---

## Task 1: Maven 模块与依赖

**Files:**
- Create: `river-server/river-module-stats/pom.xml`
- Modify: `river-server/pom.xml`

**Dependencies:**
- `river-module-affiliate-api`: 获取 Offer/Conversion 数据
- `river-module-campaign-api`: 获取 Campaign/Cost 数据

---

## Task 2: 统计实体 (Stats Entities)

**Files:**
- Create: `river-module-stats-api/.../enums/DimensionTypeEnum.java`
- Create: `river-module-stats-biz/.../dal/dataobject/DailyStatsDO.java`
- Create: `river-module-stats-biz/.../dal/dataobject/HourlyStatsDO.java`

**Enums:**
- **DimensionTypeEnum**: OFFER(1), CAMPAIGN(2), TRAFFIC_SOURCE(3), AFFILIATE_NETWORK(4), COUNTRY(5)

**Entities:**
1. **DailyStatsDO**:
   - `date`: 日期
   - `dimension_type`: 维度类型
   - `dimension_id`: 维度 ID
   - `impressions`, `clicks`, `conversions`
   - `revenue`, `cost`, `profit`
   - `currency`: 统一统计货币 (如 USD)

2. **HourlyStatsDO**:
   - 包含 `hour` (0-23) 字段，其他同 DailyStats

---

## Task 3: 数据库脚本

**Files:**
- Create: `river-server/sql/postgresql/stats/river_stats.sql`

**Content:**
- `river_stats_daily`
- `river_stats_hourly`
- 复合索引：`idx_date_dim` (date, dimension_type, dimension_id)

---

## Task 4: 数据聚合 Job (Aggregation Job)

**Files:**
- Create: `river-module-stats-biz/.../job/DailyStatsAggregationJob.java`
- Create: `river-module-stats-biz/.../job/HourlyStatsAggregationJob.java`

**Logic:**
1. **Extract**: 从 Campaign 模块拉取 Cost，从 Affiliate 模块拉取 Revenue/Conversions
2. **Transform**: 按维度 (Offer, Campaign, etc.) Group By，统一货币
3. **Load**: 存入/更新 Stats 表

---

## Task 5: 仪表盘接口 (Dashboard Controller)

**Files:**
- Create: `river-module-stats-biz/.../controller/admin/DashboardController.java`
- Create: `river-module-stats-biz/.../controller/admin/vo/DashboardTrendRespVO.java`

**Endpoints:**
- `GET /admin-api/stats/dashboard/summary`: 获取今日核心指标 (Rev, Cost, Profit, ROI)
- `GET /admin-api/stats/dashboard/trend`: 获取趋势图数据 (折线图)
- `GET /admin-api/stats/dashboard/top-offers`: 获取 Top 10 Offer

---

## Task 6: 详细报表接口 (Report Controllers)

**Files:**
- Create: `river-module-stats-biz/.../controller/admin/ReportController.java`

**Features:**
- 支持按时间范围查询 (Yesterday, Last 7 Days, This Month)
- 支持多维度 Drill-down (例如：点击某个 Campaign 查看其下 Offer 表现)
- 导出 CSV 功能

---

## Verification

**Build:**
```bash
mvn compile -pl river-module-stats -am
```

**Test:**
- 模拟写入 Conversion 和 Cost 数据
- 触发 Aggregation Job
- 验证 DailyStats 表数据准确性
- 验证 API 返回 JSON 格式符合 ECharts 要求
