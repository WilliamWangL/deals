# River Ecommica 对接真实 API 实施计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 将 river-ecommica 从 mock 数据切换到真实后端 API，移除所有 mock 代码。

**Architecture:** 后端 Controller 补充商家信息（VO 嵌套），前端修正类型定义和 API 调用路径，移除 mock 数据和降级逻辑。

**Tech Stack:** Java 17 + Spring Boot (后端), Next.js 16 + TypeScript (前端), BeanUtils (对象转换)

---

## Task 1: 新增 AppMerchantSimpleVO

**Files:**
- Create: `river-server/river-module-affiliate/src/main/java/com/river/module/affiliate/controller/app/vo/AppMerchantSimpleVO.java`

**Step 1: 创建 VO 文件**

```java
package com.river.module.affiliate.controller.app.vo;

import lombok.Data;

@Data
public class AppMerchantSimpleVO {

    private Long id;

    private String name;

    private String slug;

    private String logoUrl;

}
```

**Step 2: 验证编译**

Run: `cd river-server && mvn compile -pl river-module-affiliate/river-module-affiliate-biz -am -q`
Expected: BUILD SUCCESS

**Step 3: Commit**

```bash
git add river-server/river-module-affiliate/src/main/java/com/river/module/affiliate/controller/app/vo/AppMerchantSimpleVO.java
git commit -m "feat(affiliate): add AppMerchantSimpleVO for nested merchant info"
```

---

## Task 2: 修改 AppDealRespVO

**Files:**
- Modify: `river-server/river-module-coupon/src/main/java/com/river/module/coupon/controller/app/vo/AppDealRespVO.java`

**Step 1: 查看当前文件结构**

Run: `cat river-server/river-module-coupon/src/main/java/com/river/module/coupon/controller/app/vo/AppDealRespVO.java`

**Step 2: 添加 gotoUrl 和 merchant 字段**

新增字段：
```java
import com.river.module.affiliate.controller.app.vo.AppMerchantSimpleVO;

// 在类中添加：
private String gotoUrl;

private AppMerchantSimpleVO merchant;
```

**Step 3: 验证编译**

Run: `cd river-server && mvn compile -pl river-module-coupon/river-module-coupon-biz -am -q`
Expected: BUILD SUCCESS

**Step 4: Commit**

```bash
git add river-server/river-module-coupon/src/main/java/com/river/module/coupon/controller/app/vo/AppDealRespVO.java
git commit -m "feat(coupon): add gotoUrl and merchant fields to AppDealRespVO"
```

---

## Task 3: 修改 AppCouponRespVO

**Files:**
- Modify: `river-server/river-module-coupon/src/main/java/com/river/module/coupon/controller/app/vo/AppCouponRespVO.java`

**Step 1: 查看当前文件结构**

Run: `cat river-server/river-module-coupon/src/main/java/com/river/module/coupon/controller/app/vo/AppCouponRespVO.java`

**Step 2: 添加 gotoUrl 和 merchant 字段**

新增字段：
```java
import com.river.module.affiliate.controller.app.vo.AppMerchantSimpleVO;

// 在类中添加：
private String gotoUrl;

private AppMerchantSimpleVO merchant;
```

**Step 3: 验证编译**

Run: `cd river-server && mvn compile -pl river-module-coupon/river-module-coupon-biz -am -q`
Expected: BUILD SUCCESS

**Step 4: Commit**

```bash
git add river-server/river-module-coupon/src/main/java/com/river/module/coupon/controller/app/vo/AppCouponRespVO.java
git commit -m "feat(coupon): add gotoUrl and merchant fields to AppCouponRespVO"
```

---

## Task 4: 修改 AppDealController

**Files:**
- Modify: `river-server/river-module-coupon/src/main/java/com/river/module/coupon/controller/app/AppDealController.java`

**Step 1: 注入 MerchantService 并修改转换逻辑**

关键修改：
1. 注入 `MerchantService`
2. 列表接口：批量获取商家信息，使用 BeanUtils 转换
3. 详情接口：单独获取商家信息

