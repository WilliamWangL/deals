# Write Blog Skill 实现计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 实现博客 Markdown 渲染、编辑器和 /write-blog skill，让 Claude Code 能够自动撰写博客

**Architecture:** 前端使用 react-markdown 渲染，管理后台使用 md-editor-v3 编辑，skill 通过 WebSearch 收集资料后生成 Markdown 和 SQL

**Tech Stack:** react-markdown, remark-gfm, rehype-highlight, md-editor-v3, OpenAI-compatible image API

---

## Task 1: 前端 Markdown 渲染组件

**Files:**
- Modify: `river-ecommica/package.json` - 添加依赖
- Create: `river-ecommica/src/components/blog/MarkdownRenderer.tsx` - 渲染组件
- Create: `river-ecommica/src/components/blog/MarkdownRenderer.css` - 样式

### Step 1.1: 安装 Markdown 依赖

Run:
```bash
cd river-ecommica && pnpm add react-markdown remark-gfm rehype-highlight rehype-slug rehype-raw
```

Expected: 依赖安装成功，package.json 更新

### Step 1.2: 创建 MarkdownRenderer 组件

Create `river-ecommica/src/components/blog/MarkdownRenderer.tsx`:

```tsx
'use client';

import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import rehypeHighlight from 'rehype-highlight';
import rehypeSlug from 'rehype-slug';
import rehypeRaw from 'rehype-raw';
import Image from 'next/image';
import { ComponentProps } from 'react';
import './MarkdownRenderer.css';

interface MarkdownRendererProps {
  content: string;
  className?: string;
}

// 自定义提示框组件
function Callout({ type, children }: { type: 'tip' | 'warning' | 'info'; children: React.ReactNode }) {
  const styles = {
    tip: 'bg-emerald-50 border-emerald-500 text-emerald-800',
    warning: 'bg-amber-50 border-amber-500 text-amber-800',
    info: 'bg-blue-50 border-blue-500 text-blue-800',
  };
  const icons = {
    tip: '💡',
    warning: '⚠️',
    info: 'ℹ️',
  };
  return (
    <div className={`callout ${styles[type]} border-l-4 p-4 my-4 rounded-r-lg`}>
      <span className="mr-2">{icons[type]}</span>
      {children}
    </div>
  );
}

// 解析 blockquote 中的 [!TIP], [!WARNING], [!INFO] 语法
function parseCallout(text: string): { type: 'tip' | 'warning' | 'info'; content: string } | null {
  const match = text.match(/^\[!(TIP|WARNING|INFO)\]\s*([\s\S]*)/i);
  if (match) {
    return {
      type: match[1].toLowerCase() as 'tip' | 'warning' | 'info',
      content: match[2].trim(),
    };
  }
  return null;
}

export function MarkdownRenderer({ content, className = '' }: MarkdownRendererProps) {
  return (
    <div className={`markdown-renderer ${className}`}>
      <ReactMarkdown
        remarkPlugins={[remarkGfm]}
        rehypePlugins={[rehypeHighlight, rehypeSlug, rehypeRaw]}
        components={{
          // 自定义图片渲染，使用 Next.js Image
          img: ({ src, alt, ...props }) => {
            if (!src) return null;
            // 外部图片使用普通 img 标签
            if (src.startsWith('http')) {
              return (
                <span className="block my-6">
                  <img
                    src={src}
                    alt={alt || ''}
                    className="rounded-xl max-w-full h-auto mx-auto"
                    loading="lazy"
                  />
                  {alt && <span className="block text-center text-sm text-muted-foreground mt-2">{alt}</span>}
                </span>
              );
            }
            // 本地图片使用 Next.js Image
            return (
              <span className="block my-6">
                <Image
                  src={src}
                  alt={alt || ''}
                  width={800}
                  height={450}
                  className="rounded-xl"
                />
                {alt && <span className="block text-center text-sm text-muted-foreground mt-2">{alt}</span>}
              </span>
            );
          },
          // 自定义 blockquote 支持提示框语法
          blockquote: ({ children, ...props }) => {
            // 提取文本内容检查是否是 callout
            const textContent = children?.toString() || '';
            const callout = parseCallout(textContent);
            if (callout) {
              return <Callout type={callout.type}>{callout.content}</Callout>;
            }
            return (
              <blockquote className="border-l-4 border-gray-300 pl-4 my-4 italic text-gray-600" {...props}>
                {children}
              </blockquote>
            );
          },
          // 自定义链接，外部链接新窗口打开
          a: ({ href, children, ...props }) => {
            const isExternal = href?.startsWith('http');
            return (
              <a
                href={href}
                target={isExternal ? '_blank' : undefined}
                rel={isExternal ? 'noopener noreferrer' : undefined}
                className="text-primary hover:underline"
                {...props}
              >
                {children}
              </a>
            );
          },
          // 自定义代码块
          pre: ({ children, ...props }) => (
            <pre className="bg-gray-900 text-gray-100 rounded-xl p-4 my-4 overflow-x-auto" {...props}>
              {children}
            </pre>
          ),
          // 自定义表格
          table: ({ children, ...props }) => (
            <div className="overflow-x-auto my-6">
              <table className="min-w-full border-collapse border border-gray-200 rounded-lg" {...props}>
                {children}
              </table>
            </div>
          ),
          th: ({ children, ...props }) => (
            <th className="bg-gray-50 border border-gray-200 px-4 py-2 text-left font-semibold" {...props}>
              {children}
            </th>
          ),
          td: ({ children, ...props }) => (
            <td className="border border-gray-200 px-4 py-2" {...props}>
              {children}
            </td>
          ),
        }}
      >
        {content}
      </ReactMarkdown>
    </div>
  );
}
```

