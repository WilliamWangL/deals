-- =============================================
-- River Affiliate Module - PostgreSQL Schema
-- =============================================

-- 联盟网络表
CREATE TABLE river_affiliate_network (
    id              BIGINT PRIMARY KEY,
    code            VARCHAR(50) NOT NULL,
    name            VARCHAR(100) NOT NULL,
    type            SMALLINT NOT NULL DEFAULT 1,
    api_base_url    VARCHAR(500),
    status          SMALLINT NOT NULL DEFAULT 0,
    website_url     VARCHAR(500),
    logo_url        VARCHAR(500),
    description     TEXT,
    remark          VARCHAR(500),
    creator         VARCHAR(64) DEFAULT '',
    create_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64) DEFAULT '',
    update_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         SMALLINT NOT NULL DEFAULT 0,
    tenant_id       BIGINT NOT NULL DEFAULT 0
);
CREATE UNIQUE INDEX uk_affiliate_network_code ON river_affiliate_network(code, tenant_id) WHERE deleted = 0;
CREATE INDEX idx_affiliate_network_status ON river_affiliate_network(status);
COMMENT ON TABLE river_affiliate_network IS '联盟网络表';

-- 联盟凭证表
CREATE TABLE river_affiliate_network_credential (
    id              BIGINT PRIMARY KEY,
    network_id      BIGINT NOT NULL,
    auth_type       SMALLINT NOT NULL,
    credentials     TEXT NOT NULL,
    expires_at      TIMESTAMP,
    enabled         BOOLEAN NOT NULL DEFAULT TRUE,
    creator         VARCHAR(64) DEFAULT '',
    create_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64) DEFAULT '',
    update_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         SMALLINT NOT NULL DEFAULT 0,
    tenant_id       BIGINT NOT NULL DEFAULT 0
);
CREATE INDEX idx_network_credential_network ON river_affiliate_network_credential(network_id);
COMMENT ON TABLE river_affiliate_network_credential IS '联盟网络凭证表';

-- Postback 密钥表
CREATE TABLE river_affiliate_postback_secret (
    id                      BIGINT PRIMARY KEY,
    network_id              BIGINT NOT NULL,
    secret_key              VARCHAR(200) NOT NULL,
    algorithm               VARCHAR(50) NOT NULL DEFAULT 'HMAC-SHA256',
    allowed_ips             TEXT,
    ip_whitelist_enabled    BOOLEAN NOT NULL DEFAULT FALSE,
    signature_enabled       BOOLEAN NOT NULL DEFAULT TRUE,
    creator                 VARCHAR(64) DEFAULT '',
    create_time             TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater                 VARCHAR(64) DEFAULT '',
    update_time             TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted                 SMALLINT NOT NULL DEFAULT 0,
    tenant_id               BIGINT NOT NULL DEFAULT 0
);
CREATE INDEX idx_postback_secret_network ON river_affiliate_postback_secret(network_id);
COMMENT ON TABLE river_affiliate_postback_secret IS 'Postback回调验证配置表';

-- 商家表
CREATE TABLE river_affiliate_merchant (
    id              BIGINT PRIMARY KEY,
    network_id      BIGINT NOT NULL,
    external_id     VARCHAR(100),
    name            VARCHAR(200) NOT NULL,
    slug            VARCHAR(200),
    domain          VARCHAR(200),
    logo_url        VARCHAR(500),
    description     TEXT,
    rating          DECIMAL(3,2),
    status          SMALLINT NOT NULL DEFAULT 0,
    regions         TEXT,
    category_ids    TEXT,
    creator         VARCHAR(64) DEFAULT '',
    create_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64) DEFAULT '',
    update_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         SMALLINT NOT NULL DEFAULT 0,
    tenant_id       BIGINT NOT NULL DEFAULT 0
);
CREATE INDEX idx_merchant_network ON river_affiliate_merchant(network_id);
CREATE INDEX idx_merchant_status ON river_affiliate_merchant(status);
CREATE UNIQUE INDEX uk_merchant_slug ON river_affiliate_merchant(slug, tenant_id) WHERE deleted = 0 AND slug IS NOT NULL;
CREATE UNIQUE INDEX uk_merchant_external ON river_affiliate_merchant(network_id, external_id, tenant_id) WHERE deleted = 0 AND external_id IS NOT NULL;
COMMENT ON TABLE river_affiliate_merchant IS '商家/广告主表';

