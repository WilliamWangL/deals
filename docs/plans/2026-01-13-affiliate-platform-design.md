# River 广告联盟平台设计文档

> 创建日期: 2026-01-13  
> 状态: 已确认

## 1. 项目概述

### 1.1 商业模式

**Media Buying / 联盟套利模式：**

```
广告投放成本 (Google Ads, X 等) < 联盟佣金收入 = 利润
```

### 1.2 平台定位

- **第一阶段**：CPS 导购站（MVP）
- **后续扩展**：流量主、联盟聚合 SaaS、自建广告网络

### 1.3 核心域名

- `deals.ecommica.com` - 单域名，通过路径区分功能

---

## 2. 系统架构

```
┌─────────────────────────────────────────────────────────────┐
│                    river-ecommica (Next.js 15)              │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │   博客/SEO   │  │ Landing Page│  │   优惠/Deal 聚合    │  │
│  │  /blog/*    │  │  /lp/*      │  │   /deals, /stores   │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└──────────────────────────┬──────────────────────────────────┘
                           │ API
┌──────────────────────────▼──────────────────────────────────┐
│                    river-server (Java)                       │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌─────────────┐  │
│  │ 联盟管理  │  │ Offer管理 │  │ 点击追踪  │  │ 数据统计    │  │
│  └──────────┘  └──────────┘  └──────────┘  └─────────────┘  │
└──────────────────────────┬──────────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────────┐
│                  river-ui-admin (Vue 3)                      │
│         广告联盟配置 / Offer管理 / 数据看板 / 博客CMS         │
└─────────────────────────────────────────────────────────────┘
```

---

## 3. URL 结构

| 路径 | 功能 | 说明 |
|------|------|------|
| `/` | 首页 | 精选 Deals + 热门分类 |
| `/deals` | 优惠列表 | 筛选、排序、搜索 |
| `/deals/[slug]` | 优惠详情 | 单品页，SEO 优化 |
| `/stores` | 商家列表 | 按联盟/品牌分类 |
| `/stores/[slug]` | 商家详情 | 该商家所有优惠 |
| `/categories/[slug]` | 分类页 | 按品类聚合 |
| `/blog` | 博客首页 | 文章列表 |
| `/blog/[slug]` | 博客文章 | MDX 渲染 + 嵌入 Deal 组件 |
| `/lp/[slug]` | Landing Page | 付费广告落地页 |
| `/go/[id]` | 跳转追踪 | 301 重定向 + 点击记录 |

---

## 4. 后端模块划分

```
river-module-affiliate/     # 广告联盟核心
├── network/               # 联盟管理
├── offer/                 # Offer 管理
├── tracking/              # 点击追踪
└── stats/                 # 数据统计

river-module-blog/          # 博客系统（独立）
├── article/               # 文章 CRUD
├── category/              # 博客分类
├── tag/                   # 标签系统
├── comment/               # 评论（可选，后期）
└── seo/                   # SEO 元数据、Sitemap

river-module-marketing/     # 营销相关
├── landing/               # Landing Page 管理
├── campaign/              # 推广活动
└── analytics/             # 流量分析、UTM 追踪
```

---

## 5. 数据模型

### 5.1 affiliate 模块（已设计）

```sql
river_affiliate_network     -- 广告联盟配置
river_affiliate_campaign    -- 推广活动
river_affiliate_category    -- Offer 分类
river_affiliate_offer       -- Offer/Deal
river_affiliate_click_event -- 点击事件
river_affiliate_conversion  -- 转化记录
river_affiliate_payout      -- 佣金结算
```

### 5.2 blog 模块（新增）

```sql
river_blog_article          -- 文章
  - title, slug, content, excerpt
  - cover_image, author_id, status
  - seo_title, seo_description, seo_keywords
  - published_at, view_count

river_blog_category         -- 博客分类
river_blog_tag              -- 标签
river_blog_article_tag      -- 文章-标签关联
```

### 5.3 marketing 模块（新增）

```sql
river_landing_page          -- Landing Page
  - title, slug, template_type
  - content_json (页面配置)
  - offer_ids (关联的 Offer)
  - utm_source, utm_campaign

river_traffic_source        -- 流量来源追踪
  - source (google, twitter, facebook...)
  - cost, clicks, conversions
  - date (按天聚合)
```

---

## 6. 联盟对接优先级

| 优先级 | 联盟 | 原因 |
|--------|------|------|
| P0 | Custom（自建） | 立即可用，验证流程 |
| P0 | Admitad | 主力联盟 |
| P1 | Awin | 海外主流，品牌多 |
| P1 | CJ Affiliate | 北美大牌多 |
| P2 | 其他 | 按需扩展 |

---

## 7. 技术实现要点

### 7.1 博客系统

| 点 | 方案 |
|---|------|
| 富文本存储 | HTML 存数据库，前端 `dangerouslySetInnerHTML` 渲染 |
| Deal 嵌入 | 特殊标记 `<!-- deal:123 -->`，前端解析替换为组件 |
| 图片存储 | 复用 ruoyi 的 OSS 模块 |
| SEO | Next.js Metadata API + JSON-LD |

### 7.2 前端渲染策略

| 页面 | 策略 |
|------|------|
| `/deals` 列表 | SSG + ISR (revalidate: 3600) |
| `/deals/[slug]` | SSG + ISR |
| `/blog` | SSG + ISR |
| `/blog/[slug]` | SSG + ISR |
| `/go/[id]` | Server-side (动态追踪) |

---

## 8. 分阶段实施计划

### Phase 1 - 核心变现闭环（2-3 周）

| 模块 | 功能 | 优先级 |
|------|------|--------|
| 联盟对接 | Admitad API + Custom 自建 | P0 |
| Offer 管理 | CRUD + 审核上下架 | P0 |
| 点击追踪 | `/go/[id]` 跳转 + 事件记录 | P0 |
| 前端展示 | Deals 列表 + 详情页 | P0 |
| Admin 后台 | 联盟配置 + Offer 管理 | P0 |

**交付物：** 能跑通「上架 Offer → 展示 → 点击跳转 → 记录数据」完整链路

### Phase 2 - 博客引流（1-2 周）

| 模块 | 功能 | 优先级 |
|------|------|--------|
| 博客后端 | 文章 CRUD + 分类标签 | P1 |
| 博客 Admin | 富文本编辑 + Offer 嵌入 | P1 |
| 博客前端 | /blog 列表 + 详情 + SEO | P1 |
| Sitemap | 自动生成 XML Sitemap | P1 |

### Phase 3 - 付费投放支持（1-2 周）

| 模块 | 功能 | 优先级 |
|------|------|--------|
| Landing Page | 模板系统 + 可视化配置 | P2 |
| 流量追踪 | UTM 解析 + 来源统计 | P2 |
| ROI 看板 | 广告成本 vs 佣金收入 | P2 |

### Phase 4 - 扩展联盟（持续）

| 联盟 | 优先级 |
|------|--------|
| Awin | P3 |
| CJ Affiliate | P3 |
| ShareASale | P4 |

---

## 9. 下一步行动

1. 创建 Git 工作分支
2. 编写详细实施计划（writing-plans）
3. 按 Phase 1 开始实现
