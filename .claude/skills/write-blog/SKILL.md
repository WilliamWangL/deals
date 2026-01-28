---
name: write-blog
description: Auto-generate blog posts with AI-generated images for deals.ecommica.com. Use when user wants to write a blog post.
---

# Write Blog Skill

自动生成博客文章,包括 AI 配图,输出 Markdown 和 SQL 文件。

## 调用方式

```
/write-blog <主题> [--lang <en|zh>] [--type <deal|review|tutorial|news>] [--featured]
```

## 参数

| 参数 | 必填 | 默认值 | 说明 |
|------|------|--------|------|
| 主题 | 是 | - | 博客主题描述 |
| `--lang` | 否 | `en` | 语言:`en` 英文,`zh` 中文 |
| `--type` | 否 | `news` | 类型:`deal`/`review`/`tutorial`/`news` |
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

从用户输入中提取:
- `topic`: 博客主题(必填)
- `lang`: 语言 (en/zh),默认 en
- `type`: 类型 (deal/review/tutorial/news),默认 news
- `featured`: 是否精选,默认 false

如果用户没有明确指定参数,从主题语言和内容推断合理的默认值。

### Step 2: 搜索资料

使用 WebSearch 工具搜索相关资料:
- 搜索 3-5 个相关查询,覆盖不同角度
- 收集事实、数据、引用来源
- 确保内容准确性和时效性
- 记录来源 URL 以便在文末引用

### Step 3: 生成博客内容

生成以下字段:

| 字段 | 要求 |
|------|------|
| `title` | SEO 友好,包含关键词,60 字符以内 |
| `slug` | URL 友好格式,英文短横线(如 `must-join-affiliate-events-2026`) |
| `excerpt` | 150 字左右摘要,概括文章核心价值 |
| `content` | Markdown 正文(见下方结构要求) |
| `metaTitle` | SEO 标题,60 字符以内,可与 title 不同 |
| `metaDescription` | SEO 描述,160 字符以内 |

**内容结构要求**:

```markdown
## Table of Contents

## 引言标题

引言段落(1-2 段,说明文章价值和读者收益)

## 正文 Section 1

正文内容...

![配图描述](GENERATE_IMAGE:详细的图片生成prompt)

## 正文 Section 2

...

## 正文 Section N

...

## 总结 / Conclusion

总结段落 + CTA(引导读者行动)
```

**写作规范**:
- 使用 h2 (`##`) 分节,h3 (`###`) 分小节
- 每节有 2-3 段内容
- 适当使用列表(`-`)、表格、引用(`>`)
- 使用 `**粗体**` 强调关键信息
- 文末有总结或行动号召 (CTA)
- 保持段落短小(3-5 句)
- 每 2-3 个 section 插入一张配图

**配图占位符语法**:
```
![图片描述](GENERATE_IMAGE:detailed prompt for image generation)
```

### Step 4: 生成配图

读取项目根目录 `.env` 文件获取配置:
- `IMAGE_GEN_API_ENDPOINT`: API 端点(OpenAI 兼容格式)
- `IMAGE_GEN_API_KEY`: API 密钥
- `IMAGE_GEN_MODEL`: 模型名称

**如果 .env 文件不存在或缺少配置**,跳过图片生成,保留占位符并提示用户配置。

对于每个 `GENERATE_IMAGE:` 占位符:

1. 提取 prompt
2. 使用 Bash 工具调用 API:
```bash
curl -X POST "$IMAGE_GEN_API_ENDPOINT/images/generations" \
  -H "Authorization: Bearer $IMAGE_GEN_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "$IMAGE_GEN_MODEL",
    "prompt": "图片 prompt",
    "n": 1,
    "size": "1024x1024"
  }'
```
3. 下载生成的图片到 `docs/blogs/images/{slug}-{index}.png`
4. 替换占位符为相对路径

**封面图**:
- 额外生成一张封面图
- Prompt 模板:`Professional blog cover for "{title}", modern minimalist style, no text overlay, {type}-related imagery, clean composition`
- 保存为 `docs/blogs/images/cover-{slug}.png`

### Step 5: 输出文件

**1. Markdown 文件** (`docs/blogs/{YYYY-MM-DD}-{slug}.md`):

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
coverImage: docs/blogs/images/cover-{slug}.png
generatedAt: {ISO datetime}
---

{content with image paths replaced}
```

**2. SQL 文件** (`docs/blogs/{YYYY-MM-DD}-{slug}.sql`):

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
    1,  -- tenant_id: 默认租户,需根据实际情况修改
    1,  -- author_id: 默认作者,需根据实际情况修改
    '{title}',
    '{slug}',
    E'{escaped_markdown_content}',
    '{excerpt}',
    '{cover_image_path}',
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

**类型映射**:
| 类型 | 数值 |
|------|------|
| deal | 1 |
| review | 2 |
| tutorial | 3 |
| news | 4 |

**SQL 转义规则**:
- 单引号 `'` → `''`
- 反斜杠 `\` → `\\`
- 使用 `E'...'` 格式支持转义字符

### Step 6: 完成提示

输出完成信息:

```
博客生成完成!

Markdown: docs/blogs/{date}-{slug}.md
SQL: docs/blogs/{date}-{slug}.sql
图片: docs/blogs/images/{slug}-*.png

下一步:
1. 审核 Markdown 文件内容
2. 如需修改,直接编辑 Markdown 文件
3. 上传图片到 CDN 并更新 SQL 中的图片 URL
4. 执行 SQL 插入数据库
```

## 错误处理

| 场景 | 处理 |
|------|------|
| 图片生成 API 未配置 | 跳过图片生成,保留占位符,提示用户配置 .env |
| 图片生成失败 | 保留占位符,输出警告,继续生成其他内容 |
| WebSearch 失败 | 基于已有知识生成,标注需人工校验 |
| 参数错误 | 提示正确用法和示例 |

## 内容质量要求

1. **准确性** - 使用 WebSearch 获取最新信息,不编造事实
2. **原创性** - 重新组织表达,不直接复制搜索结果
3. **SEO 友好** - 标题包含关键词,自然分布关键词,合理使用 h2/h3
4. **可读性** - 短段落(3-5 句),清晰结构,适当使用列表和表格
5. **行动号召** - 文末引导用户采取行动(如访问网站、注册、分享)
6. **专业性** - 符合联盟营销/电商优惠领域的专业标准
