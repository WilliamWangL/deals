# River 广告平台系统设计

> 创建日期：2026-01-13  
> 状态：✅ 已完成归档（后端模块 Phase 1-4,6 完成，Phase 5 前台待开发）  
> 归档日期：2026-01-14  
> 审核：Oracle (7/10)

## 1. 项目概述

### 1.1 商业模式

River 广告平台是一个综合性流量变现系统，包含：

- **广告联盟管理**：对接多家联盟（Admitad、CJ、Awin、ShareASale），统一管理 Offer
- **优惠聚合站点**：面向用户的 SEO 优化站点，展示商家、优惠券、Deal
- **流量套利**：在 Google/Facebook 买量，推广高佣金 Offer，赚取差价
- **博客引流**：AI 生成内容，吸引 SEO 流量，植入联盟链接变现

### 1.2 目标市场

- 地区：海外（美国、欧洲为主）
- 模式：Media Buying + Affiliate Marketing
- 核心指标：ROI = (联盟佣金 - 广告花费) / 广告花费

### 1.3 预期量级

| 阶段 | 点击量 | 架构要求 |
|------|--------|---------|
| 初期 | < 100 RPS | PostgreSQL 直写，按月分区 |
| 目标 | 100-1k RPS | 异步聚合，预留队列接口 |

## 2. 技术架构

### 2.1 技术栈

| 层级 | 技术选型 |
|------|---------|
| 后端 | Java 17 + Spring Boot 3.5 (ruoyi-vue-pro 精简版) |
| 管理后台 | Vue 3 + Element Plus |
| 前台站点 | Next.js 15 + React 18 + TailwindCSS + shadcn/ui |
| 数据库 | PostgreSQL 17 (Supabase) |
| 国际化 | next-intl |

### 2.2 系统架构图

```
┌─────────────────────────────────────────────────────────────────┐
│                     river-ecommica (Next.js 15)                 │
│         SEO 优化前台：商家、Deal、优惠券、博客展示                 │
│                SSG + ISR | next-intl 国际化                      │
└─────────────────────────────┬───────────────────────────────────┘
                              │ /app-api/
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                     river-server (Spring Boot 3.5)              │
├─────────────┬─────────────┬─────────────┬─────────────┬─────────┤
│  affiliate  │   coupon    │  tracking   │  campaign   │  blog   │
│  联盟管理    │  优惠券管理  │  流量追踪    │  套利管理    │  博客   │
├─────────────┴─────────────┴─────────────┴─────────────┴─────────┤
│                          stats (数据统计)                        │
├─────────────────────────────────────────────────────────────────┤
│                    system + infra (基础框架)                     │
└─────────────────────────────┬───────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    PostgreSQL (Supabase)                        │
│            river_* 业务表 | 月分区(click) | 多租户               │
└─────────────────────────────────────────────────────────────────┘
```

### 2.3 模块依赖关系

```
affiliate ◄─────────────────┐
    │                       │
    ▼                       │
 coupon ──────► tracking ◄──┤
    │              │        │
    ▼              ▼        │
  blog ──────► campaign ────┘
                   │
                   ▼
               stats
```

## 3. 模块设计

### 3.1 联盟管理模块 (river-module-affiliate)

**职责**：统一管理多个联盟网络，同步 Offer 数据，标准化存储。

#### 核心实体

| 实体 | 说明 | 主要字段 |
|------|------|---------|
| AffiliateNetwork | 联盟网络 | code, name, type(CPS/CPA), api_base_url, status |
| NetworkCredential | API 凭证 | network_id, auth_type(OAuth2/Bearer/ApiKey), credentials(加密), expires_at |
| PostbackSecret | 回调验证 | network_id, secret_key, allowed_ips, algorithm(HMAC-SHA256) |
| Merchant | 商家/广告主 | network_id, external_id, name, logo_url, domain, description, rating |
| Offer | 广告/Offer | merchant_id, external_id, name, description, commission_type, commission_value, cookie_days, tracking_url_template, status, regions, categories |
| Category | 分类 | parent_id, name, slug, level |

#### 联盟 API 兼容设计