-- 分类表
CREATE TABLE river_affiliate_category (
    id              BIGINT PRIMARY KEY,
    parent_id       BIGINT NOT NULL DEFAULT 0,
    name            VARCHAR(100) NOT NULL,
    slug            VARCHAR(100) NOT NULL,
    level           SMALLINT NOT NULL DEFAULT 1,
    sort            INT NOT NULL DEFAULT 0,
    icon            VARCHAR(200),
    status          SMALLINT NOT NULL DEFAULT 0,
    creator         VARCHAR(64) DEFAULT '',
    create_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64) DEFAULT '',
    update_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         SMALLINT NOT NULL DEFAULT 0,
    tenant_id       BIGINT NOT NULL DEFAULT 0
);
CREATE UNIQUE INDEX uk_category_slug ON river_affiliate_category(slug, tenant_id) WHERE deleted = 0;
CREATE INDEX idx_category_parent ON river_affiliate_category(parent_id);
COMMENT ON TABLE river_affiliate_category IS '分类表';

-- Offer 表
CREATE TABLE river_affiliate_offer (
    id                      BIGINT PRIMARY KEY,
    merchant_id             BIGINT NOT NULL,
    network_id              BIGINT NOT NULL,
    external_id             VARCHAR(100),
    name                    VARCHAR(300) NOT NULL,
    description             TEXT,
    commission_type         SMALLINT NOT NULL DEFAULT 1,
    commission_value        DECIMAL(10,4) NOT NULL DEFAULT 0,
    currency                VARCHAR(10) NOT NULL DEFAULT 'USD',
    cookie_days             INT DEFAULT 30,
    tracking_url_template   TEXT,
    landing_url             VARCHAR(1000),
    status                  SMALLINT NOT NULL DEFAULT 0,
    regions                 TEXT,
    category_ids            TEXT,
    tags                    TEXT,
    image_url               VARCHAR(500),
    epc                     DECIMAL(10,4),
    conversion_rate         DECIMAL(5,4),
    featured                BOOLEAN NOT NULL DEFAULT FALSE,
    hot_score               INT NOT NULL DEFAULT 0,
    creator                 VARCHAR(64) DEFAULT '',
    create_time             TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater                 VARCHAR(64) DEFAULT '',
    update_time             TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted                 SMALLINT NOT NULL DEFAULT 0,
    tenant_id               BIGINT NOT NULL DEFAULT 0
);
CREATE INDEX idx_offer_merchant ON river_affiliate_offer(merchant_id);
CREATE INDEX idx_offer_network ON river_affiliate_offer(network_id);
CREATE INDEX idx_offer_status ON river_affiliate_offer(status);
CREATE INDEX idx_offer_featured ON river_affiliate_offer(featured) WHERE featured = TRUE;
CREATE UNIQUE INDEX uk_offer_external ON river_affiliate_offer(network_id, external_id, tenant_id) WHERE deleted = 0 AND external_id IS NOT NULL;
COMMENT ON TABLE river_affiliate_offer IS 'Offer/广告表';

-- 序列
CREATE SEQUENCE IF NOT EXISTS river_affiliate_network_seq START 1;
CREATE SEQUENCE IF NOT EXISTS river_affiliate_network_credential_seq START 1;
CREATE SEQUENCE IF NOT EXISTS river_affiliate_postback_secret_seq START 1;
CREATE SEQUENCE IF NOT EXISTS river_affiliate_merchant_seq START 1;
CREATE SEQUENCE IF NOT EXISTS river_affiliate_category_seq START 1;
CREATE SEQUENCE IF NOT EXISTS river_affiliate_offer_seq START 1;
