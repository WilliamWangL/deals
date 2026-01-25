# River Ecommica UI/UX 全面重构计划

## Context

### Original Request
对 river-ecommica 广告用户端站点进行 UI/UX 全面重构。

### Interview Summary
**Key Discussions**
- 全站重构，信息架构允许重构。
- 目标：品牌升级、转化率提升、可用性提升、性能感知优化；痛点为风格不统一与留存低。
- 允许大幅调整文案/CTA；不引入埋点/GA/GTM。
- 必须保持：URL/SEO（URL 不变）、业务逻辑与数据接口不变。
- 视觉方向：参考 Honey，关键词“ecommica”；无现有品牌资产。
- 受众：全球用户，重点美国，默认英语。
- 测试：新增 Playwright E2E + 关键页面视觉回归，覆盖 deal/coupon 核心路径。
- 兼容性：桌面 Chrome/Edge/Firefox/Safari 最近两版 + iOS Safari/Android Chrome 最近两版。

**Research Findings**
- 路由与页面：`src/app/[locale]/` 下包含 home、deals、coupons、stores、category、blog 与法务页。
- 布局与组件：`src/app/[locale]/layout.tsx` 统一注入 Header/Footer/NextIntl/RegionProvider；导航配置 `src/config/navigation.ts`。
- 设计系统：Tailwind CSS v4（`globals.css` 中 @theme tokens）+ `src/components/ui/*`（shadcn）。
- 关键组件：`DealCard`/`CouponCard`/`StoreCard`、`PageHero`、搜索组件、`HeroSearchForm`。
- SEO：`JsonLd` 组件提供结构化数据；`sitemap.ts`/`robots.ts` 已配置。
- 测试：无测试基础设施。

### Metis Review
**Identified Gaps (addressed)**
- KPI 缺失 → 使用可执行的 UX/转换代理指标（CTA 清晰度、核心路径更短、移动端可用性、页面一致性）+ Playwright 可视化验证。
- SEO 元信息一致性 → 任务中新增全站 metadata 与 JsonLd 覆盖检查（保持 URL 不变）。
- 设计系统“延续 vs 替换” → 采用“延续现有 tokens + 建立统一品牌规范”的策略。
- 边界与范围 → 明确不改业务逻辑/接口、URL 不变、不加埋点。

---

## Work Objectives

### Core Objective
在不变更 URL 与业务逻辑的前提下，对 river-ecommica 全站 UI/UX 进行系统性重构，建立统一品牌体系与高转化布局，提升留存与转化体验。

### Concrete Deliverables
- 统一品牌视觉规范（色板、字体、排版、间距、组件状态、CTA 体系）。
- 全站关键页面与组件的 UI/UX 重构（首页、列表、详情、搜索筛选、专题、博客）。
- 信息架构优化（导航结构与层级调整）。
- SEO 元信息与结构化数据优化（不改 URL）。
- Playwright E2E + 视觉回归测试覆盖 deal/coupon 核心路径。

### Definition of Done
- [ ] 关键页面完成统一视觉规范与可用性优化。
- [ ] Playwright E2E + 视觉回归全部通过。
- [ ] SEO metadata 与 JsonLd 覆盖完成，URL 未变更。
- [ ] 在主流浏览器默认范围内完成手工验证。

### Must Have
- 统一品牌视觉系统（颜色、字体、按钮、卡片、图标/徽章）。
- 优先优化 deal/coupon 核心路径（首页 → 搜索/筛选 → 详情 → CTA）。
- 兼容移动端/桌面端（等权）。
- SEO meta/结构化数据优化（URL 不变）。

### Must NOT Have (Guardrails)
- 不修改 URL 结构与路由。
- 不更改业务逻辑与数据接口。
- 不新增埋点与第三方分析工具。
- 不引入后端改动。

---

## Verification Strategy (MANDATORY)

### Test Decision
- **Infrastructure exists**: NO
- **User wants tests**: YES (Tests-after)
- **Framework**: Playwright (@playwright/test)

### Test Setup Task (required)
- Install: `npm i -D @playwright/test`
- Install browsers: `npx playwright install`
- Config: `playwright.config.ts` (baseURL, projects, screenshot thresholds)
- Example test: `tests/e2e/smoke.spec.ts`

### Manual QA (always required)
All UI changes must include Playwright-driven visual checks + manual functional verification for core paths.

---

## Task Flow

```
Test Setup → Design System → Global Layout/IA → Home → Listings → Detail Pages → Search/Filters → SEO → Performance/A11y → Tests
```

## TODOs

> Implementation + Test = ONE Task. Never separate.

### 0. Setup Playwright E2E + Visual Regression

**What to do**
- Add Playwright dependencies and config.
- Create initial smoke test and base screenshot test.
- Add npm scripts (`test:e2e`, `test:e2e:ui`).

**References**
- `river-ecommica/package.json` — add scripts for Playwright.

**Acceptance Criteria**
- [ ] `npx playwright install` completes successfully.
- [ ] `npm run test:e2e` runs and passes a smoke test.
- [ ] Baseline screenshots generated for at least 1 page.

---

### 1. Establish Unified Brand System & Tokens

**What to do**
- Redesign color palette + typography scale + spacing system aligned with Honey-like direction.
- Update `globals.css` @theme tokens and shared utility classes (cards, badges, CTA states).
- Update shadcn UI styles to match new brand tokens.

**Must NOT do**
- Do not change URL routes or introduce new data sources.