```java
import com.river.framework.common.util.object.BeanUtils;
import com.river.module.affiliate.controller.app.vo.AppMerchantSimpleVO;
import com.river.module.affiliate.dal.dataobject.MerchantDO;
import com.river.module.affiliate.service.MerchantService;

@Resource
private MerchantService merchantService;

// 列表接口修改
@GetMapping("/list")
public CommonResult<List<AppDealRespVO>> getDealList(...) {
    List<DealDO> list = dealService.getDealList();
    // 过滤逻辑...
    List<DealDO> filtered = list.stream()
            .filter(d -> merchantId == null || d.getMerchantId().equals(merchantId))
            .filter(d -> featured == null || d.getFeatured().equals(featured))
            .toList();

    // 批量获取商家信息
    List<Long> merchantIds = filtered.stream().map(DealDO::getMerchantId).distinct().toList();
    Map<Long, MerchantDO> merchantMap = merchantService.getMerchantMap(merchantIds);

    // 使用 BeanUtils 转换
    List<AppDealRespVO> result = BeanUtils.toBean(filtered, AppDealRespVO.class, vo -> {
        // 需要从原始 DO 中获取 merchantId，这里需要调整实现
    });
    return success(result);
}
```

**注意：** 由于 BeanUtils.toBean 的 peek 函数中无法直接获取原始 DO，需要在转换后单独处理商家信息。参考现有 AppMerchantController 的实现模式。

**Step 2: 验证编译**

Run: `cd river-server && mvn compile -pl river-module-coupon/river-module-coupon-biz -am -q`
Expected: BUILD SUCCESS

**Step 3: 启动后端验证接口**

Run: `cd river-server/river-server && mvn spring-boot:run`
Test: `curl http://localhost:48080/app-api/coupon/deal/list | jq`
Expected: 返回包含 `merchant` 对象的 Deal 列表

**Step 4: Commit**

```bash
git add river-server/river-module-coupon/src/main/java/com/river/module/coupon/controller/app/AppDealController.java
git commit -m "feat(coupon): enhance AppDealController with merchant info using BeanUtils"
```

---

## Task 5: 修改 AppCouponController

**Files:**
- Modify: `river-server/river-module-coupon/src/main/java/com/river/module/coupon/controller/app/AppCouponController.java`

**Step 1: 注入 MerchantService 并修改转换逻辑**

与 Task 4 类似，添加：
1. 注入 `MerchantService`
2. 批量获取商家信息
3. 使用 BeanUtils 转换并填充 merchant

**Step 2: 验证编译**

Run: `cd river-server && mvn compile -pl river-module-coupon/river-module-coupon-biz -am -q`
Expected: BUILD SUCCESS

**Step 3: 验证接口**

Test: `curl http://localhost:48080/app-api/coupon/coupon/list | jq`
Expected: 返回包含 `merchant` 对象的 Coupon 列表

**Step 4: Commit**

```bash
git add river-server/river-module-coupon/src/main/java/com/river/module/coupon/controller/app/AppCouponController.java
git commit -m "feat(coupon): enhance AppCouponController with merchant info using BeanUtils"
```

---

## Task 6: 更新前端类型定义

**Files:**
- Modify: `river-ecommica/src/types/index.ts`

**Step 1: 更新类型定义**

```typescript
// 新增精简版商家类型
export interface MerchantSimple {
  id: number;
  name: string;
  slug: string;
  logoUrl: string;
}

// Deal - 使用嵌套 merchant
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

// Coupon - 使用嵌套 merchant
export interface Coupon {
  id: number;
  code: string;
  title?: string;
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

// BlogPost - 保持不变
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

// Store 保持不变
export interface Store {
  id: number;
  name: string;
  slug: string;
  logoUrl: string;
  description: string;
  domain: string;
  rating: number;
  dealCount: number;
  couponCount: number;
  regions?: string[];
}
```

**Step 2: 验证 TypeScript 编译**

Run: `cd river-ecommica && pnpm tsc --noEmit 2>&1 | head -50`
Expected: 会有类型错误（因为组件还未更新），这是预期的

**Step 3: Commit**

```bash
git add river-ecommica/src/types/index.ts
git commit -m "feat(ecommica): update types for real API with nested merchant"
```

---

## Task 7: 重写 API 函数

**Files:**
- Modify: `river-ecommica/src/lib/api.ts`

**Step 1: 完全重写 api.ts**

移除所有 mock 相关代码，简化 API 函数：

