# Stats 模块优化设计

## 概述

优化 river-module-stats 统计模块，合并为统一的统计报表系统，修复前后端 API 不匹配问题，实现数据聚合逻辑，清理无用菜单。

## 需求确认

| 项目 | 决定 |
|------|------|
| 统计维度 | 保留全部（Offer、Campaign、Source、Merchant、Category、LandingPage） |
| 页面结构 | 合并为一个统一的统计报表页 |
| 筛选维度 | 全部维度 |
| 时间粒度 | 仅日统计 |
| 可视化元素 | 汇总卡片 + 趋势图 + 数据表 |
| 成本录入 | 保持现有 `营销活动 > 成本记录` 菜单不变 |

## 架构设计

### 整体架构

```
┌─────────────────────────────────────────────────────────┐
│                    前端 stats/index.vue                  │
├─────────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────┐  │
│  │  汇总卡片   │  │   趋势图    │  │    数据表       │  │
│  │ (5个KPI)   │  │  (ECharts)  │  │  (分页+导出)    │  │
│  └─────────────┘  └─────────────┘  └─────────────────┘  │
└─────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│              DailyStatsController                        │
├─────────────────────────────────────────────────────────┤
│  GET /stats/daily/summary     → 汇总数据                 │
│  GET /stats/daily/trend       → 趋势数据                 │
│  POST /stats/daily/page       → 分页列表                 │
│  GET /stats/daily/export-excel → 导出                    │
└─────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│              DailyStatsService                           │
│              DailyStatsMapper                            │
│              river_stats_daily 表                        │
└─────────────────────────────────────────────────────────┘
```

### 数据流

```
┌─────────────────────────────────────────────────────────┐
│  river_tracking_click          │  点击数据               │
│  river_tracking_conversion     │  转化数据 + 收入        │
│  river_campaign_cost_record    │  成本数据（手动/API）   │
└────────────────┬────────────────────────────────────────┘
                 │ StatsAggregationJob (每日凌晨 1:30)
                 ▼
┌─────────────────────────────────────────────────────────┐
│  river_stats_daily                                       │
│  (date, dimension_type, dimension_id,                   │
│   clicks, conversions, revenue, cost, profit,           │
│   epc, cr, roi)                                         │
└─────────────────────────────────────────────────────────┘
```

## 后端改动

### 1. 扩展 DimensionTypeEnum

文件：`river-module-stats/src/main/java/com/river/module/stats/enums/DimensionTypeEnum.java`

```java
public enum DimensionTypeEnum {
    CAMPAIGN(1, "活动"),
    SOURCE(2, "流量源"),
    OFFER(3, "Offer"),
    LANDING_PAGE(4, "落地页"),
    MERCHANT(5, "商家"),      // 新增
    CATEGORY(6, "分类");      // 新增
}
```

### 2. 新增 DailyStatsController

文件：`river-module-stats/src/main/java/com/river/module/stats/controller/admin/daily/DailyStatsController.java`

API 端点：

| 方法 | 路径 | 权限 | 说明 |
|------|------|------|------|
| POST | `/stats/daily/page` | `stats:daily:query` | 分页查询 |
| GET | `/stats/daily/summary` | `stats:daily:query` | 汇总数据 |
| GET | `/stats/daily/trend` | `stats:daily:query` | 趋势数据 |
| GET | `/stats/daily/export-excel` | `stats:daily:export` | 导出 Excel |

### 3. 新增请求/响应 VO

文件：`controller/admin/daily/vo/`

- `DailyStatsPageReqVO` - 分页请求（dimensionType, dimensionId, startDate, endDate）
- `DailyStatsSummaryRespVO` - 汇总响应（clicks, conversions, revenue, cost, profit）
- `DailyStatsTrendRespVO` - 趋势响应（date, clicks, conversions, revenue）
- `DailyStatsRespVO` - 列表项响应

### 4. 实现 StatsAggregationJobImpl

文件：`river-module-stats/src/main/java/com/river/module/stats/job/StatsAggregationJobImpl.java`

聚合逻辑：

```
1. 确定聚合日期（默认昨天）
2. 按各维度聚合：
   - 从 river_tracking_click 统计点击数
   - 从 river_tracking_conversion 统计转化数、收入
   - 从 river_campaign_cost_record 获取成本
   - 计算 profit = revenue - cost
   - 计算 EPC = revenue / clicks
   - 计算 CR = conversions / clicks * 100
   - 计算 ROI = profit / cost * 100
3. 写入 river_stats_daily（使用 UPSERT）
```

定时任务：`0 30 1 * * ?`（每天凌晨 1:30）

### 5. 删除冗余 Controller

删除以下文件：
- `controller/admin/dashboard/` 目录
- `controller/admin/campaign/` 目录
- `controller/admin/offer/` 目录
- `controller/admin/source/` 目录

## 前端改动

### 1. 新建统计报表页面

文件：`river-ui-admin/src/views/river/stats/index.vue`

页面布局：