**References**
- `src/app/globals.css` — existing @theme tokens and utility classes.
- `src/components/ui/*` — shadcn components to align with new tokens.

**Acceptance Criteria**
- [ ] Token updates reflect a cohesive color/typography system.
- [ ] Key UI primitives (buttons, cards, badges) consistently render across pages.
- [ ] Visual contrast meets accessibility minimums (manual check).

---

### 2. Rebuild Global Layout + Navigation IA

**What to do**
- Redesign Header/Footer to match new brand system.
- Update navigation structure and labels to optimized IA (still within existing URLs).
- Ensure language/region selectors remain accessible and coherent.

**References**
- `src/app/[locale]/layout.tsx` — global shell layout.
- `src/components/layout/Header.tsx`, `Footer.tsx` — header/footer UI.
- `src/config/navigation.ts` — nav structure.

**Acceptance Criteria**
- [ ] Navigation reflects updated IA without changing routes.
- [ ] Mobile and desktop menus are usable and consistent.
- [ ] Playwright screenshots for header/footer on mobile + desktop.

---

### 3. Home Page Redesign (Conversion-First)

**What to do**
- Redesign hero + search form for immediate value proposition and CTA clarity.
- Add trust signals (verified badges, success metrics) + key categories.
- Highlight top deals/coupons and blog funnel content.

**References**
- `src/app/[locale]/page.tsx` — home page.
- `src/components/home/HeroSearchForm.tsx` — hero search.
- `src/components/layout/PageHero.tsx` — hero structure.

**Acceptance Criteria**
- [ ] Hero communicates value & CTA clearly on first screen.
- [ ] Visual hierarchy guides to search and top deals.
- [ ] Playwright: screenshot for home (desktop + mobile).

---

### 4. Listing Pages UX (Deals/Coupons/Stores/Blog)

**What to do**
- Standardize list layout with shared filters/search toolbar.
- Optimize cards for clarity (discount, validity, CTA).
- Add empty-state + no-results UX.

**References**
- `src/app/[locale]/deals/page.tsx`
- `src/app/[locale]/coupons/page.tsx`
- `src/app/[locale]/stores/page.tsx`
- `src/app/[locale]/blog/page.tsx`
- `src/components/deal/DealCard.tsx`, `src/components/coupon/CouponCard.tsx`, `src/components/store/StoreCard.tsx`

**Acceptance Criteria**
- [ ] Cards show clear discount, validity, CTA.
- [ ] Filters/search are usable on mobile (drawer or sticky pattern).
- [ ] No-results states are informative and branded.
- [ ] Playwright snapshots for each listing page.

---

### 5. Detail Pages UX (Deal/Store/Blog)

**What to do**
- Redesign detail pages for trust, clarity, and CTA prioritization.
- Improve coupon copy/interaction where coupons appear (copy-to-clip, usage hints).
- Add related recommendations and content funnel.

**References**
- `src/app/[locale]/deals/[slug]/page.tsx`
- `src/app/[locale]/stores/[slug]/page.tsx`
- `src/app/[locale]/blog/[slug]/page.tsx`
- `src/components/seo/JsonLd.tsx` — ensure structured data intact.

**Acceptance Criteria**
- [ ] CTA visible within first scroll.
- [ ] Deal/coupon metadata clearly communicated (expiry, restrictions).
- [ ] Playwright snapshots for at least one deal and one coupon detail.

---

### 6. Search & Filter UX Upgrade

**What to do**
- Unify search bar components and filter UI.
- Improve mobile filter ergonomics and applied filter visibility.

**References**
- `src/components/ui/search-bar.tsx`
- `src/components/*/DealsSearchBar.tsx` / `StoresSearchBar.tsx`

**Acceptance Criteria**
- [ ] Filters usable on mobile with clear applied state.
- [ ] Search input shows suggestions or helpful affordances.
- [ ] Playwright functional test: search → apply filter → open detail.

---

### 7. SEO & Structured Data Enhancements

**What to do**
- Ensure metadata on all key routes consistent with new IA/UX.
- Add/adjust structured data where missing; avoid thin content signals.

**References**
- `src/components/seo/JsonLd.tsx`
- `src/app/[locale]/layout.tsx` metadata
- `src/app/sitemap.ts`, `src/app/robots.ts`

**Acceptance Criteria**
- [ ] Metadata updated without URL changes.
- [ ] JsonLd schemas remain valid for deal/store/blog.

---

### 8. Perceived Performance + Accessibility

**What to do**
- Add skeletons/placeholder states where needed.
- Ensure focus-visible, skip-link, aria-expanded for menus.

**References**
- `src/app/globals.css`
- `src/components/layout/Header.tsx`

**Acceptance Criteria**
- [ ] Visual loading states for key lists.
- [ ] Keyboard navigation works for header and filters.

---

### 9. Playwright Coverage for Deal/Coupon Core Paths

**What to do**
- Implement tests for: home → search → list → detail → CTA click.
- Visual regression for home, deals list, coupon list, detail.

**Acceptance Criteria**
- [ ] `npm run test:e2e` passes all specs.
- [ ] Visual snapshots committed for core pages.

---

## Success Criteria

### Verification Commands
```bash
npm run test:e2e
```

### Final Checklist
- [ ] All Must Have items implemented.
- [ ] No Must NOT Have violations (URL/logic/analytics untouched).
- [ ] Core deal/coupon flows validated via Playwright and manual QA.
- [ ] Manual verification on desktop + mobile for main browsers (latest two versions).