```typescript
import { Deal, Store, Coupon, BlogPost, Category } from '@/types'

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:48080/app-api'

// BlogPost type 映射
const POST_TYPE_MAP: Record<number, BlogPost['type']> = {
  1: 'deal',
  2: 'review',
  3: 'tutorial',
  4: 'news'
}

function mapPostType(post: Record<string, unknown>): BlogPost {
  return {
    ...post,
    type: POST_TYPE_MAP[post.type as number] || 'news'
  } as BlogPost
}

export async function fetchDeals(params?: { merchantId?: number; featured?: boolean }): Promise<Deal[]> {
  const url = new URL(`${API_BASE_URL}/coupon/deal/list`)
  if (params?.merchantId) url.searchParams.set('merchantId', String(params.merchantId))
  if (params?.featured !== undefined) url.searchParams.set('featured', String(params.featured))

  const res = await fetch(url.toString(), { next: { revalidate: 300 } })
  if (!res.ok) throw new Error('Fetch deals failed')
  const json = await res.json()
  return json.data || []
}

export async function fetchDealBySlug(slug: string): Promise<Deal | null> {
  const res = await fetch(`${API_BASE_URL}/coupon/deal/get-by-slug?slug=${encodeURIComponent(slug)}`, {
    next: { revalidate: 300 },
  })
  if (!res.ok) throw new Error('Fetch deal failed')
  const json = await res.json()
  return json.data || null
}

export async function fetchStores(): Promise<Store[]> {
  const res = await fetch(`${API_BASE_URL}/affiliate/merchant/list`, { next: { revalidate: 3600 } })
  if (!res.ok) throw new Error('Fetch stores failed')
  const json = await res.json()
  return json.data || []
}

export async function fetchStoreBySlug(slug: string): Promise<Store | null> {
  const res = await fetch(`${API_BASE_URL}/affiliate/merchant/get-by-slug?slug=${encodeURIComponent(slug)}`, {
    next: { revalidate: 3600 },
  })
  if (!res.ok) throw new Error('Fetch store failed')
  const json = await res.json()
  return json.data || null
}

export async function fetchCoupons(params?: { merchantId?: number; verified?: boolean }): Promise<Coupon[]> {
  const url = new URL(`${API_BASE_URL}/coupon/coupon/list`)
  if (params?.merchantId) url.searchParams.set('merchantId', String(params.merchantId))
  if (params?.verified !== undefined) url.searchParams.set('verified', String(params.verified))

  const res = await fetch(url.toString(), { next: { revalidate: 300 } })
  if (!res.ok) throw new Error('Fetch coupons failed')
  const json = await res.json()
  return json.data || []
}

export async function fetchPosts(params?: { type?: string; featured?: boolean }): Promise<BlogPost[]> {
  const url = new URL(`${API_BASE_URL}/blog/post/list`)
  if (params?.type) url.searchParams.set('type', params.type)
  if (params?.featured !== undefined) url.searchParams.set('featured', String(params.featured))

  const res = await fetch(url.toString(), { next: { revalidate: 300 } })
  if (!res.ok) throw new Error('Fetch posts failed')
  const json = await res.json()
  return (json.data || []).map(mapPostType)
}

export async function fetchPostBySlug(slug: string): Promise<BlogPost | null> {
  const res = await fetch(`${API_BASE_URL}/blog/post/get-by-slug?slug=${encodeURIComponent(slug)}`, {
    next: { revalidate: 300 },
  })
  if (!res.ok) throw new Error('Fetch post failed')
  const json = await res.json()
  return json.data ? mapPostType(json.data) : null
}

export async function fetchCategories(): Promise<Category[]> {
  const res = await fetch(`${API_BASE_URL}/affiliate/category/tree`, { next: { revalidate: 3600 } })
  if (!res.ok) throw new Error('Fetch categories failed')
  const json = await res.json()
  return json.data || []
}
```

**Step 2: 验证 TypeScript 编译**

Run: `cd river-ecommica && pnpm tsc --noEmit 2>&1 | head -50`

**Step 3: Commit**

```bash
git add river-ecommica/src/lib/api.ts
git commit -m "feat(ecommica): rewrite API functions without mock fallback"
```

---

## Task 8: 删除 Mock 目录

**Files:**
- Delete: `river-ecommica/src/lib/mock/` (整个目录)

**Step 1: 删除 mock 目录**

Run: `rm -rf river-ecommica/src/lib/mock`

**Step 2: 验证删除**

Run: `ls river-ecommica/src/lib/`
Expected: 只剩 `api.ts`, `tracking.ts`, `utils.ts`

**Step 3: Commit**

```bash
git add -A river-ecommica/src/lib/mock
git commit -m "chore(ecommica): remove mock data directory"
```

---