### Step 1.3: 创建样式文件

Create `river-ecommica/src/components/blog/MarkdownRenderer.css`:

```css
.markdown-renderer {
  @apply text-foreground leading-relaxed;
}

.markdown-renderer h1 {
  @apply text-3xl font-bold font-display mt-8 mb-4;
}

.markdown-renderer h2 {
  @apply text-2xl font-bold font-display mt-8 mb-4 pb-2 border-b border-gray-200;
}

.markdown-renderer h3 {
  @apply text-xl font-semibold mt-6 mb-3;
}

.markdown-renderer h4 {
  @apply text-lg font-semibold mt-4 mb-2;
}

.markdown-renderer p {
  @apply my-4;
}

.markdown-renderer ul {
  @apply list-disc list-inside my-4 space-y-2;
}

.markdown-renderer ol {
  @apply list-decimal list-inside my-4 space-y-2;
}

.markdown-renderer li {
  @apply pl-2;
}

.markdown-renderer code:not(pre code) {
  @apply bg-gray-100 px-1.5 py-0.5 rounded text-sm font-mono text-pink-600;
}

.markdown-renderer hr {
  @apply my-8 border-gray-200;
}

.markdown-renderer strong {
  @apply font-semibold;
}

/* 代码高亮主题覆盖 */
.markdown-renderer pre code.hljs {
  @apply bg-transparent p-0;
}

/* 提示框样式 */
.markdown-renderer .callout {
  @apply text-sm;
}

.markdown-renderer .callout p {
  @apply my-0;
}
```

### Step 1.4: 导出组件

Create `river-ecommica/src/components/blog/index.ts`:

```typescript
export { MarkdownRenderer } from './MarkdownRenderer';
export { BlogPagination } from './BlogPagination';
```

### Step 1.5: 验证构建

Run:
```bash
cd river-ecommica && pnpm build
```

Expected: 构建成功，无错误

### Step 1.6: Commit

```bash
git add river-ecommica/package.json river-ecommica/pnpm-lock.yaml river-ecommica/src/components/blog/
git commit -m "feat(ecommica): add MarkdownRenderer component with rich features

- Support GFM (tables, strikethrough, task lists)
- Support callout boxes ([!TIP], [!WARNING], [!INFO])
- Code syntax highlighting with rehype-highlight
- Optimized image rendering with Next.js Image
- External links open in new tab"
```

