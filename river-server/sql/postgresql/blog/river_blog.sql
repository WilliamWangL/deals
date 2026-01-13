-- =============================================
-- River Blog Module - PostgreSQL Schema
-- =============================================

-- 作者表
CREATE TABLE river_blog_author (
    id              BIGINT PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    slug            VARCHAR(100) NOT NULL,
    avatar_url      VARCHAR(500),
    bio             TEXT,
    status          SMALLINT NOT NULL DEFAULT 1,
    creator         VARCHAR(64) DEFAULT '',
    create_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64) DEFAULT '',
    update_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         SMALLINT NOT NULL DEFAULT 0,
    tenant_id       BIGINT NOT NULL DEFAULT 0
);
CREATE UNIQUE INDEX uk_blog_author_slug ON river_blog_author(slug, tenant_id) WHERE deleted = 0;
CREATE INDEX idx_blog_author_status ON river_blog_author(status);
COMMENT ON TABLE river_blog_author IS '博客作者表';

-- 标签表
CREATE TABLE river_blog_tag (
    id              BIGINT PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    slug            VARCHAR(100) NOT NULL,
    post_count      INT NOT NULL DEFAULT 0,
    status          SMALLINT NOT NULL DEFAULT 1,
    creator         VARCHAR(64) DEFAULT '',
    create_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64) DEFAULT '',
    update_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         SMALLINT NOT NULL DEFAULT 0,
    tenant_id       BIGINT NOT NULL DEFAULT 0
);
CREATE UNIQUE INDEX uk_blog_tag_slug ON river_blog_tag(slug, tenant_id) WHERE deleted = 0;
CREATE INDEX idx_blog_tag_status ON river_blog_tag(status);
COMMENT ON TABLE river_blog_tag IS '博客标签表';

-- 文章表
CREATE TABLE river_blog_post (
    id                  BIGINT PRIMARY KEY,
    author_id           BIGINT NOT NULL,
    title               VARCHAR(300) NOT NULL,
    slug                VARCHAR(300) NOT NULL,
    content             TEXT,
    excerpt             TEXT,
    cover_image         VARCHAR(500),
    type                SMALLINT NOT NULL DEFAULT 1,
    status              SMALLINT NOT NULL DEFAULT 0,
    published_at        TIMESTAMP,
    meta_title          VARCHAR(200),
    meta_description    VARCHAR(500),
    canonical_url       VARCHAR(500),
    view_count          INT NOT NULL DEFAULT 0,
    featured            BOOLEAN NOT NULL DEFAULT FALSE,
    creator             VARCHAR(64) DEFAULT '',
    create_time         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater             VARCHAR(64) DEFAULT '',
    update_time         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted             SMALLINT NOT NULL DEFAULT 0,
    tenant_id           BIGINT NOT NULL DEFAULT 0
);
CREATE UNIQUE INDEX uk_blog_post_slug ON river_blog_post(slug, tenant_id) WHERE deleted = 0;
CREATE INDEX idx_blog_post_author ON river_blog_post(author_id);
CREATE INDEX idx_blog_post_type ON river_blog_post(type);
CREATE INDEX idx_blog_post_status ON river_blog_post(status);
CREATE INDEX idx_blog_post_featured ON river_blog_post(featured) WHERE featured = TRUE;
CREATE INDEX idx_blog_post_published_at ON river_blog_post(published_at);
COMMENT ON TABLE river_blog_post IS '博客文章表';

-- 文章标签关联表
CREATE TABLE river_blog_post_tag (
    id              BIGINT PRIMARY KEY,
    post_id         BIGINT NOT NULL,
    tag_id          BIGINT NOT NULL,
    creator         VARCHAR(64) DEFAULT '',
    create_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64) DEFAULT '',
    update_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         SMALLINT NOT NULL DEFAULT 0,
    tenant_id       BIGINT NOT NULL DEFAULT 0
);
CREATE INDEX idx_blog_post_tag_post ON river_blog_post_tag(post_id);
CREATE INDEX idx_blog_post_tag_tag ON river_blog_post_tag(tag_id);
CREATE UNIQUE INDEX uk_blog_post_tag ON river_blog_post_tag(post_id, tag_id, tenant_id) WHERE deleted = 0;
COMMENT ON TABLE river_blog_post_tag IS '文章标签关联表';

-- 文章Offer关联表（追踪文章带来的转化）
CREATE TABLE river_blog_post_offer (
    id              BIGINT PRIMARY KEY,
    post_id         BIGINT NOT NULL,
    offer_id        BIGINT NOT NULL,
    anchor_text     VARCHAR(200),
    position        INT NOT NULL DEFAULT 0,
    creator         VARCHAR(64) DEFAULT '',
    create_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64) DEFAULT '',
    update_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         SMALLINT NOT NULL DEFAULT 0,
    tenant_id       BIGINT NOT NULL DEFAULT 0
);
CREATE INDEX idx_blog_post_offer_post ON river_blog_post_offer(post_id);
CREATE INDEX idx_blog_post_offer_offer ON river_blog_post_offer(offer_id);
COMMENT ON TABLE river_blog_post_offer IS '文章Offer关联表';

-- 序列
CREATE SEQUENCE IF NOT EXISTS river_blog_author_seq START 1;
CREATE SEQUENCE IF NOT EXISTS river_blog_tag_seq START 1;
CREATE SEQUENCE IF NOT EXISTS river_blog_post_seq START 1;
CREATE SEQUENCE IF NOT EXISTS river_blog_post_tag_seq START 1;
CREATE SEQUENCE IF NOT EXISTS river_blog_post_offer_seq START 1;