## Task 9: 更新 DealCard 组件

**Files:**
- Modify: `river-ecommica/src/components/deal/DealCard.tsx`

**Step 1: 查看当前组件结构**

Run: `cat river-ecommica/src/components/deal/DealCard.tsx`

**Step 2: 更新字段访问**

将所有 `deal.merchantName` 改为 `deal.merchant.name`
将所有 `deal.merchantLogo` 改为 `deal.merchant.logoUrl`
将跳转链接改为 `deal.gotoUrl`

**Step 3: 验证 TypeScript 编译**

Run: `cd river-ecommica && pnpm tsc --noEmit 2>&1 | grep -i error | head -20`

**Step 4: Commit**

```bash
git add river-ecommica/src/components/deal/DealCard.tsx
git commit -m "feat(ecommica): update DealCard to use nested merchant"
```

---

## Task 10: 更新 CouponCard 组件

**Files:**
- Modify: `river-ecommica/src/components/coupon/CouponCard.tsx`

**Step 1: 查看当前组件结构**

Run: `cat river-ecommica/src/components/coupon/CouponCard.tsx`

**Step 2: 更新字段访问**

将所有 `coupon.merchantName` 改为 `coupon.merchant.name`
将所有 `coupon.merchantLogo` 改为 `coupon.merchant.logoUrl`
将跳转链接改为 `coupon.gotoUrl`

**Step 3: 验证 TypeScript 编译**

Run: `cd river-ecommica && pnpm tsc --noEmit 2>&1 | grep -i error | head -20`

**Step 4: Commit**

```bash
git add river-ecommica/src/components/coupon/CouponCard.tsx
git commit -m "feat(ecommica): update CouponCard to use nested merchant"
```

---

## Task 11: 创建环境变量文件

**Files:**
- Create: `river-ecommica/.env.local`
- Create: `river-ecommica/.env.production`

**Step 1: 创建本地环境变量**

```bash
echo "NEXT_PUBLIC_API_URL=http://localhost:48080/app-api" > river-ecommica/.env.local
```

**Step 2: 创建生产环境变量**

```bash
echo "NEXT_PUBLIC_API_URL=https://api.ecommica.com/app-api" > river-ecommica/.env.production
```

**Step 3: 确认 .gitignore 包含 .env.local**

Run: `grep -q ".env.local" river-ecommica/.gitignore && echo "OK" || echo "NEED_ADD"`

如果输出 NEED_ADD，添加到 .gitignore

**Step 4: Commit (只提交 .env.production)**

```bash
git add river-ecommica/.env.production
git commit -m "chore(ecommica): add production environment config"
```

---

## Task 12: 修复其他组件和页面的类型错误

**Files:**
- 根据 TypeScript 错误修复相关文件

**Step 1: 运行完整 TypeScript 检查**

Run: `cd river-ecommica && pnpm tsc --noEmit 2>&1`

**Step 2: 逐个修复类型错误**

根据错误信息修复各页面和组件中对 `merchantName`、`merchantLogo`、`trackingLinkId` 的引用

**Step 3: 验证无类型错误**

Run: `cd river-ecommica && pnpm tsc --noEmit`
Expected: 无错误输出

**Step 4: Commit**

```bash
git add -A
git commit -m "fix(ecommica): fix type errors after API migration"
```

---

## Task 13: 端到端验证

**Step 1: 启动后端服务**

Run: `cd river-server/river-server && mvn spring-boot:run`

**Step 2: 启动前端开发服务器**

Run: `cd river-ecommica && pnpm dev`

**Step 3: 验证各页面**

- 首页 http://localhost:3000
- Deals 页面 http://localhost:3000/deals
- Coupons 页面 http://localhost:3000/coupons
- Stores 页面 http://localhost:3000/stores
- 店铺详情页 http://localhost:3000/stores/{slug}

**Step 4: 验证 API 数据正确加载**

检查：
- Deal/Coupon 卡片显示商家名称和 Logo
- 点击跳转链接正常工作
- 分类数据正常加载

---

## Task 14: 最终提交和合并准备

**Step 1: 运行 lint 检查**

Run: `cd river-ecommica && pnpm lint`

**Step 2: 修复新增的 lint 错误（如有）**

**Step 3: 最终提交**

```bash
git add -A
git commit -m "feat(ecommica): complete migration to real API"
```

**Step 4: 推送分支**

```bash
git push -u origin feature/ecommica-real-api
```
