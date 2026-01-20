# Ecommica Footer Pages Design

> 设计日期：2026-01-20
> 状态：已批准

## 概述

为 river-ecommica 项目补齐 Footer 中链接的缺失页面，共 6 个静态页面：

| 页面 | 路径 | 类型 |
|------|------|------|
| About Us | `/about` | 品牌介绍 |
| Contact | `/contact` | 联系信息 |
| FAQ | `/faq` | 常见问题 |
| Privacy Policy | `/privacy-policy` | 法律文档 |
| Terms of Service | `/terms-of-service` | 法律文档 |
| Cookie Policy | `/cookie-policy` | 法律文档 |

## 设计决策

- **内容来源**：静态内容，硬编码在代码中
- **Contact 页面**：仅展示联系信息，无表单
- **FAQ 内容**：聚焦电商优惠类问题（如何使用优惠码、有效期等）
- **法律页面**：针对联盟营销和优惠聚合业务定制内容

## 整体美学方向

### 设计语言延续

沿用现有的 Premium Dark Luxury 风格：

- 深色背景配渐变发光（slate-950 → indigo-950）
- 精致的玻璃态效果（backdrop-blur, 半透明边框）
- 流畅的入场动画（fade-in, slide-in）
- 渐变色图标容器 + 阴影光晕

### 技术栈

- Next.js 16 App Router
- React 19 Server Components
- Tailwind CSS 4
- shadcn/ui 组件库
- Lucide React 图标
- next-intl 国际化

---

## 页面设计详情

### 1. About Us 页面

**路径**：`/[locale]/about/page.tsx`

**页面结构**：

```
┌─────────────────────────────────────────┐
│  HERO SECTION                           │
│  标语：Your Savings, Our Mission        │
│  副标题：简述 Ecommica 的使命           │
├─────────────────────────────────────────┤
│  OUR STORY                              │
│  左侧文字描述 + 右侧装饰性渐变色块      │
├─────────────────────────────────────────┤
│  STATS SECTION (数据统计)               │
│  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐   │
│  │50K+  │ │10K+  │ │500+  │ │99%   │   │
│  │Users │ │Stores│ │Brands│ │Valid │   │
│  └──────┘ └──────┘ └──────┘ └──────┘   │
├─────────────────────────────────────────┤
│  VALUES (我们的价值观)                  │
│  3列卡片：Transparency / Quality / Trust│
└─────────────────────────────────────────┘
```

**视觉特点**：

- Hero 使用与首页一致的深色渐变背景 + 发光效果
- Stats 数字使用大号渐变文字（amber → orange）
- Values 卡片悬浮时有微妙的渐变边框发光

---

### 2. Contact 页面

**路径**：`/[locale]/contact/page.tsx`

**页面结构**：

```
┌─────────────────────────────────────────┐
│  HERO SECTION (简洁版)                  │
│  标题：Get in Touch                     │
│  副标题：We'd love to hear from you     │
├─────────────────────────────────────────┤
│  CONTACT CARDS (3列网格)                │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐│
│  │ 📧 Email │ │ 💬 Social│ │ 📍 Info  ││
│  │ support@ │ │ Twitter  │ │ Business ││
│  │ ecommica │ │ Facebook │ │ Hours    ││
│  │ .com     │ │ Instagram│ │ Response ││
│  └──────────┘ └──────────┘ └──────────┘│
├─────────────────────────────────────────┤
│  FAQ CALLOUT (引导区块)                 │
│  Looking for answers? → View FAQ        │
└─────────────────────────────────────────┘
```

**视觉特点**：

- Hero 采用较浅的渐变（slate-900 → slate-950）
- Contact Cards 使用玻璃态卡片（bg-white/5, border-white/10, backdrop-blur）
- 每个卡片左上角有渐变色图标容器（Email=emerald, Social=violet, Info=amber）
- 悬浮时卡片轻微上移 + 边框发光
- FAQ Callout 使用虚线边框 + 柔和背景

---

### 3. FAQ 页面

**路径**：`/[locale]/faq/page.tsx`

**页面结构**：

```
┌─────────────────────────────────────────┐
│  HERO SECTION                           │
│  标题：Frequently Asked Questions       │
│  副标题：Everything you need to know    │
├─────────────────────────────────────────┤
│  FAQ CATEGORIES (分组手风琴)            │
│                                         │
│  ┌─ Using Coupons ──────────────────┐   │
│  │ ▼ How do I use a coupon code?    │   │
│  │ ▶ Why isn't my code working?     │   │
│  │ ▶ Do coupons expire?             │   │
│  └──────────────────────────────────┘   │
│                                         │
│  ┌─ Deals & Offers ─────────────────┐   │
│  │ ▶ How are deals verified?        │   │
│  │ ▶ How often are deals updated?   │   │
│  │ ▶ Can I submit a deal?           │   │
│  └──────────────────────────────────┘   │
│                                         │
│  ┌─ Account & Support ──────────────┐   │
│  │ ▶ Do I need an account?          │   │
│  │ ▶ How do I contact support?      │   │
│  └──────────────────────────────────┘   │
├─────────────────────────────────────────┤
│  CONTACT CTA                            │
│  Still have questions? → Contact Us     │
└─────────────────────────────────────────┘
```

