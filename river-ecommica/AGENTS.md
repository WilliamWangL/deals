# River Ecommica - AI Agent 开发规范

> 本文档继承自 [项目通用规范](../AGENTS.md)。
> 
> 如未特别说明，遵循项目通用规范的所有约定。

## 特有规范

本文档记录前端站点子项目特有的开发规范。

### 技术栈分类

| 分类 | 模式 | 说明 |
|------|------|------|
| 框架 | Next.js + React | 前端框架 |
| 样式 | Tailwind CSS 类型 | 原子化 CSS |
| 国际化 | next-intl 模式 | 国际化方案 |
| 状态管理 | React Hooks 模式 | 状态管理 |

### 国际化模式

#### 目录结构

国际化资源位于 `src/` 目录下：

```
src/
├── app/
│   └── [locale]/     # 国际化路由
├── i18n/             # 国际化配置
└── messages/         # 翻译文件
```

#### 使用方式

- 使用 App Router 的 `[locale]` 动态路由
- 翻译文件组织在 `messages/` 目录
- 通过 `next-intl` 的 useTranslations hook 获取翻译
- 页面组件通过 params 获取 locale 参数

### 常用命令

```bash
cd river-ecommica

# 本地开发
pnpm dev

# 构建
pnpm build

# 生产运行
pnpm start

# 代码检查
pnpm lint
```

### TypeScript 配置

```json
{
  "compilerOptions": {
    "strict": true,
    "jsx": "react-jsx",
    "paths": {
      "@/*": ["./src/*"]
    }
  }
}
```

### 代码风格

#### 组件约定

- 使用 App Router（`src/app/`）
- Server Components 默认
- 使用 `next-intl` 进行国际化
- 使用 Tailwind CSS 4 进行样式

#### 命名规范

| 类型 | 规范 | 示例 |
|------|------|------|
| 组件 | PascalCase | `CouponCard.tsx` |
| 工具函数 | camelCase | `formatDate.ts` |
| hooks | use 前缀 | `usePagination.ts` |

#### 导入顺序

1. Node.js 内置
2. React 相关
3. 第三方库
4. 项目内部组件/工具
5. 相对导入

#### Tailwind CSS 规范

- 使用 `@tailwindcss/postcss`
- 通过 `tailwind-merge` 合并类名
- 使用 `clsx` 管理条件类名

```typescript
import { clsx } from 'clsx'
import { twMerge } from 'tailwind-merge'

function cn(...inputs: (string | undefined | null | false)[]) {
  return twMerge(clsx(inputs))
}
```

#### 类型安全（硬性禁止）

```typescript
// 禁止
as any
@ts-ignore
@ts-expect-error
catch(e) {}  // 空捕获块
```

### 项目结构

```
src/
├── app/              # App Router（文件系统路由）
│   └── [locale]/     # 国际化路由
├── components/       # React 组件
│   ├── ui/           # UI 基础组件
│   ├── layout/       # 布局组件
│   ├── deal/         # Deal 相关
│   ├── coupon/       # Coupon 相关
│   ├── store/        # Store 相关
│   └── blog/         # Blog 相关
├── lib/              # 工具库
│   ├── api/          # API 调用
│   ├── tracking/     # 追踪逻辑
│   └── utils/        # 工具函数
├── constants/        # 常量定义
│   └── pagination.ts # 分页配置
├── i18n/             # 国际化配置
├── messages/         # 翻译文件
├── config/           # 配置
├── types/            # TypeScript 类型
└── middleware.ts     # 中间件
```

### 分页规范

#### 分页常量

所有分页配置使用 `src/constants/pagination.ts` 中的枚举常量，禁止硬编码：

```typescript
import { PAGINATION } from '@/constants/pagination';

// 使用方式
const pageSize = PAGINATION.PAGE_SIZE.STORE;    // 12
const pageNo = PAGINATION.DEFAULT_PAGE;         // 1
const pageRange = PAGINATION.PAGE_RANGE;        // 5
```

#### 分页配置

| 常量 | 值 | 说明 |
|------|-----|------|
| `PAGINATION.PAGE_SIZE.STORE` | 12 | 商家列表每页数量 |
| `PAGINATION.PAGE_SIZE.DEAL` | 12 | 优惠列表每页数量 |
| `PAGINATION.PAGE_SIZE.COUPON` | 12 | 优惠券列表每页数量 |
| `PAGINATION.PAGE_SIZE.BLOG` | 9 | 博客列表每页数量 |
| `PAGINATION.DEFAULT_PAGE` | 1 | 默认页码 |
| `PAGINATION.PAGE_RANGE` | 5 | 分页组件显示的页码数量 |

#### 分页组件

各模块使用对应的分页组件：

```tsx
import { StorePagination } from '@/components/store/StorePagination';
import { DealPagination } from '@/components/deal/DealPagination';
import { CouponPagination } from '@/components/coupon/CouponPagination';
import { BlogPagination } from '@/components/blog/BlogPagination';
```

#### API 返回格式

```typescript
interface PageResult<T> {
  total: number;  // 总数量
  list: T[];      // 当前页数据
}

// API 调用
const { list: items, total } = await fetchStores({
  pageNo: PAGINATION.DEFAULT_PAGE,
  pageSize: PAGINATION.PAGE_SIZE.STORE,
});
```

#### URL 参数

分页状态同步到 URL 查询参数：

- `?page=2` - 第 2 页
- `?page=3&q=keyword` - 第 3 页 + 搜索关键词

#### 页面中使用分页

```tsx
export default async function StoresPage({
  params,
  searchParams
}: {
  params: Promise<{ locale: string }>;
  searchParams: Promise<{ q?: string; page?: string }>
}) {
  const { locale } = await params;
  const queryParams = await searchParams;
  
  const currentPage = parseInt(
    queryParams.page || String(PAGINATION.DEFAULT_PAGE), 
    10
  );
  const pageSize = PAGINATION.PAGE_SIZE.STORE;

  const { list: stores, total } = await fetchStores({
    pageNo: currentPage,
    pageSize,
  });

  return (
    <>
      <StoreCard ... />
      <StorePagination 
        total={total} 
        pageSize={pageSize} 
        currentPage={currentPage} 
      />
    </>
  );
}
```

### ESLint 配置

使用 `eslint-config-next` 作为基础配置：

```javascript
import { defineConfig, globalIgnores } from "eslint/config";
import nextVitals from "eslint-config-next/core-web-vitals";
import nextTs from "eslint-config-next/typescript";

const eslintConfig = defineConfig([
  ...nextVitals,
  ...nextTs,
  globalIgnores([".next/**", "out/**", "build/**"]),
]);
```

### 技术栈

| 技术 | 版本 |
|------|------|
| Next.js | 16.1.1 |
| React | 19.2.3 |
| TypeScript | 5 |
| Tailwind CSS | 4 |
| next-intl | 4.7.0 |

### 重要注意事项

1. **遵循现有模式**：创建新代码前先查看类似组件
2. **未明确要求不得 commit**：不要主动创建 Git 提交
3. **类型安全**：禁止使用 `as any` 等类型错误抑制
4. **国际化**：使用 `next-intl` 管理多语言
5. **前端 UI/UX 变更**：样式、布局、动画等视觉变更使用 `frontend-design ` skill 处理
