---
name: write-blog
description: Auto-generate blog posts with images for deals.ecommica.com. Use when user wants to write a blog post.
---

# Write Blog Skill

自动生成博客文章，包括配图，输出 Markdown 和 SQL 文件。

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

### Step 1: 解析参数

从用户输入中提取：
- `topic`：博客主题（必填）
- `lang`：语言（en/zh），默认 en
- `type`：类型（deal/review/tutorial/news），默认 news
- `featured`：是否精选，默认 false

如果用户没有明确指定参数，从主题语言和内容推断合理的默认值。

### Step 2: 搜索资料

使用 WebSearch 工具搜索相关资料：
- 搜索 3-5 个相关查询，覆盖不同角度
- 收集事实、数据、引用来源
- 确保内容准确性和时效性
- 记录来源 URL 以便在文末引用

### Step 3: 生成博客内容

生成以下字段：

| 字段 | 要求 |
|------|------|
| `title` | SEO 友好，包含关键词，60 字符以内 |
| `slug` | URL 友好格式，英文短横线（如 `must-join-affiliate-events-2026`） |
| `excerpt` | 150 字左右摘要，概括文章核心价值 |
| `content` | Markdown 正文（见下方结构要求） |
| `metaTitle` | SEO 标题，60 字符以内，可与 title 不同 |
| `metaDescription` | SEO 描述，160 字符以内 |

**内容结构要求**：

```markdown
## Table of Contents

## 引言标题

引言段落（1-2 段，说明文章价值和读者收益）

## 正文 Section 1

正文内容...

![配图描述](IMAGE:关键词描述)

## 正文 Section 2

...

## 正文 Section N

...

## 总结 / Conclusion

总结段落 + CTA（引导读者行动）
```

**写作规范**：
- 使用 h2（`##`）分节，h3（`###`）分小节
- 每节有 2-3 段内容
- 适当使用列表（`-`）、表格、引用（`>`）
- 使用 `**粗体**` 强调关键信息
- 文末有总结或行动号召（CTA）
- 保持段落短小（3-5 句）
- 每 2-3 个 section 插入一张配图

**配图占位符语法**：
```
![图片描述](IMAGE:搜索关键词)
```

### Step 4: 获取配图

**优先级：免费图片 > AI 生成**

#### 4.1 搜索免费图片（优先）

使用 WebSearch 搜索免费商用图片：

```
site:unsplash.com {关键词}
site:pexels.com {关键词}
```

**免费图片源**：
| 网站 | 特点 |
|------|------|
| Unsplash | 高质量摄影图片，免费商用 |
| Pexels | 多样化图片和视频，免费商用 |
| Pixabay | 大量免费素材，无需署名 |

从搜索结果中提取图片 URL，优先选择：
- 高分辨率（1200px+ 宽度）
- 与主题高度相关
- 专业、现代风格

#### 4.2 AI 生成图片（备选）

仅当免费图片不满足需求时使用。

读取项目根目录 `.env` 文件获取配置：
- `IMAGE_GEN_API_ENDPOINT`：API 端点
- `IMAGE_GEN_API_KEY`：API 密钥
- `IMAGE_GEN_MODEL`：模型名称

使用 chat/completions 端点生成图片：
```bash
curl -X POST "$IMAGE_GEN_API_ENDPOINT/v1/chat/completions" \
  -H "Authorization: Bearer $IMAGE_GEN_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "$IMAGE_GEN_MODEL",
    "messages": [
      {
        "role": "user",
        "content": "Generate an image: {prompt}"
      }
    ]
  }'
```

从响应中提取图片 URL（格式：`![...](URL)`），下载到 `docs/blogs/images/`。

#### 4.3 封面图

- 优先搜索免费图片：`{topic} cover professional`
- 备选 AI 生成：`Professional blog cover for "{title}", modern minimalist style, no text overlay`
- 保存为 `docs/blogs/images/cover-{slug}.png`（或使用外链 URL）

### Step 5: 输出文件

**1. Markdown 文件**（`docs/blogs/{YYYY-MM-DD}-{slug}.md`）：

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
coverImage: {cover_image_url}
generatedAt: {ISO datetime}
---

{content with image URLs}
```

**2. SQL 文件**（`docs/blogs/{YYYY-MM-DD}-{slug}.sql`）：

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
    1,  -- tenant_id: 默认租户，需根据实际情况修改
    1,  -- author_id: 默认作者，需根据实际情况修改
    '{title}',
    '{slug}',
    E'{escaped_markdown_content}',
    '{excerpt}',
    '{cover_image_url}',
    {type_int},  -- 1:deal, 2:review, 3:tutorial, 4:news
    1,  -- status: published
    NOW(),
    '{metaTitle}',
    '{metaDescription}',
    0,
    {featured_bool},
    'claude', NOW(), 'claude', NOW(), 0
);
```

**类型映射**：
| 类型 | 数值 |
|------|------|
| deal | 1 |
| review | 2 |
| tutorial | 3 |
| news | 4 |

**SQL 转义规则**：
- 单引号 `'` → `''`
- 反斜杠 `\` → `\\`
- 使用 `E'...'` 格式支持转义字符

### Step 6: 完成提示

输出完成信息：

```
博客生成完成！

📄 Markdown: docs/blogs/{date}-{slug}.md
🗃️ SQL: docs/blogs/{date}-{slug}.sql
🖼️ 图片来源: [免费图片/AI生成]

下一步：
1. 审核 Markdown 文件内容
2. 如需修改，直接编辑 Markdown 文件
3. 确认图片 URL 可访问（外链）或上传到 CDN（本地文件）
4. 执行 SQL 插入数据库
```

## 错误处理

| 场景 | 处理 |
|------|------|
| 免费图片搜索无结果 | 尝试 AI 生成 |
| AI 生成 API 未配置 | 保留占位符，提示用户手动添加图片 |
| AI 生成失败 | 保留占位符，输出警告 |
| WebSearch 失败 | 基于已有知识生成，标注需人工校验 |
| 参数错误 | 提示正确用法和示例 |

## 内容质量要求

1. **准确性** - 使用 WebSearch 获取最新信息，不编造事实
2. **原创性** - 重新组织表达，不直接复制搜索结果
3. **SEO 友好** - 标题包含关键词，自然分布关键词，合理使用 h2/h3
4. **可读性** - 短段落（3-5 句），清晰结构，适当使用列表和表格
5. **行动号召** - 文末引导用户采取行动（如访问网站、注册、分享）
6. **专业性** - 符合联盟营销/电商优惠领域的专业标准
7. **图片质量** - 使用高质量、专业的配图，优先免费商用图片
