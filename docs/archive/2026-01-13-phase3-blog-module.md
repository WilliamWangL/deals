# Phase 3: 博客模块实施计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 实现 river-module-blog 模块，构建支持 SEO 的内容管理系统，支持文章与 Offer 关联以实现内容变现。

**Architecture:**
- 新建 Maven 模块 `river-module-blog`
- 核心模型：Post (文章), Author (作者), Tag (标签)
- 关联模型：PostTag (文章标签关联), PostOffer (文章商品关联)

**Tech Stack:** Java 17, Spring Boot 3.5, MyBatis-Plus, MapStruct, PostgreSQL

---

## Task 1: 创建 Maven 模块结构

**Files:**
- Create: `river-server/river-module-blog/pom.xml`
- Create: `river-server/river-module-blog/river-module-blog-api/pom.xml`
- Create: `river-server/river-module-blog/river-module-blog-biz/pom.xml`
- Modify: `river-server/pom.xml` (添加子模块)

**Step 1: 创建模块目录结构**
```bash
cd river-server
mkdir -p river-module-blog/river-module-blog-api/src/main/java/com/river/module/blog
mkdir -p river-module-blog/river-module-blog-biz/src/main/java/com/river/module/blog
mkdir -p river-module-blog/river-module-blog-biz/src/main/resources
```

**Step 2: Commit**
```bash
git add river-module-blog/ pom.xml
git commit -m "feat(blog): 创建 river-module-blog 模块结构"
```

---

## Task 2: 创建作者与标签实体

**Files:**
- Create: `river-module-blog-biz/src/main/java/com/river/module/blog/dal/dataobject/AuthorDO.java`
- Create: `river-module-blog-biz/src/main/java/com/river/module/blog/dal/dataobject/TagDO.java`

**Step 1: 创建 AuthorDO**
- 字段：`name`, `slug`, `avatarUrl`, `bio`

**Step 2: 创建 TagDO**
- 字段：`name`, `slug`, `postCount`

**Step 3: Commit**
```bash
git add river-module-blog/
git commit -m "feat(blog): 添加 AuthorDO 和 TagDO 实体"
```

---

## Task 3: 创建文章实体 (Post)

**Files:**
- Create: `river-module-blog-biz/src/main/java/com/river/module/blog/dal/dataobject/PostDO.java`
- Create: `river-module-blog-api/src/main/java/com/river/module/blog/enums/PostTypeEnum.java`
- Create: `river-module-blog-api/src/main/java/com/river/module/blog/enums/PostStatusEnum.java`

**Step 1: 创建枚举**
- `PostTypeEnum`: 1=ARTICLE, 2=GUIDE, 3=REVIEW, 4=NEWS
- `PostStatusEnum`: 0=DRAFT, 1=PUBLISHED, 2=ARCHIVED

**Step 2: 创建 PostDO**
- 字段：`title`, `slug`, `content` (Markdown/HTML), `excerpt`
- SEO：`metaTitle`, `metaDescription`, `canonicalUrl`
- 统计：`viewCount`
- 关联：`authorId`

**Step 3: Commit**
```bash
git add river-module-blog/
git commit -m "feat(blog): 添加 PostDO 实体"
```

---

## Task 4: 创建关联实体

**Files:**
- Create: `river-module-blog-biz/src/main/java/com/river/module/blog/dal/dataobject/PostTagDO.java`
- Create: `river-module-blog-biz/src/main/java/com/river/module/blog/dal/dataobject/PostOfferDO.java`

**Step 1: 创建 PostTagDO**
- 字段：`postId`, `tagId`

**Step 2: 创建 PostOfferDO**
- 字段：`postId`, `offerId`
- 锚点信息：`anchorText`, `position`

**Step 3: Commit**
```bash
git add river-module-blog/
git commit -m "feat(blog): 添加关联实体 PostTagDO 和 PostOfferDO"
```

---

## Task 5: 创建数据库 SQL 脚本

**Files:**
- Create: `river-server/sql/postgresql/blog/river_blog.sql`

**Step 1: 编写 SQL**
- 表：`river_blog_post`, `river_blog_author`, `river_blog_tag`, `river_blog_post_tag`, `river_blog_post_offer`
- 索引：`slug` (唯一), `published_at`

**Step 2: Commit**
```bash
git add river-server/sql/
git commit -m "feat(blog): 添加博客模块数据库 SQL"
```

---

## Task 6: 创建 Mapper 接口

**Files:**
- Create: `river-module-blog-biz/src/main/java/com/river/module/blog/dal/mysql/PostMapper.java`
- Create: `river-module-blog-biz/src/main/java/com/river/module/blog/dal/mysql/AuthorMapper.java`
- Create: `river-module-blog-biz/src/main/java/com/river/module/blog/dal/mysql/TagMapper.java`
- Create: `river-module-blog-biz/src/main/java/com/river/module/blog/dal/mysql/PostTagMapper.java`
- Create: `river-module-blog-biz/src/main/java/com/river/module/blog/dal/mysql/PostOfferMapper.java`

**Step 1: 创建 Mapper**
- 继承 `BaseMapperX`

**Step 2: Commit**
```bash
git add river-module-blog/
git commit -m "feat(blog): 添加 Mapper 接口"
```

---

## Verification

完成所有任务后，验证模块编译通过：

```bash
cd river-server
mvn compile -pl river-module-blog -am
```
