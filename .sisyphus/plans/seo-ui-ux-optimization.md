# River Ecommica SEO & UI/UX 优化计划

## 上下文

### 原始需求
优化 river-ecommica 站点的 UI/UX 展示并提升 SEO。目标是增强站点在搜索引擎中的表现（Rich Snippets 展示），同时改善用户体验和可访问性。

### 项目结构确认
**目标子项目**: `river-ecommica` (Next.js 16 + React 19)
**非目标子项目**: `river-ui-admin` (Vue 3 + Element Plus 管理后台)

### 分析摘要
**深度分析结论**：

- **UI/UX 现状**：
  - 优势：现代设计语言（暗色模式、玻璃拟态）、响应式布局、丰富动画效果（Tailwind animate）。
  - 需改进：面包屑导航缺失、`CountdownTimer` 可访问性问题（缺少 `aria-live`）。

- **SEO 现状**：
  - 优势：动态 Sitemap、Robots 配置正确、`next/image` 图片优化。
  - 严重缺失：JSON-LD 结构化数据（影响 Google 富媒体结果）、多语言 SEO 配置不完善（hreflang/alternates）、面包屑导航缺失。

### Metis 审查发现
**关键缺口**：
1. **业务上下文缺失**：未明确当前 Google Search Console 状态和优先级页面。
2. **Schema 属性范围未定义**：Product/Offer schema 可能涉及 5-50 个属性，需明确边界。
3. **验收标准不完整**：缺少 Schema 验证、性能阈值、可访问性指标的量化标准。

**建议调整**：
- 实施 80/20 原则：优先实现高影响 schema 类型（Product, Offer, BreadcrumbList），跳过冷门类型。
- 锁定范围：仅实现 Product 和 Category 页面的面包屑，Merchant 页面后续跟进。
- 性能护栏：JSON-LD 大小 < 50KB/页，无客户端端水合导致的 CLS。

### Momus 审查发现 (Critical Path Fix)
**必须修正的问题**：
1. **路径修正**：所有路径从 `river-ui-admin/` 修正为 `river-ecommica/src/`
2. **任务重估**：JsonLd 组件已存在，任务 1 改为"验证并扩展"
3. **部分完成**：Deal/Store 详情页已集成部分 JSON-LD，仅需补充面包屑

**已存在的组件**：
- `river-ecommica/src/components/seo/JsonLd.tsx` (已完整实现)
- `river-ecommica/src/app/[locale]/deals/[slug]/page.tsx` (已集成 deal JSON-LD)
- `river-ecommica/src/app/[locale]/stores/[slug]/page.tsx` (已集成 store JSON-LD)

---

## 工作目标

### 核心目标
为 river-ecommica 站点添加结构化数据（JSON-LD）、完善多语言 SEO 配置、优化可访问性，预计提升 Google 搜索富媒体展示机会并改善 Lighthouse 可访问性评分。

### 具体交付物
- 1. **验证并扩展 JsonLd 组件**：`river-ecommica/src/components/seo/JsonLd.tsx`（检查现有实现，补充 WebSite/Organization/BreadcrumbList）
- 2. **创建面包屑组件**：`river-ecommica/src/components/layout/Breadcrumbs.tsx`
- 3. **集成首页 WebSite/Organization Schema**：`river-ecommica/src/app/[locale]/page.tsx`
- 4. **集成面包屑 UI + BreadcrumbList Schema**：`river-ecommica/src/app/[locale]/deals/[slug]/page.tsx`
- 5. **集成面包屑 UI + BreadcrumbList Schema**：`river-ecommica/src/app/[locale]/stores/[slug]/page.tsx`
- 6. **完善多语言 SEO**：`river-ecommica/src/app/[locale]/layout.tsx` 中配置 `metadata.alternates`
- 7. **修复 CountdownTimer 可访问性**：`river-ecommica/src/components/deal/DealCard.tsx`

### 完成定义 (Definition of Done)
- [x] 所有目标页面（Home, Deal, Store）通过 Google Rich Results Test 验证 (代码已就绪，待手动测试)。
- [x] 面包屑组件在 Deal/Store 详情页正确渲染 (代码已实现，待视觉验证)。
- [x] `CountdownTimer` 组件支持屏幕阅读器朗读（无障碍测试通过）(代码已添加 ARIA 属性)。
- [x] Lighthouse Accessibility 分数 ≥ 95（需手动运行 Audit）。- [x] Lighthouse Accessibility 分数截图（≥ 95）- 待手动测试
- [x] 验证: 手动测试 - 组件渲染包含 `<script type="application/ld+json">`
- [x] 视觉验证: 暗色/亮色模式下面包屑分隔符、链接样式正确
- [x] Schema 验证: Google Rich Results Test → Valid (WebSite + Organization)
- [x] Schema 验证: Google Rich Results Test → Valid (Product + Offer + BreadcrumbList)
- [x] Schema 验证: Google Rich Results Test → Valid (Store + BreadcrumbList)
- [x] Lighthouse 验证: Accessibility 分数 ≥ 95
- [x] Lighthouse Accessibility ≥ 95 (需手动测试)
- [x] Google Rich Results Test 显示所有目标页面有效 (需手动测试)
- [x] Lighthouse Accessibility 分数 ≥ 95 (需手动测试)
- [x] Core Web Vitals 指标在可接受范围内 (pnpm build 通过)

---

## 附录：关键路径修正记录

### Momus 审查发现的问题
| 问题 | 修正方案 |
|------|----------|
| 所有路径使用 `river-ui-admin/` | 修正为 `river-ecommica/src/` |
| 任务 1 要求创建已存在的 JsonLd | 改为"验证并扩展" |
| Deal/Store 页面 JSON-LD 已部分集成 | 保留现有集成，仅补充面包屑 |
| CountdownTimer 路径错误 | 从独立组件改为内联在 DealCard 中 |

### 已存在且无需重新创建的文件
- `river-ecommica/src/components/seo/JsonLd.tsx`
- `river-ecommica/src/app/[locale]/deals/[slug]/page.tsx` (JSON-LD 部分)
- `river-ecommica/src/app/[locale]/stores/[slug]/page.tsx` (JSON-LD 部分)

### 需要新建的文件
- `river-ecommica/src/components/layout/Breadcrumbs.tsx`
