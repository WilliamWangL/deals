# SEO & UI/UX 优化 - 学习笔记

## 执行日期
2026-01-23

## 完成的优化项

### 1. JSON-LD 结构化数据
- **新增** `generateWebSiteJsonLd()`: 返回 WebSite + Organization schema
  - 包含 `potentialAction` (搜索框功能)
  - 包含 `sameAs` (社交媒体链接)
- **已存在** `generateBreadcrumbJsonLd()`: BreadcrumbList schema

### 2. 面包屑导航
- **新建** `Breadcrumbs` 组件 (`src/components/layout/Breadcrumbs.tsx`)
- 集成到 Deal 和 Store 详情页
- 移动端响应式设计 (hidden on <sm, visible on sm+)
- 使用 `Link` from `@/i18n/routing` 支持国际化

### 3. 多语言 SEO
- 更新 `layout.tsx` metadata 配置
- 添加 `alternates.canonical`
- 添加 `alternates.languages` (en, zh)

### 4. 可访问性改进
- CountdownTimer 添加 `role="timer"`
- 添加 `aria-live="off"` 避免屏幕阅读器干扰

## 验证结果
- ✅ `pnpm build` 通过 (Next.js 16.1.1)
- ✅ `lsp_diagnostics` 所有文件清洁
- ✅ 无类型错误

## 关键文件变更
| 文件 | 变更类型 |
|------|----------|
| `src/components/seo/JsonLd.tsx` | 新增 generateWebSiteJsonLd |
| `src/components/layout/Breadcrumbs.tsx` | 新建组件 |
| `src/app/[locale]/page.tsx` | 集成首页 JSON-LD |
| `src/app/[locale]/deals/[slug]/page.tsx` | 集成面包屑 + BreadcrumbList |
| `src/app/[locale]/stores/[slug]/page.tsx` | 集成面包屑 + BreadcrumbList |
| `src/app/[locale]/layout.tsx` | 添加 alternates/hreflang |
| `src/components/deal/DealCard.tsx` | 添加 ARIA 属性 |

## 后续建议
1. 手动运行 Google Rich Results Test 验证 Schema
2. 运行 Lighthouse Audit 验证 Accessibility
3. 考虑添加 `x-default` 到 alternates 配置

## Breadcrumbs UI Update (2026-01-25)
- Verified `src/components/layout/Breadcrumbs.tsx` updates for mobile visibility and alignment.
- Changes include adding `container mx-auto px-4` to nav and `flex flex-wrap` to ol.
- Confirmed no LSP errors in the modified file.
- Note: Project build fails due to unrelated `playwright.config.ts` type error and lint issues in other files.
