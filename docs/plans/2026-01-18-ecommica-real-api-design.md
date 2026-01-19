# River Ecommica 对接真实 API 设计方案

## 概述

将 river-ecommica (Next.js 优惠聚合站点) 从 mock 数据切换到真实的后端 API，移除所有 mock 数据和相关逻辑。

## 设计决策

| 问题 | 决策 |
|------|------|
| 缺失字段处理 | 后端补全，使用 VO 嵌套 |
| API 路径不一致 | 修改前端，调用正确路径 |
| BlogPost.type 映射 | 前端转换，整数映射为字符串枚举 |
| 跟踪链接 | 使用 `gotoUrl` 直接跳转 |
| Mock 数据 | 完全移除 |
| 环境配置 | 本地 + 生产两套环境 |

---

## 一、后端修改

### 1.1 新增 VO

**AppMerchantSimpleVO** (精简版商家信息，供嵌套使用):

```java
package com.river.module.affiliate.controller.app.vo;

@Data
public class AppMerchantSimpleVO {
    private Long id;
    private String name;
    private String slug;
    private String logoUrl;
}
```

### 1.2 修改 AppDealRespVO

新增字段：
- `gotoUrl`: String - 直接跳转链接
- `merchant`: AppMerchantSimpleVO - 嵌套商家信息

### 1.3 修改 AppCouponRespVO

新增字段：
- `gotoUrl`: String - 直接跳转链接
- `merchant`: AppMerchantSimpleVO - 嵌套商家信息

### 1.4 修改 AppDealController

使用 `BeanUtils.toBean()` 进行转换，填充商家信息：

```java
// 列表查询 - 批量获取商家信息避免 N+1
@GetMapping("/list")
public CommonResult<List<AppDealRespVO>> getDealList(...) {
    List<DealDO> list = dealService.getDealList();
    // 过滤逻辑...

    // 批量获取商家信息
    List<Long> merchantIds = list.stream().map(DealDO::getMerchantId).distinct().toList();
    Map<Long, MerchantDO> merchantMap = merchantService.getMerchantMap(merchantIds);

    // 转换
    return success(BeanUtils.toBean(list, AppDealRespVO.class, vo -> {
        MerchantDO merchant = merchantMap.get(vo.getMerchantId());
        vo.setMerchant(BeanUtils.toBean(merchant, AppMerchantSimpleVO.class));
    }));
}
```

### 1.5 修改 AppCouponController

同 AppDealController，使用 BeanUtils 转换并填充商家信息。

---

## 二、前端修改

### 2.1 类型定义更新 (`src/types/index.ts`)

```typescript
// 新增精简版商家类型
export interface MerchantSimple {
  id: number;
  name: string;
  slug: string;
  logoUrl: string;
}

// Deal - 移除 merchantId/merchantName/merchantLogo/trackingLinkId
export interface Deal {
  id: number;
  slug: string;
  title: string;
  description: string;
  originalPrice: number;
  dealPrice: number;
  discountPercent: number;
  imageUrl: string;
  startTime: string;
  endTime: string;
  featured: boolean;
  gotoUrl: string;
  merchant: MerchantSimple;
}

// Coupon - 同样调整
export interface Coupon {
  id: number;
  code: string;
  description: string;
  discountType: number;
  discountValue: number;
  minPurchase?: number;
  endTime: string;
  verified: boolean;
  gotoUrl: string;
  merchant: MerchantSimple;
}

// Category - 新增 level, parentId
export interface Category {
  id: number;
  name: string;
  slug: string;
  icon: string;
  level?: number;
  parentId?: number;
  children?: Category[];
}

// BlogPost - type 保持字符串，前端做映射
export interface BlogPost {
  id: number;
  title: string;
  slug: string;
  excerpt: string;
  content?: string;
  coverImage: string;
  authorName: string;
  authorAvatar?: string;
  publishedAt: string;
  type: 'deal' | 'review' | 'tutorial' | 'news';
  viewCount?: number;
  featured?: boolean;
  metaTitle?: string;
  metaDescription?: string;
}
```

### 2.2 API 修改 (`src/lib/api.ts`)

1. **移除所有 mock 相关代码**：
   - 删除 `USE_MOCK` 常量
   - 删除 mock 数据导入
   - 删除 `MOCK_POSTS` 常量
   - 移除所有 mock 降级逻辑

2. **修正 API 路径**：
   - `fetchCategories`: `/coupon/category/list` → `/affiliate/category/tree`

3. **添加 BlogPost type 映射**：
```typescript
const POST_TYPE_MAP: Record<number, BlogPost['type']> = {
  1: 'deal',
  2: 'review',
  3: 'tutorial',
  4: 'news'
}

// 在 fetchPosts/fetchPostBySlug 返回时转换
function mapPostType(post: any): BlogPost {
  return {
    ...post,
    type: POST_TYPE_MAP[post.type] || 'news'
  }
}
```

### 2.3 删除 Mock 目录

删除整个 `src/lib/mock/` 目录：
- `deals.ts`
- `stores.ts`
- `coupons.ts`
- `categories.ts`
- `utils.ts`
- `index.ts`

### 2.4 环境变量配置

**`.env.local`** (本地开发):
```
NEXT_PUBLIC_API_URL=http://localhost:48080/app-api
```

**`.env.production`** (生产环境):
```
NEXT_PUBLIC_API_URL=https://api.ecommica.com/app-api
```

---

## 三、组件适配

### 3.1 DealCard 组件

```typescript
// 之前
<img src={deal.merchantLogo} />
<span>{deal.merchantName}</span>
<a href={`/api/go/${deal.trackingLinkId}`}>

// 之后
<img src={deal.merchant.logoUrl} />
<span>{deal.merchant.name}</span>
<a href={deal.gotoUrl} target="_blank" rel="noopener">
```

### 3.2 CouponCard 组件

```typescript
// 之前
<img src={coupon.merchantLogo} />
<span>{coupon.merchantName}</span>

// 之后
<img src={coupon.merchant.logoUrl} />
<span>{coupon.merchant.name}</span>
<a href={coupon.gotoUrl} target="_blank" rel="noopener">
```

### 3.3 清理无用代码

- 如果 `/api/go/[id]/route.ts` 不再需要，可以删除

---

## 四、实施顺序

1. **后端**：新增/修改 VO → 修改 Controller
2. **前端**：更新类型定义 → 修改 API 函数 → 适配组件
3. **配置**：创建环境变量文件
4. **清理**：删除 mock 目录和无用代码
5. **测试**：启动后端和前端，验证所有页面功能

---

## 五、影响范围

### 后端文件
- `AppDealController.java` - 修改
- `AppDealRespVO.java` - 修改
- `AppCouponController.java` - 修改
- `AppCouponRespVO.java` - 修改
- `AppMerchantSimpleVO.java` - 新增

### 前端文件
- `src/types/index.ts` - 修改
- `src/lib/api.ts` - 修改
- `src/lib/mock/*` - 删除
- `src/components/deal/DealCard.tsx` - 修改
- `src/components/coupon/CouponCard.tsx` - 修改
- `.env.local` - 新增
- `.env.production` - 新增
