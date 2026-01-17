# CLAUDE.md - River Ecommica

优惠聚合站点 (deals.ecommica.com)，基于 Next.js 16 + React 19 + Tailwind CSS 4。

## 常用命令

```bash
pnpm dev      # 本地开发
pnpm build    # 构建
pnpm start    # 生产运行
pnpm lint     # 代码检查
```

## 项目架构

### 目录结构

```
src/
├── app/              # App Router（文件系统路由）
│   ├── [locale]/     # 国际化路由
│   │   ├── page.tsx      # 首页
│   │   ├── coupons/      # 优惠券页面
│   │   ├── stores/       # 店铺页面
│   │   └── categories/   # 分类页面
│   ├── globals.css   # 全局样式
│   ├── sitemap.ts    # 站点地图
│   └── robots.ts     # 爬虫配置
├── components/       # React 组件
│   ├── ui/           # shadcn/ui 基础组件
│   ├── layout/       # 布局组件（Header、Footer）
│   ├── deal/         # 交易组件
│   ├── coupon/       # 优惠券组件
│   └── store/        # 店铺组件
├── lib/              # 工具库
│   ├── api.ts        # API 客户端
│   ├── tracking.ts   # 跟踪工具
│   └── utils.ts      # 通用工具
├── i18n/             # 国际化配置
├── messages/         # 翻译文件
├── config/           # 配置文件
├── types/            # TypeScript 类型
└── middleware.ts     # 中间件（语言检测、路由）
```

## 开发规范

### 国际化 (i18n)

使用 `next-intl`：

```typescript
import { useTranslations } from 'next-intl'
const t = useTranslations('common')
```

翻译文件位于 `messages/` 目录。

### API 调用

使用 `src/lib/api.ts` 中的 API 客户端：

```typescript
import { fetchDeals, fetchCoupons } from '@/lib/api'
```

### 样式

- 使用 Tailwind CSS 4
- 使用 `class-variance-authority` (cva) 管理变体样式
- 使用 `clsx` 和 `tailwind-merge` 合并类名

## 配置文件

| 文件 | 说明 |
|------|------|
| `next.config.ts` | Next.js 配置 |
| `tailwind.config.ts` | Tailwind CSS 配置 |
| `tsconfig.json` | TypeScript 配置 |

## 重要注意事项

1. **遵循现有模式** - 创建新页面/组件前先查看类似模块
2. **类型安全** - 充分利用 TypeScript
3. **组件复用** - 优先使用 `components/ui` 中的 shadcn/ui 组件
4. **SEO 优化** - 使用 Next.js Metadata API
5. **国际化** - 所有用户可见文本必须使用翻译
6. **未明确要求不得 commit** - 不要主动创建 Git 提交