**视觉特点**：

- 使用 shadcn/ui 的 Accordion 组件
- 分类标题带渐变色左边框（3px solid gradient）
- 展开/收起动画平滑过渡
- 展开状态的问题项有柔和的背景高亮

**FAQ 内容分类**（3组，约 8-10 个问题）：

1. **Using Coupons** - 如何使用优惠码、为什么不生效、有效期
2. **Deals & Offers** - 如何验证、更新频率、能否提交
3. **Account & Support** - 是否需要账户、如何联系

---

### 4-6. 法律页面（共用模板）

**路径**：
- `/[locale]/privacy-policy/page.tsx`
- `/[locale]/terms-of-service/page.tsx`
- `/[locale]/cookie-policy/page.tsx`

**页面结构**：

```
┌─────────────────────────────────────────┐
│  HERO SECTION (紧凑版)                  │
│  标题：Privacy Policy                   │
│  更新日期：Last updated: Jan 2026       │
├─────────────────────────────────────────┤
│  ┌────────────┬────────────────────┐    │
│  │ SIDEBAR    │  CONTENT           │    │
│  │ (目录导航) │                    │    │
│  │            │  1. Introduction   │    │
│  │ • Intro    │  We respect your   │    │
│  │ • Data     │  privacy and...    │    │
│  │ • Cookies  │                    │    │
│  │ • Rights   │  2. Data We Collect│    │
│  │ • Contact  │  When you visit... │    │
│  │            │                    │    │
│  │ (sticky)   │                    │    │
│  └────────────┴────────────────────┘    │
├─────────────────────────────────────────┤
│  RELATED LINKS                          │
│  [Privacy] [Terms] [Cookies]            │
└─────────────────────────────────────────┘
```

**视觉特点**：

- Hero 背景较浅（slate-900），保持专业严肃感
- 侧边目录使用 `sticky top-24`，滚动时固定
- 当前阅读章节高亮（左边框 + 文字变色）
- 正文使用 `prose` 样式，行高宽松便于阅读
- 章节标题带序号，使用渐变色数字
- 移动端：侧边栏变为顶部水平滚动目录

**内容结构**：

| 页面 | 核心章节 |
|------|----------|
| Privacy Policy | 数据收集、使用目的、第三方分享（联盟链接）、用户权利、联系方式 |
| Terms of Service | 服务描述、用户责任、免责声明（价格/库存）、知识产权、终止条款 |
| Cookie Policy | Cookie 类型、用途（分析/广告）、管理方式、第三方 Cookie |

---

## 共享组件

### PageHero 组件

用于所有页面的 Hero 区域，可配置：
- `title`: 页面标题
- `subtitle`: 副标题
- `variant`: 'dark' | 'light' (控制背景强度)
- `size`: 'default' | 'compact' (控制高度)

### LegalPageLayout 组件

用于 3 个法律页面的共享布局：
- 侧边目录导航
- 响应式处理
- 相关链接区域

---

## 文件结构

```
src/app/[locale]/
├── about/
│   └── page.tsx
├── contact/
│   └── page.tsx
├── faq/
│   └── page.tsx
├── privacy-policy/
│   └── page.tsx
├── terms-of-service/
│   └── page.tsx
└── cookie-policy/
    └── page.tsx

src/components/
├── layout/
│   ├── PageHero.tsx          # 共享 Hero 组件
│   └── LegalPageLayout.tsx   # 法律页面布局
└── faq/
    └── FaqAccordion.tsx      # FAQ 手风琴组件
```

---

## 国际化

所有用户可见文本需要添加到 `messages/en.json` 和 `messages/zh.json`：

```json
{
  "About": {
    "title": "About Us",
    "heroTitle": "Your Savings, Our Mission",
    "heroSubtitle": "...",
    ...
  },
  "Contact": { ... },
  "Faq": { ... },
  "Legal": { ... }
}
```

---

## 实施优先级

1. **PageHero 组件** - 基础共享组件
2. **About 页面** - 品牌核心页面
3. **Contact 页面** - 用户沟通入口
4. **FAQ 页面** - 自助服务
5. **LegalPageLayout 组件** - 法律页面共享布局
6. **Privacy Policy** → **Terms of Service** → **Cookie Policy**
