# Write Blog Skill 设计文档

## 概述

为 River 广告平台增加博客写作能力：
1. **前端 Markdown 渲染** - river-ecommica 博客详情页支持富 Markdown 渲染
2. **管理后台编辑器** - river-ui-admin 博客编辑器支持 Markdown 分栏预览
3. **Claude Code Skill** - `/write-blog` skill 自动生成博客内容

## 需求背景

- 参考效果：https://www.linkhaitao.com/blog/must-join-affiliate-marketing-events-in-2026
- 博客内容格式：Markdown
- 图片处理：AI 生成（OpenAI 兼容 API，支持 Grok/Gemini）
- 工作流程：全自动生成，输出 SQL 文件手动执行
- 语言：根据主题灵活选择（en/zh）
- 内容类型：全部支持（deal/review/tutorial/news）

---

## Part 1：前端 Markdown 渲染增强

### 当前问题

博客详情页 `river-ecommica/src/app/[locale]/blog/[slug]/page.tsx` 直接用 `dangerouslySetInnerHTML` 渲染，不支持 Markdown 特性。

### 解决方案

引入 `react-markdown` + `remark/rehype` 插件体系。

### 组件栈

| 包名 | 用途 |
|------|------|
| `react-markdown` | 核心 Markdown 渲染器 |
| `remark-gfm` | GitHub 风格 Markdown（表格、删除线、任务列表） |
| `remark-toc` | 自动生成目录 |
| `rehype-highlight` | 代码语法高亮 |
| `rehype-slug` | 标题锚点（配合 TOC） |
| `rehype-raw` | 允许嵌入原生 HTML（视频 iframe） |

### 新建组件

`river-ecommica/src/components/blog/MarkdownRenderer.tsx`

功能：
- 自动 TOC 目录（文章顶部或侧边栏）
- 自定义提示框（通过 blockquote 语法扩展）
  ```markdown
  > [!TIP]
  > 这是一个提示

  > [!WARNING]
  > 这是一个警告

  > [!INFO]
  > 这是一条信息
  ```
- 图片优化：用 Next.js `Image` 组件替代，支持懒加载
- 视频嵌入：YouTube/Vimeo iframe 支持
- 脚注支持

### 样式

复用 Tailwind Typography 插件 (`@tailwindcss/typography`)，通过 `prose` 类提供排版样式。

---

## Part 2：管理后台 Markdown 编辑器

### 方案

使用 `md-editor-v3`，Vue 3 生态成熟的 Markdown 编辑器，支持分栏预览。

### 新建组件

`river-ui-admin/src/components/Markdown/MarkdownEditor.vue`

功能：
- 左侧编辑、右侧实时预览
- 工具栏快捷按钮（标题、粗体、列表、链接、图片等）
- 图片上传集成（调用现有 FileService）
- 代码语法高亮
- 表格编辑辅助
- 全屏编辑模式

### 使用方式

```vue
<template>
  <MarkdownEditor v-model="formData.content" :upload-image="handleUploadImage" />
</template>
```

### 修改文件

- `river-ui-admin/src/views/river/blog/post/PostForm.vue` - content 字段使用新编辑器

---

## Part 3：`/write-blog` Skill 设计

### 调用方式

```bash
/write-blog <主题> [--lang <en|zh>] [--type <deal|review|tutorial|news>] [--featured]
```

**参数说明**：

| 参数 | 必填 | 默认值 | 说明 |
|------|------|--------|------|
| 主题 | 是 | - | 博客主题描述 |
| `--lang` | 否 | `en` | 语言：`en` 英文，`zh` 中文 |
| `--type` | 否 | `news` | 类型：`deal`/`review`/`tutorial`/`news` |
| `--featured` | 否 | false | 是否标记为精选 |

**示例**：

```bash
# 英文行业资讯
/write-blog Must-join affiliate marketing events in 2026 --lang en --type news

# 中文购物攻略
/write-blog 亚马逊 Prime Day 省钱攻略 --lang zh --type tutorial --featured

# 商家评测
/write-blog Honey browser extension review --type review
```

### 执行流程

```
1. 解析参数（主题、语言、类型、精选标记）
     ↓
2. 使用 WebSearch 搜索相关资料
   - 确保内容准确性和时效性
   - 收集事实、数据、引用来源
     ↓
3. 生成博客结构
   - title: SEO 友好的标题
   - slug: URL 友好格式（英文短横线）
   - excerpt: 150 字左右摘要
   - content: Markdown 正文
     - 包含 TOC 标记
     - 包含配图占位符 `![alt](GENERATE_IMAGE:prompt)`
   - metaTitle: SEO 标题（可与 title 不同）
   - metaDescription: SEO 描述
     ↓
4. AI 图片生成
   - 解析正文中的 `GENERATE_IMAGE:prompt` 占位符
   - 调用配置的 AI 图片生成 API
   - 封面图：1200x630 (OG 标准尺寸)
   - 文中配图：800x450 或 16:9
   - 保存图片到 docs/blogs/images/
   - 替换占位符为实际图片路径
     ↓
5. 生成输出文件
   - docs/blogs/{date}-{slug}.md - Markdown 源文件
   - docs/blogs/{date}-{slug}.sql - INSERT SQL 语句
     ↓
6. 输出完成提示
   - 显示生成的文件路径
   - 提示用户审核后执行 SQL
```

### 图片生成 Prompt 规范