| 联盟 | 认证方式 | Offer 接口 | 追踪参数 | 限流 |
|------|---------|-----------|---------|------|
| Admitad | OAuth2 (7天token) | GET /advcampaigns/ | subid | 600/分钟 |
| CJ | Bearer Token | GraphQL | sid | 有限制 |
| Awin | OAuth2 Bearer | POST /promotions | awc/clickRef | 20/分钟 |
| ShareASale | 已并入 Awin | - | - | - |

#### 统一数据模型

```java
// 标准化 Offer 结构
public class NormalizedOffer {
    String networkCode;        // 联盟标识
    String externalId;         // 联盟侧 ID
    String name;
    String description;
    CommissionType commissionType; // CPS / CPA / CPC
    BigDecimal commissionValue;    // 金额或比例
    Integer cookieDays;
    String trackingUrlTemplate;    // 含 {click_id}, {sub1} 等占位符
    List<String> regions;          // 支持地区
    List<String> categories;       // 分类标签
    OfferStatus status;            // ACTIVE / PAUSED / ENDED
}
```

### 3.2 优惠券模块 (river-module-coupon)

**职责**：管理优惠券、折扣码、限时 Deal，支持前台展示和 SEO。

#### 核心实体

| 实体 | 说明 | 主要字段 |
|------|------|---------|
| Coupon | 优惠券 | merchant_id, offer_id, code, discount_type(PERCENT/FIXED/FREE_SHIPPING), discount_value, min_purchase, start_time, end_time, terms, source(NETWORK_SYNC/MANUAL/USER_SUBMIT), verified, hot_score |
| Deal | 限时优惠 | merchant_id, offer_id, title, description, original_price, deal_price, discount_percent, start_time, end_time, stock_limit, image_url, hot_score |

#### 关键能力

- 自动过期处理（定时任务扫描）
- 热度排序（点击量、转化率、编辑推荐）
- 前台分类筛选：按商家、品类、折扣力度
- SEO 友好：每个 Deal/Coupon 独立页面 + 结构化数据

### 3.3 流量追踪模块 (river-module-tracking)

**职责**：记录所有点击和转化，支持 Sub ID 级多维归因分析。

#### 核心实体

| 实体 | 说明 | 主要字段 |
|------|------|---------|
| Click | 点击事件（月分区） | **click_id(ULID)**, offer_id, campaign_id, landing_page_id, sub1-sub5, ip, user_agent, referer, device_type, country, created_at |
| Conversion | 转化事件 | click_id, network_code, external_conversion_id, conversion_type(LEAD/SALE), commission, currency, status(PENDING/APPROVED/REJECTED/REVERSED), network_payload, created_at, updated_at |
| Attribution | 归因结果 | conversion_id, click_id, attribution_type(LAST_CLICK/FIRST_CLICK), confidence_score |
| UnattributedConversion | 未归因转化 | network_code, external_conversion_id, raw_payload, reason, created_at |
| TrackingLink | 追踪链接 | offer_id, slug, preset_sub1-sub5, utm_params, short_url |

#### Sub ID 规范

| 槽位 | 用途 | 示例 |
|------|------|------|
| sub1 | 流量源 | google / facebook / seo |
| sub2 | Campaign ID | camp_123 |
| sub3 | AdGroup ID | adg_456 |
| sub4 | Ad ID / 关键词 | kw_shoes |
| sub5 | 落地页 ID | lp_789 |

#### 关键约束（Oracle 审核要求）

| 约束 | 实现方式 |
|------|---------|
| **click_id 归因** | 服务端生成 ULID，透传联盟，Postback 回传匹配 |
| **Postback 幂等** | UK: (tenant_id, network_code, external_conversion_id) |
| **Postback 鉴权** | HMAC 签名验证 + 可选 IP 白名单 |
| **Redirect 安全** | 只允许已登记 URL，参数签名防篡改 |
| **转化状态机** | Pending → Approved / Rejected → Reversed |

#### 点击重定向流程

```
GET /api/go/{tracking_link_id}?sub1=google&sub2=camp_123
    │
    ├─1. 验证 tracking_link_id 存在
    ├─2. 生成 click_id (ULID)
    ├─3. 异步写入 Click 表
    ├─4. 组装联盟链接（替换 {click_id}, {sub1} 等）
    └─5. 302 重定向
```

#### Postback 接收流程

