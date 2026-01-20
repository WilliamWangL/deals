# River Ecommica UI/UX 测试与修复计划

> **For Claude:** REQUIRED SUB-SKILL: 每个任务使用 `agent-browser` skill 进行页面测试，发现 UI 问题时使用 `document-skills:frontend-design` skill 进行修复。

**Goal:** 逐页测试 river-ecommica 项目的所有页面，发现并修复 UI/UX 问题

**Architecture:** 使用 Playwright 浏览器自动化工具访问本地开发服务器 (http://localhost:3000)，截图分析每个页面的 UI/UX，发现问题后使用 frontend-design skill 进行专业修复

**Tech Stack:** Next.js 16, React 19, Tailwind CSS 4, Playwright

**本地服务器:** http://localhost:3000

---

## 页面清单

| 序号 | 页面 | 路径 | 组件路径 |
|------|------|------|----------|
| 1 | 首页 | `/` | `src/app/[locale]/page.tsx` |
| 2 | Deals 列表页 | `/deals` | `src/app/[locale]/deals/page.tsx` |
| 3 | Deals 详情页 | `/deals/[slug]` | `src/app/[locale]/deals/[slug]/page.tsx` |
| 4 | Coupons 页 | `/coupons` | `src/app/[locale]/coupons/page.tsx` |
| 5 | Stores 列表页 | `/stores` | `src/app/[locale]/stores/page.tsx` |
| 6 | Store 详情页 | `/stores/[slug]` | `src/app/[locale]/stores/[slug]/page.tsx` |
| 7 | Blog 列表页 | `/blog` | `src/app/[locale]/blog/page.tsx` |
| 8 | Blog 详情页 | `/blog/[slug]` | `src/app/[locale]/blog/[slug]/page.tsx` |
| 9 | Category 页 | `/category/[slug]` | `src/app/[locale]/category/[slug]/page.tsx` |

---

## Task 1: 首页 (Homepage) 测试与修复

**Files:**
- Test: `src/app/[locale]/page.tsx`
- Components: `src/components/home/*`, `src/components/layout/*`

**Step 1: 使用 agent-browser 测试首页**

使用 agent-browser skill 访问 http://localhost:3000，执行以下检查：
- 截图整个页面（桌面端 1920x1080）
- 截图移动端视图（375x812）
- 检查 Header 导航
- 检查 Hero 区域
- 检查 Deals/Coupons 展示区
- 检查 Footer
- 检查所有链接是否可点击
- 检查加载状态和空状态

**Step 2: 记录发现的问题**

将发现的 UI/UX 问题记录到 TodoWrite

**Step 3: 使用 frontend-design skill 修复问题**

对每个发现的问题，使用 frontend-design skill 进行专业修复

**Step 4: 验证修复**

再次使用 agent-browser 验证修复效果

---

## Task 2: Deals 列表页测试与修复

**Files:**
- Test: `src/app/[locale]/deals/page.tsx`
- Components: `src/components/deal/*`

**Step 1: 使用 agent-browser 测试 Deals 页**

访问 http://localhost:3000/deals，执行以下检查：
- 截图桌面端和移动端
- 检查搜索栏功能
- 检查筛选/排序功能
- 检查 Deal 卡片展示
- 检查分页组件
- 检查加载状态 (Skeleton)
- 检查空状态展示

**Step 2: 记录发现的问题**

**Step 3: 使用 frontend-design skill 修复问题**

**Step 4: 验证修复**

---

## Task 3: Deals 详情页测试与修复

**Files:**
- Test: `src/app/[locale]/deals/[slug]/page.tsx`

**Step 1: 使用 agent-browser 测试 Deal 详情页**

访问 http://localhost:3000/deals/[某个真实slug]，检查：
- 商品图片展示
- 价格和折扣信息
- 描述内容
- 跳转按钮
- 相关推荐
- 面包屑导航

**Step 2-4: 同上流程**

---

## Task 4: Coupons 页测试与修复

**Files:**
- Test: `src/app/[locale]/coupons/page.tsx`
- Components: `src/components/coupon/*`

**Step 1: 使用 agent-browser 测试 Coupons 页**

访问 http://localhost:3000/coupons，检查：
- 优惠券卡片展示
- 复制优惠码功能
- 过期时间显示
- 分类筛选
- 分页功能

**Step 2-4: 同上流程**

---

## Task 5: Stores 列表页测试与修复

**Files:**
- Test: `src/app/[locale]/stores/page.tsx`
- Components: `src/components/store/*`

**Step 1: 使用 agent-browser 测试 Stores 页**

访问 http://localhost:3000/stores，检查：
- 商家列表展示
- 搜索功能
- Logo 展示
- 优惠数量显示
- 分页功能

**Step 2-4: 同上流程**

---

## Task 6: Store 详情页测试与修复

**Files:**
- Test: `src/app/[locale]/stores/[slug]/page.tsx`

**Step 1: 使用 agent-browser 测试 Store 详情页**

访问 http://localhost:3000/stores/[某个真实slug]，检查：
- 商家信息展示
- 相关优惠券/Deals 列表
- 商家描述
- 跳转官网按钮

**Step 2-4: 同上流程**

---

## Task 7: Blog 列表页测试与修复

**Files:**
- Test: `src/app/[locale]/blog/page.tsx`
- Components: `src/components/blog/*`

**Step 1: 使用 agent-browser 测试 Blog 页**

访问 http://localhost:3000/blog，检查：
- 文章卡片展示
- 封面图
- 标题和摘要
- 发布日期
- 分页功能

**Step 2-4: 同上流程**

---

## Task 8: Blog 详情页测试与修复

**Files:**
- Test: `src/app/[locale]/blog/[slug]/page.tsx`

**Step 1: 使用 agent-browser 测试 Blog 详情页**

访问 http://localhost:3000/blog/[某个真实slug]，检查：
- 文章内容排版
- 代码块样式
- 图片展示
- 目录导航
- 相关文章推荐

**Step 2-4: 同上流程**

---

## Task 9: Category 页测试与修复

**Files:**
- Test: `src/app/[locale]/category/[slug]/page.tsx`

**Step 1: 使用 agent-browser 测试 Category 页**

访问 http://localhost:3000/category/[某个真实slug]，检查：
- 分类标题
- 分类下的 Deals/Coupons 展示
- 筛选和排序
- 分页功能

**Step 2-4: 同上流程**

---

## UI/UX 检查清单

每个页面都需要检查以下方面：

### 视觉设计
- [ ] 颜色一致性
- [ ] 字体大小层级
- [ ] 间距一致性
- [ ] 图片质量和比例
- [ ] 图标清晰度

### 响应式设计
- [ ] 桌面端 (1920px)
- [ ] 平板端 (768px)
- [ ] 移动端 (375px)

### 交互体验
- [ ] 按钮 hover 状态
- [ ] 链接可点击区域
- [ ] 加载状态
- [ ] 错误状态
- [ ] 空状态

### 无障碍性
- [ ] 颜色对比度
- [ ] 键盘导航
- [ ] 焦点状态

---

## 执行说明

1. **按顺序执行**: 从 Task 1 开始，完成一个页面后再进入下一个
2. **使用 agent-browser**: 每个任务使用 `agent-browser` skill 进行自动化测试
3. **使用 frontend-design**: 发现 UI 问题时使用 `document-skills:frontend-design` skill 进行修复
4. **验证修复**: 每次修复后重新截图验证
5. **记录问题**: 使用 TodoWrite 记录所有发现的问题和修复状态