---

## Task 2: 更新博客详情页

**Files:**
- Modify: `river-ecommica/src/app/[locale]/blog/[slug]/page.tsx`

### Step 2.1: 更新博客详情页使用 MarkdownRenderer

Modify `river-ecommica/src/app/[locale]/blog/[slug]/page.tsx` 第 164-169 行:

将:
```tsx
<div className="prose prose-lg prose-slate max-w-none prose-headings:font-display prose-a:text-primary prose-img:rounded-xl">
  {post.content ? (
    <div dangerouslySetInnerHTML={{ __html: post.content.replace(/\n/g, '<br/>') }} />
  ) : (
    <p className="text-xl text-gray-600 leading-relaxed">{post.excerpt}</p>
  )}
</div>
```

替换为:
```tsx
{post.content ? (
  <MarkdownRenderer content={post.content} className="prose prose-lg prose-slate max-w-none" />
) : (
  <p className="text-xl text-gray-600 leading-relaxed">{post.excerpt}</p>
)}
```

### Step 2.2: 添加 import

在文件顶部添加:
```tsx
import { MarkdownRenderer } from '@/components/blog';
```

### Step 2.3: 验证

Run:
```bash
cd river-ecommica && pnpm dev
```

访问 http://localhost:3000/en/blog/[任意slug] 验证 Markdown 渲染正常

### Step 2.4: Commit

```bash
git add river-ecommica/src/app/[locale]/blog/[slug]/page.tsx
git commit -m "feat(ecommica): use MarkdownRenderer in blog detail page

Replace dangerouslySetInnerHTML with MarkdownRenderer component
for proper Markdown rendering with syntax highlighting and callouts"
```

---

## Task 3: 管理后台 Markdown 编辑器

**Files:**
- Modify: `river-ui-admin/package.json` - 添加 md-editor-v3
- Create: `river-ui-admin/src/components/MarkdownEditor/index.vue` - 编辑器组件
- Create: `river-ui-admin/src/components/MarkdownEditor/index.ts` - 导出

### Step 3.1: 安装 md-editor-v3

Run:
```bash
cd river-ui-admin && pnpm add md-editor-v3
```

Expected: 依赖安装成功

### Step 3.2: 创建 MarkdownEditor 组件

Create `river-ui-admin/src/components/MarkdownEditor/index.vue`:

```vue
<template>
  <div class="markdown-editor-wrapper">
    <MdEditor
      v-model="content"
      :language="language"
      :theme="isDark ? 'dark' : 'light'"
      :preview="preview"
      :toolbars="toolbars"
      :style="{ height }"
      @onUploadImg="handleUploadImg"
      @onChange="handleChange"
    />
  </div>
</template>

<script setup lang="ts">
import { MdEditor, ToolbarNames } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import { getAccessToken } from '@/utils/auth'

defineOptions({ name: 'MarkdownEditor' })

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  height: {
    type: String,
    default: '500px'
  },
  preview: {
    type: Boolean,
    default: true
  },
  language: {
    type: String as PropType<'zh-CN' | 'en-US'>,
    default: 'zh-CN'
  }
})

const emit = defineEmits(['update:modelValue', 'change'])

// 双向绑定
const content = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

// 主题
const isDark = computed(() => {
  // 可以从 store 或 CSS 变量获取当前主题
  return document.documentElement.classList.contains('dark')
})

// 工具栏配置
const toolbars: ToolbarNames[] = [
  'bold',
  'underline',
  'italic',
  'strikeThrough',
  '-',
  'title',
  'sub',
  'sup',
  'quote',
  'unorderedList',
  'orderedList',
  'task',
  '-',
  'codeRow',
  'code',
  'link',
  'image',
  'table',
  '-',
  'revoke',
  'next',
  '=',
  'preview',
  'fullscreen'
]

// 图片上传处理
const handleUploadImg = async (
  files: File[],
  callback: (urls: string[]) => void
) => {
  const results: string[] = []

  for (const file of files) {
    try {
      const formData = new FormData()
      formData.append('file', file)

      const response = await fetch(
        `${import.meta.env.VITE_API_URL}/admin-api/infra/file/upload`,
        {
          method: 'POST',
          headers: {
            Authorization: `Bearer ${getAccessToken()}`
          },
          body: formData
        }
      )

      const data = await response.json()
      if (data.code === 0 && data.data) {
        results.push(data.data)
      }
    } catch (error) {
      console.error('Upload failed:', error)
    }
  }

  callback(results)
}

// 内容变化回调
const handleChange = (val: string) => {
  emit('change', val)
}
</script>

<style scoped>
.markdown-editor-wrapper {
  border: 1px solid var(--el-border-color);
  border-radius: 4px;
  overflow: hidden;
}

.markdown-editor-wrapper :deep(.md-editor) {
  --md-bk-color: var(--el-bg-color);
}
</style>
```