```
┌─────────────────────────────────────────────────────────────┐
│ 筛选栏                                                       │
│ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌─────────────────┐  │
│ │ 维度类型  │ │ 维度选择  │ │ 日期范围  │ │ 搜索 │ 重置 │ 导出│  │
│ └──────────┘ └──────────┘ └──────────┘ └─────────────────┘  │
├─────────────────────────────────────────────────────────────┤
│ KPI 卡片                                                     │
│ ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐     │
│ │ 点击数  │ │ 转化数  │ │  收入   │ │  成本   │ │  利润   │     │
│ └────────┘ └────────┘ └────────┘ └────────┘ └────────┘     │
├─────────────────────────────────────────────────────────────┤
│ 趋势图 (ECharts)                                             │
│ ┌─────────────────────────────────────────────────────────┐ │
│ │  📈 点击/转化/收入 趋势折线图                              │ │
│ └─────────────────────────────────────────────────────────┘ │
├─────────────────────────────────────────────────────────────┤
│ 数据表                                                       │
│ ┌─────────────────────────────────────────────────────────┐ │
│ │ 日期 │ 维度 │ 点击 │ 转化 │ 收入 │ 成本 │ 利润 │ EPC │ CR │ ROI │ │
│ └─────────────────────────────────────────────────────────┘ │
│ 分页                                                         │
└─────────────────────────────────────────────────────────────┘
```

维度选择器联动：

| 维度类型 | 数据来源 |
|----------|----------|
| Campaign | `/campaign/campaign/simple-list` |
| Source | `/campaign/traffic-source/simple-list` |
| Offer | `/affiliate/offer/simple-list` |
| Merchant | `/affiliate/merchant/simple-list` |
| Category | `/affiliate/category/simple-list` |

### 2. 更新 Stats API 模块

文件：`river-ui-admin/src/api/river/stats/index.ts`

```typescript
export const DailyStatsApi = {
  getPage: (params: DailyStatsPageReqVO) =>
    request.post({ url: '/stats/daily/page', data: params }),
  getSummary: (params: DailyStatsQueryVO) =>
    request.get({ url: '/stats/daily/summary', params }),
  getTrend: (params: DailyStatsQueryVO) =>
    request.get({ url: '/stats/daily/trend', params }),
  exportExcel: (params: DailyStatsQueryVO) =>
    request.download({ url: '/stats/daily/export-excel', params })
}
```

### 3. 删除旧页面

删除以下目录：
- `views/river/stats/daily/`
- `views/river/stats/hourly/`

## 数据库改动

### 1. 删除旧菜单

```sql
-- 删除 stats 下的旧子菜单
DELETE FROM system_menu WHERE id IN (20191, 20192, 20193, 20194, 20195);
```

### 2. 新增统计报表菜单

```sql
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, status, creator, create_time, updater, update_time, deleted)
VALUES (20196, '统计报表', 'stats:daily:query', 2, 1, 20190, 'report', 'ep:data-analysis', 'river/stats/index', 0, '1', NOW(), '1', NOW(), 0);
```

### 3. 更新字典 DIMENSION_TYPE

```sql
-- 新增 MERCHANT 和 CATEGORY 字典项
INSERT INTO system_dict_data (sort, label, value, dict_type, status, creator, create_time, updater, update_time, deleted)
VALUES
(5, '商家', '5', 'dimension_type', 0, '1', NOW(), '1', NOW(), 0),
(6, '分类', '6', 'dimension_type', 0, '1', NOW(), '1', NOW(), 0);
```

## 实施任务清单

### 后端任务

| # | 任务 | 文件 |
|---|------|------|
| 1 | 扩展 DimensionTypeEnum | `enums/DimensionTypeEnum.java` |
| 2 | 新增 DailyStatsController | `controller/admin/daily/DailyStatsController.java` |
| 3 | 新增请求/响应 VO | `controller/admin/daily/vo/` |
| 4 | 扩展 DailyStatsService | `service/DailyStatsService.java` |
| 5 | 扩展 DailyStatsServiceImpl | `service/DailyStatsServiceImpl.java` |
| 6 | 实现 StatsAggregationJobImpl | `job/StatsAggregationJobImpl.java` |
| 7 | 删除冗余 Controller | `controller/admin/dashboard/campaign/offer/source/` |

### 前端任务

| # | 任务 | 文件 |
|---|------|------|
| 8 | 新建统计报表页面 | `views/river/stats/index.vue` |
| 9 | 更新 Stats API 模块 | `api/river/stats/index.ts` |
| 10 | 删除旧页面 | `views/river/stats/daily/`、`hourly/` |

### 数据库任务

| # | 任务 |
|---|------|
| 11 | 删除旧菜单（20191-20195） |
| 12 | 新增统计报表菜单（20196） |
| 13 | 更新字典 DIMENSION_TYPE |

## 注意事项

1. 成本录入入口保持不变：`营销活动 > 成本记录`
2. 定时任务框架已支持，只需实现聚合逻辑
3. 开发环境 PostgreSQL 为本地 Docker 安装
