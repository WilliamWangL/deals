# 分类 UI/UX 优化设计文档

## 背景

当前首页分类展示为顶部粘性横向滚动条，存在以下问题：
1. 视觉不突出，用户容易忽略
2. 横向滚动不便，尤其移动端
3. 缺少层级关系展示（只显示一级分类）

同时，分类详情页的 Deal/Coupon 查询未按分类 ID 过滤，显示的是全站数据。

## 目标

1. **首页分类 UI/UX 优化**：改为 Hero 下方独立卡片网格区块，展示一级+二级分类
2. **分类详情页数据修复**：Deal/Coupon 查询按分类 ID 过滤
3. **新增分类列表页**：作为首页 "View All" 的跳转目标

## 不做

- 分类管理后台功能调整
- 分类数据结构变更
- 其他页面的分类展示

---

## 首页分类 UI/UX 方案

### 布局变更

**移除**：顶部粘性分类栏（当前第 167-186 行）

**新增**：Hero 区域下方独立分类区块

### 卡片网格设计

```
┌─────────────────────────────────────────────────────────────┐
│  Browse by Category                              View All → │
├─────────────────┬─────────────────┬─────────────────────────┤
│ ┌─────────────┐ │ ┌─────────────┐ │ ┌─────────────┐         │
│ │ 🖥️ Electronics│ │ │ 👕 Fashion  │ │ │ 🏠 Home      │         │
│ ├─────────────┤ │ ├─────────────┤ │ ├─────────────┤         │
│ │ Laptops     │ │ │ Men's       │ │ │ Furniture   │         │
│ │ Phones      │ │ │ Women's     │ │ │ Kitchen     │         │
│ │ Audio       │ │ │ Kids        │ │ │ Decor       │         │
│ │ Gaming      │ │ │ Shoes       │ │ │ Garden      │         │
│ └─────────────┘ │ └─────────────┘ │ └─────────────┘         │
├─────────────────┼─────────────────┼─────────────────────────┤
│ ┌─────────────┐ │ ┌─────────────┐ │ ┌─────────────┐         │
│ │ 💪 Sports   │ │ │ 👶 Baby     │ │ │ 💄 Beauty   │         │
│ │ ...         │ │ │ ...         │ │ │ ...         │         │
│ └─────────────┘ │ └─────────────┘ │ └─────────────┘         │
└─────────────────┴─────────────────┴─────────────────────────┘
```

### 响应式布局

| 屏幕 | 列数 | 说明 |
|------|------|------|
| Desktop (≥1024px) | 4 列 | `grid-cols-4` |
| Tablet (≥768px) | 3 列 | `grid-cols-3` |
| Mobile (≥640px) | 2 列 | `grid-cols-2` |
| Small Mobile | 1 列 | `grid-cols-1` |

### 卡片组件设计

每张卡片包含：
- **图标**：一级分类图标（使用现有 `IconMap`）
- **标题**：一级分类名称，可点击跳转
- **子分类标签**：最多显示 4-6 个二级分类，每个可点击
- **溢出处理**：超过时显示 "+N more"

---

## 后端 API 修改

### 需要修改的文件

#### 1. AppDealPageReqVO.java - 添加 categoryId 参数

```java
@Schema(description = "分类 ID")
private Long categoryId;
```

#### 2. AppCouponPageReqVO.java - 添加 categoryId 参数

```java
@Schema(description = "分类 ID")
private Long categoryId;
```

#### 3. DealPageReqVO.java - 添加 categoryId 参数（Admin 也支持）

```java
@Schema(description = "分类 ID", example = "1")
private Long categoryId;
```

#### 4. CouponPageReqVO.java - 添加 categoryId 参数

```java
@Schema(description = "分类 ID", example = "1")
private Long categoryId;
```

#### 5. DealMapper.java - 修改查询逻辑

```java
// 添加 categoryIds LIKE 查询
// categoryIds 字段格式为逗号分隔，如 "1,2,3"
// 查询逻辑：使用 PostgreSQL 数组操作
```

#### 6. CouponMapper.java - 修改查询逻辑

```java
// 同上，使用字符串转数组后判断包含
```

### 查询方案

由于 `categoryIds` 是逗号分隔字符串，使用 PostgreSQL 的数组操作：

```sql
-- 方案：将字符串转数组后判断包含
WHERE string_to_array(category_ids, ',') @> ARRAY[CAST(#{categoryId} AS VARCHAR)]
```

或使用 MyBatis Plus 的 `apply` 方法在 Java 中实现。

---

## 前端修改

### 1. api.ts - 添加 categoryId 参数

```typescript
// fetchDeals 添加 categoryId
export async function fetchDeals(params?: {
  merchantId?: number;
  featured?: boolean;
  categoryId?: number;  // 新增
  pageNo?: number;
  pageSize?: number;
  regions?: string[]
}): Promise<PageResult<Deal>>

// fetchCoupons 添加 categoryId
export async function fetchCoupons(params?: {
  merchantId?: number;
  verified?: boolean;
  categoryId?: number;  // 新增
  pageNo?: number;
  pageSize?: number;
  regions?: string[]
}): Promise<PageResult<Coupon>>
```

### 2. 首页 page.tsx - 重构分类区块

**移除**：第 167-186 行的粘性分类栏

**新增**：Hero 下方的 `CategorySection` 组件

