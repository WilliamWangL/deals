# Backend API Completion Design

> **Created**: 2026-01-14
> **Status**: Approved
> **Author**: AI Assistant (Sisyphus)

## Overview

完善 river-server 后端 App API，以支持 river-ecommica 前端的完整功能。

### 目标

1. 创建 `AppCategoryController` — 分类 App API
2. 修复 `AppMerchantController` — dealCount/couponCount 统计

### 背景

- **前端状态**：river-ecommica UI 已完成，API 层准备就绪，使用 Mock 数据降级
- **后端状态**：Deal、Coupon、Merchant、Blog 的 App API 已实现，缺 Category App API
- **问题**：Merchant 返回的 dealCount/couponCount 写死为 0

---

## 调研结论

### 后端现状

| 项目 | 状态 |
|------|------|
| 表结构 | ✅ `river_affiliate_category` 已存在，支持 `parent_id` 递归 |
| 实体类 | ✅ `CategoryDO`、`CategoryRespVO` 已有 |
| Admin API | ✅ CRUD 完整 (`/admin-api/affiliate/category/*`) |
| App API | ❌ 缺失 `AppCategoryController` |

### 联盟 API 行业标准

| 平台 | 分类结构 | 标准 |
|------|----------|------|
| Amazon | 递归树 (Browse Nodes) | 自有体系 |
| CJ/Rakuten | 多级 | 映射到 GPC |
| Awin | 递归树 | 原生支持 Google Product Category |

**结论**：行业趋势是 Google Product Category (GPC) 标准，3-7 层递归结构。

---

## 设计方案

### 1. 数据模型

后端 `river_affiliate_category` 表已具备核心字段：

```sql
CREATE TABLE river_affiliate_category (
    id              BIGINT PRIMARY KEY,
    parent_id       BIGINT NOT NULL DEFAULT 0,  -- 父分类 ID，0 表示顶级
    name            VARCHAR(100) NOT NULL,
    slug            VARCHAR(100) NOT NULL,
    level           SMALLINT NOT NULL DEFAULT 1,
    sort            INT NOT NULL DEFAULT 0,
    icon            VARCHAR(200),
    status          SMALLINT NOT NULL DEFAULT 1,
    -- 审计字段省略
);
```

**可选扩展**（未来）：
```sql
ALTER TABLE river_affiliate_category 
ADD COLUMN IF NOT EXISTS gpc_id INT;  -- Google Product Category ID
```

### 2. AppCategoryController 接口设计

| 端点 | 方法 | 参数 | 用途 |
|------|------|------|------|
| `/app-api/affiliate/category/tree` | GET | - | 获取完整分类树（前端导航菜单） |
| `/app-api/affiliate/category/list` | GET | `parentId?` | 扁平列表（按需加载子分类） |
| `/app-api/affiliate/category/get-by-slug` | GET | `slug` | 分类详情页（含面包屑） |

### 3. 响应 VO 设计

```java
// AppCategoryRespVO - 基础分类信息
@Data
public class AppCategoryRespVO {
    private Long id;
    private String name;
    private String slug;
    private String icon;
    private Integer level;
    private Long parentId;
    private Integer dealCount;    // 该分类下的 Deal 数量
    private Integer couponCount;  // 该分类下的 Coupon 数量
    private List<AppCategoryRespVO> children; // 子分类（tree 接口用）
}

// AppCategoryDetailRespVO - 详情页用（含面包屑）
@Data
public class AppCategoryDetailRespVO extends AppCategoryRespVO {
    private List<BreadcrumbVO> ancestors; // 祖先链路 [{id, name, slug}, ...]
}

@Data
public class BreadcrumbVO {
    private Long id;
    private String name;
    private String slug;
}
```

### 4. 与前端类型对齐

前端 `Category` 类型：
```typescript
interface Category {
  id: number;
  name: string;
  slug: string;
  icon?: string;
  children?: Category[];
}
```

✅ 完全兼容，无需修改前端类型。

### 5. Merchant 统计修复

**问题**：`AppMerchantController` 返回的 `dealCount` / `couponCount` 写死为 0。

**修复方案**：

```java
// Mapper 新增方法
// DealMapper
Long selectCountByMerchantId(@Param("merchantId") Long merchantId);

// CouponMapper  
Long selectCountByMerchantId(@Param("merchantId") Long merchantId);

// Controller 中使用
Long dealCount = dealMapper.selectCountByMerchantId(merchantId);
Long couponCount = couponMapper.selectCountByMerchantId(merchantId);
```

---

## 实施计划

| Phase | 任务 | 预估时间 |
|-------|------|----------|
| 1 | 创建 `AppCategoryController` + VO | 30 min |
| 2 | 实现分类树查询 Service 逻辑 | 20 min |
| 3 | 修复 Merchant 统计（Mapper + Controller） | 30 min |
| 4 | 编译验证 | 10 min |
| 5 | 前后端联调验证 | 20 min |

**总计**：约 2 小时

---

## 文件变更清单

### 新建文件

```
river-server/river-module-affiliate/river-module-affiliate-biz/
├── src/main/java/com/river/module/affiliate/controller/app/
│   └── AppCategoryController.java
└── src/main/java/com/river/module/affiliate/controller/app/vo/
    ├── AppCategoryRespVO.java
    └── AppCategoryDetailRespVO.java
```

### 修改文件

```
river-server/river-module-affiliate/river-module-affiliate-biz/
├── src/main/java/com/river/module/affiliate/controller/app/
│   └── AppMerchantController.java  # 注入统计查询
└── src/main/java/com/river/module/affiliate/service/
    └── CategoryService.java        # 新增树形查询方法

river-server/river-module-coupon/river-module-coupon-biz/
└── src/main/java/com/river/module/coupon/dal/mysql/
    ├── DealMapper.java             # 新增 selectCountByMerchantId
    └── CouponMapper.java           # 新增 selectCountByMerchantId
```

---

## 验收标准

1. [ ] `GET /app-api/affiliate/category/tree` 返回完整分类树
2. [ ] `GET /app-api/affiliate/category/get-by-slug?slug=electronics` 返回分类详情
3. [ ] `GET /app-api/affiliate/merchant/list` 返回真实的 dealCount/couponCount
4. [ ] 编译通过，无错误
5. [ ] 前端切换到真实 API 后页面正常显示