### Step 3.3: 创建导出文件

Create `river-ui-admin/src/components/MarkdownEditor/index.ts`:

```typescript
import MarkdownEditor from './index.vue'

export { MarkdownEditor }
export default MarkdownEditor
```

### Step 3.4: 注册全局组件（可选）

如果需要全局使用，在 `river-ui-admin/src/components/index.ts` 中添加:

```typescript
export { MarkdownEditor } from './MarkdownEditor'
```

### Step 3.5: 验证编辑器

创建临时测试页面或在已有页面测试组件渲染

Run:
```bash
cd river-ui-admin && pnpm dev
```

### Step 3.6: Commit

```bash
git add river-ui-admin/package.json river-ui-admin/pnpm-lock.yaml river-ui-admin/src/components/MarkdownEditor/
git commit -m "feat(admin): add MarkdownEditor component with split preview

- Use md-editor-v3 for Markdown editing
- Support image upload to existing FileService
- Configurable toolbar and theme
- Split preview mode for real-time feedback"
```

---

## Task 4: 更新 PostForm 使用 MarkdownEditor

**Files:**
- Modify: `river-ui-admin/src/views/river/blog/post/PostForm.vue`

### Step 4.1: 替换 textarea 为 MarkdownEditor

Modify `river-ui-admin/src/views/river/blog/post/PostForm.vue`:

1. 添加 import (在 script 顶部):
```typescript
import { MarkdownEditor } from '@/components/MarkdownEditor'
```

2. 替换第 84-91 行的 content 表单项:

将:
```vue
<el-form-item label="内容" prop="content">
  <el-input
    v-model="formData.content"
    type="textarea"
    :rows="10"
    placeholder="请输入文章内容（支持Markdown）"
  />
</el-form-item>
```

替换为:
```vue
<el-form-item label="内容" prop="content">
  <MarkdownEditor
    v-model="formData.content"
    height="400px"
    :preview="true"
  />
</el-form-item>
```

3. 调整弹窗宽度（第 2 行）:

将 `width="900px"` 改为 `width="1200px"`（编辑器需要更大空间）

### Step 4.2: 验证

Run:
```bash
cd river-ui-admin && pnpm dev
```

访问博客文章管理页面，点击新增或编辑，验证 Markdown 编辑器正常工作

### Step 4.3: Commit

```bash
git add river-ui-admin/src/views/river/blog/post/PostForm.vue
git commit -m "feat(admin): use MarkdownEditor in blog PostForm

Replace textarea with MarkdownEditor for better Markdown editing
experience with split preview and toolbar"
```

---

## Task 5: 创建 write-blog Skill

**Files:**
- Create: `.claude/skills/write-blog/SKILL.md`
- Create: `docs/blogs/.gitkeep`
- Create: `docs/blogs/images/.gitkeep`

### Step 5.1: 创建输出目录