```
GET /api/postback/{network_code}?click_id=xxx&amount=50&currency=USD&sig=xxx
    │
    ├─1. 验证签名 (HMAC)
    ├─2. 验证 IP 白名单（可选）
    ├─3. 检查幂等（external_conversion_id）
    ├─4. 查找 Click 记录
    │     ├─ 找到 → 创建 Conversion + Attribution
    │     └─ 未找到 → 写入 UnattributedConversion
    └─5. 返回 200 OK
```

### 3.4 Campaign 管理模块 (river-module-campaign)

**职责**：管理付费流量投放，追踪成本与收益，计算 ROI。

#### 核心实体

| 实体 | 说明 | 主要字段 |
|------|------|---------|
| TrafficSource | 流量源配置 | code(google/facebook/native), name, api_credentials(预留), status |
| Campaign | 广告系列 | traffic_source_id, name, type(ARBITRAGE/ORGANIC), offer_ids, landing_page_id, budget_daily, budget_total, status, external_campaign_id |
| AdGroup | 广告组 | campaign_id, name, targeting, bid_strategy, external_adgroup_id |
| LandingPage | 落地页 | name, slug, type(INTERNAL/EXTERNAL), url, offer_id, content(内置类型), status |
| CostRecord | 成本记录 | campaign_id, adgroup_id, date, impressions, clicks, cost, currency, source(MANUAL/API_SYNC) |
| Currency | 货币 | code(USD/EUR), name, symbol |
| FXRate | 汇率 | from_currency, to_currency, rate, date |

#### ROI 计算

```sql
SELECT 
    c.id,
    c.name,
    COALESCE(SUM(cost.cost), 0) as total_cost,
    COALESCE(SUM(conv.commission), 0) as total_revenue,
    COALESCE(SUM(conv.commission), 0) - COALESCE(SUM(cost.cost), 0) as profit,
    CASE 
        WHEN SUM(cost.cost) > 0 
        THEN (SUM(conv.commission) - SUM(cost.cost)) / SUM(cost.cost) * 100
        ELSE 0 
    END as roi_percent
FROM river_campaign c
LEFT JOIN river_cost_record cost ON cost.campaign_id = c.id
LEFT JOIN river_click click ON click.campaign_id = c.id
LEFT JOIN river_conversion conv ON conv.click_id = click.click_id
WHERE c.type = 'ARBITRAGE'
GROUP BY c.id, c.name;
```

#### 核心指标

| 指标 | 公式 | 说明 |
|------|------|------|
| EPC | 收入 / 点击数 | 每次点击收益 |
| CR | 转化数 / 点击数 | 转化率 |
| ROI | (收入 - 成本) / 成本 × 100% | 投资回报率 |
| ROAS | 收入 / 成本 | 广告支出回报 |

### 3.5 博客内容模块 (river-module-blog)

**职责**：管理 SEO 内容，支持多种内容类型，驱动自然流量。

#### 核心实体

| 实体 | 说明 | 主要字段 |
|------|------|---------|
| Author | 作者 | name, slug, avatar_url, bio |
| Post | 文章 | author_id, title, slug, content(Markdown), excerpt, cover_image, type(DEAL/REVIEW/TUTORIAL/NEWS), status(DRAFT/PENDING/PUBLISHED/ARCHIVED), published_at, meta_title, meta_description, canonical_url |
| Tag | 标签 | name, slug |
| PostTag | 文章标签关联 | post_id, tag_id |
| PostOffer | 文章-Offer 关联 | post_id, offer_id, anchor_text, position |

#### SEO 能力

- **Meta 管理**：Title、Description、Canonical、OG 标签独立配置
- **结构化数据**：Article、Product、Offer 等 Schema.org 标记
- **自动内链**：关键词自动链接到相关 Deal/商家页
- **Sitemap 生成**：自动生成并更新

#### 内容工作流（预留）

```
AI 生成草稿 → 人工审核 → 排期发布 → 自动 SEO 优化
```

### 3.6 数据统计模块 (river-module-stats)

**职责**：汇总各维度数据，提供 ROI 分析和决策支持。

#### 核心实体