Skill 会为不同场景生成合适的图片 prompt：

| 场景 | Prompt 模板 |
|------|-------------|
| 封面图 | `Professional blog cover image for "{title}", modern minimalist style, tech/business theme, no text` |
| 事件/活动 | `Conference or marketing event illustration, professional business setting, diverse attendees` |
| 产品评测 | `Product photography style, clean background, {product} showcase` |
| 购物攻略 | `Shopping and savings concept, money/discount theme, cheerful mood` |

### 输出文件格式

**Markdown 文件** (`docs/blogs/2026-01-28-must-join-affiliate-events-2026.md`):

```markdown
---
title: Must-Join Affiliate Marketing Events in 2026
slug: must-join-affiliate-events-2026
excerpt: Discover the top affiliate marketing conferences and events...
type: news
lang: en
featured: false
metaTitle: Top Affiliate Marketing Events 2026 | Ecommica
metaDescription: Complete guide to must-attend affiliate marketing events in 2026...
coverImage: /blogs/images/cover-must-join-affiliate-events-2026.png
---

## Table of Contents

## Introduction

The affiliate marketing industry continues to grow...

![Affiliate Summit West 2026](/blogs/images/affiliate-summit-west-2026.png)

## 1. Affiliate Summit West 2026

**Date:** January 27-29, 2026
**Location:** Las Vegas, NV

...
```

**SQL 文件** (`docs/blogs/2026-01-28-must-join-affiliate-events-2026.sql`):

```sql
-- Blog Post: Must-Join Affiliate Marketing Events in 2026
-- Generated: 2026-01-28
-- Review before executing!

INSERT INTO river_blog_post (
    id, tenant_id, author_id, title, slug, content, excerpt,
    cover_image, type, status, published_at,
    meta_title, meta_description, view_count, featured,
    creator, create_time, updater, update_time, deleted
) VALUES (
    nextval('river_blog_post_seq'),
    1,  -- tenant_id: 默认租户
    1,  -- author_id: 默认作者，需根据实际情况修改
    'Must-Join Affiliate Marketing Events in 2026',
    'must-join-affiliate-events-2026',
    E'## Table of Contents\n\n## Introduction\n\nThe affiliate marketing industry...',
    'Discover the top affiliate marketing conferences and events...',
    'https://your-cdn.com/blogs/images/cover-must-join-affiliate-events-2026.png',
    4,  -- type: news
    1,  -- status: published
    NOW(),
    'Top Affiliate Marketing Events 2026 | Ecommica',
    'Complete guide to must-attend affiliate marketing events in 2026...',
    0,
    false,
    'claude', NOW(), 'claude', NOW(), 0
);
```

---

## Part 4：配置

### 前端依赖 (river-ecommica)

```json
{
  "dependencies": {
    "react-markdown": "^9.0.0",
    "remark-gfm": "^4.0.0",
    "rehype-highlight": "^7.0.0",
    "rehype-slug": "^6.0.0",
    "rehype-raw": "^7.0.0"
  }
}
```

### 管理后台依赖 (river-ui-admin)

```json
{
  "dependencies": {
    "md-editor-v3": "^4.x"
  }
}
```

### Skill 环境变量

在项目根目录 `.env` 或 CI/CD 环境中配置：

```bash
# AI 图片生成 API（OpenAI 兼容格式）
IMAGE_GEN_API_ENDPOINT=https://your-proxy.com/v1
IMAGE_GEN_API_KEY=sk-xxx
IMAGE_GEN_MODEL=grok-2-image
# 或 gemini-2.0-flash-exp-image

# 图片保存路径（相对于项目根目录）
BLOG_OUTPUT_DIR=docs/blogs
```

---

## Part 5：文件清单

### 新增文件

| 文件路径 | 说明 |
|---------|------|
| `.claude/skills/write-blog/SKILL.md` | Skill 定义文件 |
| `river-ecommica/src/components/blog/MarkdownRenderer.tsx` | Markdown 渲染组件 |
| `river-ui-admin/src/components/Markdown/MarkdownEditor.vue` | Markdown 编辑器组件 |
| `docs/blogs/` | 博客输出目录 |
| `docs/blogs/images/` | 博客图片目录 |

### 修改文件

| 文件路径 | 说明 |
|---------|------|
| `river-ecommica/src/app/[locale]/blog/[slug]/page.tsx` | 使用 MarkdownRenderer |
| `river-ecommica/package.json` | 新增 Markdown 依赖 |
| `river-ui-admin/src/views/river/blog/post/PostForm.vue` | content 使用 MarkdownEditor |
| `river-ui-admin/package.json` | 新增 md-editor-v3 依赖 |

---

## Part 6：后续扩展（可选）

以下功能不在本次实现范围，但设计上预留扩展空间：

1. **博客系列** - 支持多篇博客组成系列
2. **标签系统** - 博客标签分类
3. **相关推荐** - 基于内容相似度推荐相关博客
4. **SEO 自动优化** - 自动生成 canonical URL、hreflang 标签
5. **定时发布** - 支持设置发布时间

---

## 验收标准

1. [ ] 前端博客详情页正确渲染 Markdown（含 TOC、代码高亮、提示框）
2. [ ] 管理后台博客编辑器支持 Markdown 分栏预览
3. [ ] `/write-blog` skill 能根据主题生成完整博客
4. [ ] 生成的 SQL 可直接执行，博客正常显示
5. [ ] AI 生成的图片质量可接受