Run:
```bash
mkdir -p docs/blogs/images
touch docs/blogs/.gitkeep docs/blogs/images/.gitkeep
```

### Step 5.2: 创建 Skill 文件

Create `.claude/skills/write-blog/SKILL.md`:

```markdown
---
name: write-blog
description: Auto-generate blog posts with AI-generated images for deals.ecommica.com
---

# Write Blog Skill

自动生成博客文章，包括 AI 配图，输出 Markdown 和 SQL 文件。

## 调用方式

```
/write-blog <主题> [--lang <en|zh>] [--type <deal|review|tutorial|news>] [--featured]
```

## 参数

| 参数 | 必填 | 默认值 | 说明 |
|------|------|--------|------|
| 主题 | 是 | - | 博客主题描述 |
| `--lang` | 否 | `en` | 语言：`en` 英文，`zh` 中文 |
| `--type` | 否 | `news` | 类型：`deal`/`review`/`tutorial`/`news` |
| `--featured` | 否 | false | 是否标记为精选 |

## 示例

```bash
# 英文行业资讯
/write-blog Must-join affiliate marketing events in 2026 --lang en --type news

# 中文购物攻略
/write-blog 亚马逊 Prime Day 省钱攻略 --lang zh --type tutorial --featured

# 商家评测
/write-blog Honey browser extension review --type review
```

## 执行流程

**Step 1: 解析参数**

从用户输入中提取：
- `topic`: 博客主题
- `lang`: 语言 (en/zh)，默认 en
- `type`: 类型 (deal/review/tutorial/news)，默认 news
- `featured`: 是否精选，默认 false

**Step 2: 搜索资料**

使用 WebSearch 工具搜索相关资料：
- 搜索 3-5 个相关查询
- 收集事实、数据、引用来源
- 确保内容准确性和时效性

**Step 3: 生成博客内容**

生成以下字段：
- `title`: SEO 友好的标题
- `slug`: URL 友好格式（英文短横线，如 `must-join-affiliate-events-2026`）
- `excerpt`: 150 字左右摘要
- `content`: Markdown 正文
- `metaTitle`: SEO 标题（60 字符以内）
- `metaDescription`: SEO 描述（160 字符以内）
- `coverImage`: 封面图路径（待生成）

**内容结构要求**：
- 开头有引言段落
- 使用 h2 分节
- 每节有 2-3 段内容
- 适当使用列表、表格、引用
- 文末有总结或 CTA

**配图占位符语法**：
```markdown
![图片描述](GENERATE_IMAGE:详细的图片生成prompt)
```

**Step 4: 生成配图**

读取环境变量配置：
- `IMAGE_GEN_API_ENDPOINT`: API 端点
- `IMAGE_GEN_API_KEY`: API 密钥
- `IMAGE_GEN_MODEL`: 模型名称

对于每个 `GENERATE_IMAGE:` 占位符：
1. 提取 prompt
2. 调用图片生成 API
3. 保存图片到 `docs/blogs/images/{slug}-{index}.png`
4. 替换占位符为相对路径

**封面图生成**：
- 尺寸：1200x630（OG 标准）
- Prompt 模板：`Professional blog cover for "{title}", modern minimalist style, no text, {type}-related imagery`

**Step 5: 输出文件**

生成两个文件：

**1. Markdown 文件** (`docs/blogs/{date}-{slug}.md`):

```markdown
---
title: {title}
slug: {slug}
excerpt: {excerpt}
type: {type}
lang: {lang}
featured: {featured}
metaTitle: {metaTitle}
metaDescription: {metaDescription}
coverImage: {coverImage}
generatedAt: {ISO datetime}
---

{content}
```

**2. SQL 文件** (`docs/blogs/{date}-{slug}.sql`):

```sql
-- Blog Post: {title}
-- Generated: {date}
-- Review before executing!