| 实体 | 说明 | 主要字段 |
|------|------|---------|
| DailyStats | 日报聚合 | date, dimension_type(OFFER/CAMPAIGN/SOURCE/MERCHANT), dimension_id, clicks, conversions, revenue, cost, profit |

#### 统计维度

- 按时间：小时/日/周/月
- 按来源：流量渠道 × Campaign × Ad Group × Ad
- 按目标：Offer × Merchant × Category
- 组合分析：来源 × 目标 × 时间

#### 异步聚合策略

| 任务 | 频率 | 说明 |
|------|------|------|
| 小时聚合 | 每小时 | 实时性要求高的指标 |
| 日报聚合 | 每日凌晨 | 完整日报数据 |
| 月报归档 | 每月初 | 历史数据压缩 |

## 4. 前台站点设计 (river-ecommica)

### 4.1 页面结构

| 页面 | URL | 渲染方式 | 说明 |
|------|-----|---------|------|
| 首页 | / | SSG + ISR | 热门 Deal、精选商家、最新博客 |
| 商家列表 | /stores | SSG + ISR | 按分类筛选 |
| 商家详情 | /stores/{slug} | SSG + ISR | 商家介绍 + 所有优惠 |
| Deal 列表 | /deals | SSG + ISR | 筛选、排序 |
| Deal 详情 | /deals/{slug} | SSG + ISR | 优惠信息 + 追踪链接 |
| 优惠券列表 | /coupons | SSG + ISR | 折扣码集合 |
| 博客列表 | /blog | SSG + ISR | 文章列表 |
| 博客详情 | /blog/{slug} | SSG + ISR | 文章内容 + 联盟链接 |
| 分类页 | /category/{slug} | SSG + ISR | 分类聚合（SEO 长尾） |

### 4.2 SEO 策略

- **渲染**：SSG 为主 + ISR 增量更新
- **URL 结构**：语义化、扁平化
- **Meta**：每页独立 Title/Description
- **结构化数据**：Product、Offer、Article、BreadcrumbList、Organization
- **Sitemap**：自动生成，按类型分割
- **国际化**：next-intl，预留多语言扩展

## 5. 流量套利完整链路

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           买量端（花钱）                                 │
│  Google Ads / Facebook Ads / Native Ads                                 │
│  投放广告 → 用户点击 → 跳转到落地页                                       │
└────────────────────────────┬────────────────────────────────────────────┘
                             │ ?gclid=xxx&utm_source=google&utm_campaign=xxx
                             ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                        落地页（river-ecommica 或独立页）                 │
│  记录访问：来源、Campaign、关键词、设备等                                 │
│  展示 Offer 内容，引导用户点击                                           │
└────────────────────────────┬────────────────────────────────────────────┘
                             │ 用户点击 "领取优惠" 按钮
                             ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                        追踪跳转（river-server）                          │
│  GET /api/go/{trackingId}?sub1=google&sub2=campaign_xxx                 │
│  生成 click_id (ULID) → 异步写入 Click → 302 跳转联盟链接                │
└────────────────────────────┬────────────────────────────────────────────┘
                             │ 跳转到联盟商家网站
                             ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                        联盟商家网站                                      │
│  用户下单 / 注册 / 完成动作                                              │
└────────────────────────────┬────────────────────────────────────────────┘
                             │ 联盟 Postback 回调
                             ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                        转化回收（river-server）                          │
│  GET /api/postback/{network}?click_id=xxx&amount=50&sig=xxx             │
│  验签 → 幂等检查 → 归因匹配 → 记录 Conversion → 更新统计                  │
└─────────────────────────────────────────────────────────────────────────┘
```

## 6. 数据库设计规范

### 6.1 表命名

- 前缀：`river_{module}_{entity}`
- 示例：`river_affiliate_offer`, `river_tracking_click`

### 6.2 公共字段

```sql
id              BIGINT PRIMARY KEY,          -- 主键（雪花算法）
creator         VARCHAR(64) DEFAULT '',      -- 创建人
create_time     TIMESTAMP DEFAULT NOW(),     -- 创建时间
updater         VARCHAR(64) DEFAULT '',      -- 更新人
update_time     TIMESTAMP DEFAULT NOW(),     -- 更新时间
deleted         SMALLINT DEFAULT 0,          -- 软删除标记
tenant_id       BIGINT DEFAULT 0             -- 租户 ID
```

### 6.3 分区策略

Click 表按月分区：

```sql
CREATE TABLE river_tracking_click (
    click_id        VARCHAR(26) PRIMARY KEY,  -- ULID
    -- ... 其他字段
    created_at      TIMESTAMP NOT NULL
) PARTITION BY RANGE (created_at);