```typescript
// 新组件：components/home/CategorySection.tsx
interface CategorySectionProps {
  categories: Category[];
  locale: string;
  showViewAll?: boolean;
}
```

### 3. 分类详情页 category/[slug]/page.tsx

**修改前**：
```typescript
const [categories, allDealsResult, allCouponsResult] = await Promise.all([
  fetchCategories({ regions: region ? [region] : undefined }),
  fetchDeals({ regions: region ? [region] : undefined }),      // ❌ 无分类过滤
  fetchCoupons({ regions: region ? [region] : undefined }),    // ❌ 无分类过滤
]);
const deals = allDealsResult.list.slice(0, 8);
const coupons = allCouponsResult.list.slice(0, 6);
```

**修改后**：
```typescript
const categories = await fetchCategories({ regions: region ? [region] : undefined });
const category = findCategoryBySlug(categories, slug);

if (!category) notFound();

const [dealsResult, couponsResult] = await Promise.all([
  fetchDeals({ categoryId: category.id, regions: region ? [region] : undefined }),    // ✅ 按分类过滤
  fetchCoupons({ categoryId: category.id, regions: region ? [region] : undefined }),  // ✅ 按分类过滤
]);
const deals = dealsResult.list.slice(0, 8);
const coupons = couponsResult.list.slice(0, 6);
```

---

## 分类列表页

### 页面路径

`/[locale]/categories/page.tsx`

### 页面结构

```
┌─────────────────────────────────────────────────────────────┐
│ Hero Section                                                │
│ ┌─────────────────────────────────────────────────────────┐ │
│ │ 🧭 Explore / All Categories                             │ │
│ │                                                         │ │
│ │ Browse by                                               │ │
│ │ Category                    [统计卡片]                   │ │
│ │                             ┌────────┐ ┌────────┐       │ │
│ │ Find deals and coupons      │Categories│ │ Deals │       │ │
│ │ in your favorite categories │   8    │ │  120  │       │ │
│ │                             └────────┘ └────────┘       │ │
│ └─────────────────────────────────────────────────────────┘ │
├─────────────────────────────────────────────────────────────┤
│ Category Grid（与首页分类区块相同布局）                       │
│ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐          │
│ │ 🖥️ Electronics│ │ 👕 Fashion   │ │ 🏠 Home      │          │
│ │ ─────────────│ │ ─────────────│ │ ─────────────│          │
│ │ Laptops      │ │ Men's        │ │ Furniture    │          │
│ │ Phones       │ │ Women's      │ │ Kitchen      │          │
│ │ Audio        │ │ Kids         │ │ Decor        │          │
│ └──────────────┘ └──────────────┘ └──────────────┘          │
│ ...                                                         │
└─────────────────────────────────────────────────────────────┘
```

### 设计决策

| 元素 | 决策 | 理由 |
|------|------|------|
| Hero | ✅ 保留 | 统一风格，展示分类总数统计 |
| 搜索栏 | ❌ 移除 | 分类数量少，无需搜索 |
| 分页 | ❌ 移除 | 分类通常一页能展示完 |
| 内容区 | ✅ 卡片网格 | 与首页分类区块布局一致 |

### 数据获取

```typescript
const categories = await fetchCategories({ regions: region ? [region] : undefined });

// 统计
const totalCategories = categories.length;
const totalSubcategories = categories.reduce((acc, c) => acc + (c.children?.length || 0), 0);
```

### 组件复用

分类列表页和首页共用同一个 `CategorySection` 组件：

```typescript
// 首页：显示 View All 链接
<CategorySection categories={categories} locale={locale} showViewAll />

// 分类列表页：不显示 View All
<CategorySection categories={categories} locale={locale} />
```

---

## 实现步骤

### 后端修改

| 序号 | 文件 | 修改内容 |
|------|------|----------|
| 1 | `AppDealPageReqVO.java` | 添加 `categoryId` 字段 |
| 2 | `AppCouponPageReqVO.java` | 添加 `categoryId` 字段 |
| 3 | `DealPageReqVO.java` | 添加 `categoryId` 字段 |
| 4 | `CouponPageReqVO.java` | 添加 `categoryId` 字段 |
| 5 | `DealMapper.java` | 添加按 `categoryIds` 过滤查询 |
| 6 | `CouponMapper.java` | 添加按 `categoryIds` 过滤查询 |

### 前端修改

| 序号 | 文件 | 修改内容 |
|------|------|----------|
| 1 | `api.ts` | `fetchDeals`、`fetchCoupons` 添加 `categoryId` 参数 |
| 2 | `components/home/CategorySection.tsx` | 新建分类卡片网格组件 |
| 3 | `app/[locale]/page.tsx` | 移除粘性分类栏，添加 `CategorySection` |
| 4 | `app/[locale]/categories/page.tsx` | 新建分类列表页 |
| 5 | `app/[locale]/category/[slug]/page.tsx` | 修复数据查询，传递 `categoryId` |
| 6 | `messages/*.json` | 添加分类相关翻译 |

### 实现顺序

1. **后端 API** → 先支持 categoryId 过滤
2. **前端 api.ts** → 添加参数支持
3. **分类详情页修复** → 验证数据过滤正常
4. **CategorySection 组件** → 新建卡片网格组件
5. **首页改造** → 移除旧栏，使用新组件
6. **分类列表页** → 新建页面
