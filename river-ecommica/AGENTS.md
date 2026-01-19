# AGENTS.md - river-ecommica

本文件为 river-ecommica（Next.js 优惠聚合站点）开发指南，供 AI Agent 使用。

## 常用命令

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

## TypeScript 配置

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

## 代码风格

### 组件约定

- 使用 App Router（`src/app/`）
- Server Components 默认
- 使用 `next-intl` 进行国际化
- 使用 Tailwind CSS 4 进行样式

### 命名规范

| 类型 | 规范 | 示例 |
|------|------|------|
| 组件 | PascalCase | `CouponCard.tsx` |
| 工具函数 | camelCase | `formatDate.ts` |
| hooks | use 前缀 | `usePagination.ts` |

### 导入顺序

1. Node.js 内置
2. React 相关
3. 第三方库
4. 项目内部组件/工具
5. 相对导入

### Tailwind CSS 规范

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

### 类型安全（硬性禁止）

```typescript
// 禁止
as any
@ts-ignore
@ts-expect-error
catch(e) {}  // 空捕获块
```

## 项目结构

```
src/
├── app/              # App Router（文件系统路由）
│   └── [locale]/     # 国际化路由
├── components/       # React 组件
│   ├── ui/           # UI 基础组件
│   ├── layout/       # 布局组件
│   ├── deal/         # Deal 相关
│   └── coupon/       # Coupon 相关
├── lib/              # 工具库
│   ├── api/          # API 调用
│   ├── tracking/     # 追踪逻辑
│   └── utils/        # 工具函数
├── i18n/             # 国际化配置
├── messages/         # 翻译文件
├── config/           # 配置
├── types/            # TypeScript 类型
└── middleware.ts     # 中间件
```

## ESLint 配置

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

## 技术栈

| 技术 | 版本 |
|------|------|
| Next.js | 16.1.1 |
| React | 19.2.3 |
| TypeScript | 5 |
| Tailwind CSS | 4 |
| next-intl | 4.7.0 |

## 重要注意事项

1. **遵循现有模式**：创建新代码前先查看类似组件
2. **未明确要求不得 commit**：不要主动创建 Git 提交
3. **类型安全**：禁止使用 `as any` 等类型错误抑制
4. **国际化**：使用 `next-intl` 管理多语言
5. **前端 UI/UX 变更**：样式、布局、动画等视觉变更委托给 `frontend-ui-ux-engineer` agent 处理