CREATE TABLE river_tracking_click_2026_01 
    PARTITION OF river_tracking_click
    FOR VALUES FROM ('2026-01-01') TO ('2026-02-01');
```

## 7. 安全设计

### 7.1 Postback 安全

| 措施 | 实现 |
|------|------|
| 签名验证 | HMAC-SHA256，密钥按联盟配置 |
| IP 白名单 | 可选，按联盟配置 |
| 幂等保护 | (tenant_id, network_code, external_conversion_id) 唯一约束 |
| 重放防护 | timestamp + nonce 验证（可选） |
| 限流 | 按联盟限制请求频率 |

### 7.2 Redirect 安全

| 措施 | 实现 |
|------|------|
| URL 白名单 | 只允许已登记的 landing_page/offer URL |
| 参数签名 | 防止参数篡改 |
| 日志审计 | 记录所有重定向请求 |

## 8. 监控与告警

### 8.1 关键指标

| 指标 | 告警阈值 |
|------|---------|
| Redirect P95 延迟 | > 200ms |
| Postback 签名失败率 | > 1% |
| 未归因转化率 | > 10% |
| Campaign ROI | < -20% |
| 同 IP 高频点击 | > 100/分钟 |

### 8.2 日志追踪

- 以 `click_id` 为关联 ID，串联整个链路
- 记录：点击 → 重定向 → Postback → 归因 → 统计

## 9. 实施计划

### Phase 1: 基础模块（2-3 周）✅

- [x] affiliate 模块：Network, Merchant, Offer, Category 基础 CRUD
- [ ] Admitad API 对接（预留接口，待实际对接）
- [x] 手动上传 Offer 功能

### Phase 2: 追踪核心（2 周）✅

- [x] tracking 模块：Click, Conversion, TrackingLink
- [x] 重定向接口 /api/go/{id}
- [x] Postback 接收接口 /api/postback/{network}
- [x] click_id 归因链路

### Phase 3: 优惠与内容（2 周）✅

- [x] coupon 模块：Coupon, Deal
- [x] blog 模块：Post, Author, Tag

### Phase 4: 套利管理（1-2 周）✅

- [x] campaign 模块：Campaign, CostRecord, LandingPage
- [x] ROI 计算与展示

### Phase 5: 前台站点（2-3 周）⏳

- [x] river-ecommica 基础框架（Next.js 15 脚手架已创建）
- [ ] 商家、Deal、优惠券页面
- [ ] 博客页面
- [ ] SEO 优化

### Phase 6: 统计与优化（1-2 周）✅

- [x] stats 模块：DailyStats, 聚合任务
- [ ] 仪表盘（待前台开发）
- [ ] 告警配置（待运维配置）

## 10. 附录

### 10.1 模块 Maven 结构

| 模块 | Artifact ID | 包路径 |
|------|-------------|--------|
| affiliate | river-module-affiliate | com.river.module.affiliate |
| coupon | river-module-coupon | com.river.module.coupon |
| tracking | river-module-tracking | com.river.module.tracking |
| campaign | river-module-campaign | com.river.module.campaign |
| blog | river-module-blog | com.river.module.blog |
| stats | river-module-stats | com.river.module.stats |

### 10.2 API 路径规范

| 类型 | 前缀 | 说明 |
|------|------|------|
| 管理后台 | /admin-api/{module}/ | 需要登录认证 |
| 公开接口 | /app-api/{module}/ | 前台站点调用 |
| 追踪接口 | /api/go/, /api/postback/ | 特殊路径，高性能要求 |

### 10.3 Oracle 审核意见摘要

- 评分：7/10
- 主要风险：归因身份与幂等/安全未强约束
- 已采纳建议：
  - click_id (ULID) 作为归因主键
  - Postback 幂等 + 签名验证
  - Redirect 白名单 + 异步写入
  - 转化状态机设计
  - 月分区策略（当前量级足够）
