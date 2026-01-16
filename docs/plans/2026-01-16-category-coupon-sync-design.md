# 分类与优惠券同步设计方案

## 背景

当前问题：
- `river_affiliate_category` 表为空，分类数据未利用
- `merchant.category_ids` 和 `offer.category_ids` 未填充
- `coupon` 表缺少 title、regions、category_ids 等关键字段
- `deal` 表缺少 regions、category_ids 等字段
- 优惠券/Deal 数据未从联盟同步

## 设计目标

1. 建立分类映射机制，兼容多联盟分类体系
2. 补充 Coupon/Deal 表缺失字段
3. 实现 Admitad 优惠券同步
4. 为 Merchant/Offer 填充分类数据

## 数据模型变更

### 1. 新增分类映射表

```sql
CREATE TABLE river_affiliate_category_mapping (
    id bigint PRIMARY KEY,
    network_id bigint NOT NULL,
    external_id varchar(50) NOT NULL,
    external_name varchar(200),
    category_id bigint,
    auto_created boolean DEFAULT false,
    creator varchar(64),
    create_time timestamp DEFAULT CURRENT_TIMESTAMP,
    updater varchar(64),
    update_time timestamp DEFAULT CURRENT_TIMESTAMP,
    deleted smallint DEFAULT 0,
    tenant_id bigint DEFAULT 0,
    UNIQUE(network_id, external_id, deleted)
);
```

### 2. Coupon 表新增字段

```sql
ALTER TABLE river_coupon_coupon
ADD COLUMN external_id varchar(100),
ADD COLUMN network_id bigint,
ADD COLUMN title varchar(300),
ADD COLUMN regions text,
ADD COLUMN category_ids text,
ADD COLUMN image_url varchar(500),
ADD COLUMN goto_url varchar(1000),
ADD COLUMN exclusive boolean DEFAULT false,
ADD COLUMN coupon_type smallint DEFAULT 1;
```

### 3. Deal 表新增字段

```sql
ALTER TABLE river_coupon_deal
ADD COLUMN external_id varchar(100),
ADD COLUMN network_id bigint,
ADD COLUMN regions text,
ADD COLUMN category_ids text,
ADD COLUMN goto_url varchar(1000),
ADD COLUMN exclusive boolean DEFAULT false;
```

## 主流联盟字段兼容性

| 字段 | Admitad | CJ | ShareASale | Impact | Awin | 统一字段名 |
|------|---------|----|-----------:|--------|------|-----------|
| 商家名称 | name | advertiser-name | merchant | name | advertiser | name |
| 分类 | categories[] | category | category | category | primaryCategory | category_ids |
| 地区 | regions[] | country | country | region | countries | regions |
| Logo | image | - | logo | logo | logo | logo_url |
| 优惠码 | promocode | coupon-code | couponCode | promoCode | code | code |
| 折扣 | discount | - | discountAmount | discount | - | discount_value |
| 有效期 | date_start/end | start-date/end-date | startDate/endDate | startDate/endDate | startDate/endDate | start_time/end_time |
| 外部ID | id | id | dealId | id | id | external_id |
| 跳转链接 | goto_link | link | clickUrl | url | url | goto_url |

## 同步流程

### 分类同步流程

```
Admitad API → categories: [{id: 6, name: "游戏"}]
      │
      ▼
检查 category_mapping 是否存在
      │
      ├── 不存在 → 自动创建映射记录（category_id=null）
      │
      └── 存在且有 category_id → 返回本地分类 ID
      │
      ▼
设置 merchant/offer/coupon.category_ids
```

### Coupon/Deal 同步流程

```
Admitad API: GET /coupons/website/{id}/
      │
      ▼
解析 species 字段
  - "promocode" → river_coupon_coupon
  - "sale/other" → river_coupon_deal
      │
      ▼
通过 campaign.id 查找本地 Merchant
      │
      ▼
映射分类和地区
      │
      ▼
插入或更新记录（通过 network_id + external_id 判重）
```

### Admitad → Coupon 字段映射

| Admitad 字段 | Coupon 表字段 | Deal 表字段 |
|-------------|--------------|-------------|
| id | external_id | external_id |
| name/short_name | title | title |
| promocode | code | - |
| discount | discount_value | discount_percent |
| date_start | start_time | start_time |
| date_end | end_time | end_time |
| description | terms | description |
| campaign.id | → merchant_id | → merchant_id |
| regions[] | regions | regions |
| categories[] | category_ids | category_ids |
| image | image_url | image_url |
| goto_link | goto_url | goto_url |
| exclusive | exclusive | exclusive |
| species | coupon_type | - |

## 代码结构

### 新增 Java 类

```
river-module-affiliate/
├── dal/dataobject/CategoryMappingDO.java
├── dal/mysql/CategoryMappingMapper.java
└── service/network/admitad/AdmitadCoupon.java

river-module-coupon/
├── dal/dataobject/CouponDO.java (更新)
├── dal/dataobject/DealDO.java (更新)
└── api/CouponSyncApi.java (新增跨模块接口)
```

### 同步 API 端点

| 端点 | 方法 | 说明 |
|------|------|------|
| `/admin-api/affiliate/network/sync` | POST | 同步商家和 Offer |
| `/admin-api/affiliate/network/sync-coupons` | POST | 同步优惠券和 Deal |
| `/admin-api/affiliate/network/sync-all` | POST | 全量同步 |
| `/admin-api/affiliate/category-mapping/list` | GET | 查看分类映射 |
| `/admin-api/affiliate/category-mapping/bind` | POST | 绑定本地分类 |

## 实施计划

### 阶段 1：数据库变更
- 创建 `river_affiliate_category_mapping` 表
- 为 `river_coupon_coupon` 添加新字段
- 为 `river_coupon_deal` 添加新字段
- 创建唯一索引

### 阶段 2：分类同步
- 创建 `CategoryMappingDO` 和 `CategoryMappingMapper`
- 在 `AdmitadSyncService` 中添加分类映射逻辑
- 更新 Merchant/Offer 同步以填充 `category_ids`

### 阶段 3：Coupon 模块扩展
- 更新 `CouponDO` 和 `DealDO` 添加新字段
- 创建 `CouponSyncApi` 跨模块接口
- 更新 Mapper 添加查询方法

### 阶段 4：Coupon/Deal 同步
- 创建 `AdmitadCoupon` 数据模型
- 在 `AdmitadClient` 添加 `getCoupons()` 方法
- 在 `AdmitadSyncService` 添加 `syncCoupons()` 方法
- 添加 Controller 端点

### 阶段 5：测试验证
- 执行同步测试
- 验证数据完整性
- 检查分类映射正确性
