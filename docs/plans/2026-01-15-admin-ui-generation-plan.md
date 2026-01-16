# 管理后台前端生成计划

## 概述

本计划用于生成 River 广告平台管理后台的所有前端页面。后端 CRUD 已完成，仅需生成前端管理界面。

## 生成原则

1. **不覆盖后端代码** - 后端已有完整 CRUD 实现
2. **参考现有模板** - 使用 `river-module-infra/codegen/vue3/` 下的 Velocity 模板
3. **遵循现有模式** - 参考 `river-ui-admin/src/views/river/deal/` 的实现风格

## 模块清单

### 1. Affiliate 模块 (6张表)

| 表名 | 页面路径 | 说明 |
|------|----------|------|
| river_affiliate_network | `river/affiliate/network/` | 联盟网络管理 |
| river_affiliate_network_credential | `river/affiliate/credential/` | 联盟凭证管理 |
| river_affiliate_postback_secret | `river/affiliate/postback-secret/` | Postback密钥管理 |
| river_affiliate_merchant | `river/affiliate/merchant/` | 商家管理 |
| river_affiliate_category | `river/affiliate/category/` | 分类管理 |
| river_affiliate_offer | `river/affiliate/offer/` | Offer管理 |

### 2. Coupon 模块 (2张表)

| 表名 | 页面路径 | 说明 |
|------|----------|------|
| river_coupon | `river/coupon/coupon/` | 优惠券管理 |
| river_deal | `river/coupon/deal/` | Deal管理 |

### 3. Blog 模块 (4张表)

| 表名 | 页面路径 | 说明 |
|------|----------|------|
| river_blog_post | `river/blog/post/` | 博客文章管理 |
| river_blog_author | `river/blog/author/` | 作者管理 |
| river_blog_tag | `river/blog/tag/` | 标签管理 |
| river_blog_category | `river/blog/category/` | 博客分类管理 |

### 4. Campaign 模块 (7张表)

| 表名 | 页面路径 | 说明 |
|------|----------|------|
| river_traffic_source | `river/campaign/traffic-source/` | 流量源管理 |
| river_campaign | `river/campaign/campaign/` | 营销活动管理 |
| river_landing_page | `river/campaign/landing-page/` | 落地页管理 |
| river_ad_creative | `river/campaign/ad-creative/` | 广告创意管理 |
| river_ad_group | `river/campaign/ad-group/` | 广告组管理 |
| river_cost_record | `river/campaign/cost-record/` | 成本记录管理 |
| river_conversion_goal | `river/campaign/conversion-goal/` | 转化目标管理 |

### 5. Tracking 模块 (6张表 + 1张新增)

| 表名 | 页面路径 | 说明 |
|------|----------|------|
| river_click_log | `river/tracking/click-log/` | 点击日志（只读） |
| river_conversion_log | `river/tracking/conversion-log/` | 转化日志（只读） |
| river_attribution_record | `river/tracking/attribution/` | 归因记录 |
| river_traffic_source_rule | `river/tracking/traffic-rule/` | 流量识别规则（新增） |
| river_postback_log | `river/tracking/postback-log/` | Postback日志 |
| river_tracking_link | `river/tracking/tracking-link/` | 跟踪链接管理 |

### 6. Stats 模块 (2张表)

| 表名 | 页面路径 | 说明 |
|------|----------|------|
| river_daily_stats | `river/stats/daily-stats/` | 日统计报表（只读） |
| river_hourly_stats | `river/stats/hourly-stats/` | 小时统计报表（只读） |

## 每个模块生成内容

### 前端文件结构

```
src/views/river/{module}/
├── index.vue              # 列表页
└── {Entity}Form.vue       # 表单组件

src/api/river/{module}/
└── index.ts               # API 调用
```

### 列表页 (index.vue) 功能

- 搜索表单（条件筛选）
- 数据表格（分页）
- 操作按钮（新增、编辑、删除）
- 状态标签（status 字段）
- 时间格式化

### 表单页 (xxxForm.vue) 功能

- 表单验证
- 字段类型映射
- 枚举下拉选择
- 日期时间选择器
- 图片上传（logo_url 等）

### API 文件 (index.ts)

- 列表查询 `get{Entity}Page`
- 详情查询 `get{Entity}`
- 创建 `create{Entity}`
- 更新 `update{Entity}`
- 删除 `delete{Entity}`
- 导出 `export{Entity}`

## 字段类型映射

| SQL 类型 | Vue 组件 |
|----------|----------|
| varchar/int2/int4/int8 | `el-input` |
| text | `el-input type="textarea"` |
| timestamp | `el-date-picker` |
| decimal | `el-input-number` |
| boolean | `el-switch` |

## 枚举处理

所有 `status`、`type` 等枚举字段使用 `DictTypeEnum` 或本地枚举：

```typescript
// 从字典获取
const statusDictOptions = useDict('river_common_status')

// 或本地枚举
const CommissionType = {
  CPA: 1,
  CPC: 2,
  CPS: 3,
  CPL: 4
}
```

## 实施顺序

1. **Affiliate 模块** - 核心基础数据
2. **Coupon 模块** - 依赖 Affiliate
3. **Campaign 模块** - 流量管理
4. **Tracking 模块** - 数据追踪
5. **Stats 模块** - 报表展示
6. **Blog 模块** - 内容管理

## 特殊处理

### 只读表

- `river_click_log` - 仅列表展示，无新增编辑
- `river_conversion_log` - 仅列表展示
- `river_daily_stats` - 统计报表，带图表
- `river_hourly_stats` - 统计报表，带图表

### 关联字段

- Merchant → Network（下拉选择）
- Offer → Merchant（下拉选择）
- BlogPost → Author, Category（多选）
- Deal → Merchant, Category（多选）

### 图片字段

- logo_url, image_url 等使用 `UploadImg` 组件

## 验收标准

1. 列表页正常展示数据
2. 表单验证正确
3. 新增/编辑/删除功能正常
4. 搜索筛选有效
5. 分页功能正常
6. 导出功能可用
7. TypeScript 类型检查通过