INSERT INTO river_blog_post (
    id, tenant_id, author_id, title, slug, content, excerpt,
    cover_image, type, status, published_at,
    meta_title, meta_description, view_count, featured,
    creator, create_time, updater, update_time, deleted
) VALUES (
    nextval('river_blog_post_seq'),
    1,  -- tenant_id
    1,  -- author_id: 需根据实际情况修改
    '{title}',
    '{slug}',
    E'{escaped_content}',
    '{excerpt}',
    '{cover_image_url}',
    {type_int},  -- 1:deal, 2:review, 3:tutorial, 4:news
    1,  -- status: published
    NOW(),
    '{metaTitle}',
    '{metaDescription}',
    0,
    {featured},
    'claude', NOW(), 'claude', NOW(), 0
);
```

**类型映射**：
- deal: 1
- review: 2
- tutorial: 3
- news: 4

**Step 6: 完成提示**

输出：
```
✅ 博客生成完成！

📄 Markdown: docs/blogs/{date}-{slug}.md
📊 SQL: docs/blogs/{date}-{slug}.sql
🖼️ 图片: docs/blogs/images/{slug}-*.png

请审核内容后：
1. 上传图片到 CDN 并更新 SQL 中的 cover_image URL
2. 执行 SQL 插入数据库
```

## 配置要求

在项目根目录 `.env` 或环境变量中配置：

```bash
# AI 图片生成 API（OpenAI 兼容格式）
IMAGE_GEN_API_ENDPOINT=https://your-proxy.com/v1
IMAGE_GEN_API_KEY=sk-xxx
IMAGE_GEN_MODEL=grok-2-image
```

## 内容质量要求

1. **准确性** - 使用 WebSearch 获取最新信息
2. **原创性** - 不要直接复制搜索结果，重新组织表达
3. **SEO 友好** - 标题包含关键词，自然分布关键词
4. **可读性** - 短段落，清晰结构，适当使用列表
5. **行动号召** - 文末引导用户采取行动

## 错误处理

- 图片生成失败：保留占位符，输出警告
- WebSearch 失败：基于已有知识生成，标注需人工校验
- 参数错误：提示正确用法
```

### Step 5.3: Commit

```bash
git add .claude/skills/write-blog/ docs/blogs/
git commit -m "feat: add /write-blog skill for auto blog generation

- Support multiple languages (en/zh)
- Support all blog types (deal/review/tutorial/news)
- AI image generation integration
- Output Markdown and SQL files for review"
```

---

## Task 6: 端到端测试

### Step 6.1: 测试前端渲染

1. 在数据库中插入一条包含 Markdown 内容的博客：

```sql
UPDATE river_blog_post
SET content = E'## Introduction\n\nThis is a **test** post with:\n\n- Item 1\n- Item 2\n\n> [!TIP]\n> This is a tip callout!\n\n```javascript\nconsole.log("Hello World");\n```'
WHERE id = 1;
```

2. 访问 http://localhost:3000/en/blog/[slug] 验证渲染

### Step 6.2: 测试管理后台编辑器

1. 访问管理后台博客管理
2. 点击编辑
3. 验证 Markdown 编辑器加载
4. 验证分栏预览
5. 验证图片上传

### Step 6.3: 测试 Skill

Run:
```bash
/write-blog Top 5 Cashback Apps for 2026 --lang en --type review
```

验证输出文件正确生成

### Step 6.4: Final Commit

```bash
git add -A
git commit -m "feat: complete write-blog feature implementation

- Frontend Markdown rendering with react-markdown
- Admin Markdown editor with md-editor-v3
- /write-blog skill for auto blog generation
- AI image generation integration"
```

---

## 验收清单

- [ ] `pnpm build` 在 river-ecommica 成功
- [ ] `pnpm build:local` 在 river-ui-admin 成功
- [ ] 博客详情页正确渲染 Markdown（标题、列表、代码、表格、提示框）
- [ ] 管理后台编辑器分栏预览正常
- [ ] 管理后台图片上传正常
- [ ] `/write-blog` skill 生成正确的 Markdown 和 SQL 文件
