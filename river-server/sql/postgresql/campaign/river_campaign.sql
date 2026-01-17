-- =============================================
-- River Campaign Module - PostgreSQL Schema
-- =============================================

-- 流量来源表
CREATE TABLE river_campaign_traffic_source (
    id              BIGINT PRIMARY KEY,
    code            VARCHAR(50) NOT NULL,
    name            VARCHAR(100) NOT NULL,
    api_credentials TEXT,
    status          SMALLINT NOT NULL DEFAULT 0,
    creator         VARCHAR(64) DEFAULT '',
    create_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64) DEFAULT '',
    update_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         SMALLINT NOT NULL DEFAULT 0,
    tenant_id       BIGINT NOT NULL DEFAULT 0
);
CREATE UNIQUE INDEX uk_traffic_source_code ON river_campaign_traffic_source(code, tenant_id) WHERE deleted = 0;
CREATE INDEX idx_traffic_source_status ON river_campaign_traffic_source(status);
COMMENT ON TABLE river_campaign_traffic_source IS '流量来源表';

-- Campaign 表
CREATE TABLE river_campaign_campaign (
    id                      BIGINT PRIMARY KEY,
    traffic_source_id       BIGINT NOT NULL,
    name                    VARCHAR(200) NOT NULL,
    type                    SMALLINT NOT NULL DEFAULT 1,
    offer_ids               TEXT,
    landing_page_id         BIGINT,
    budget_daily            DECIMAL(12,2),
    budget_total            DECIMAL(12,2),
    external_campaign_id    VARCHAR(200),
    status                  SMALLINT NOT NULL DEFAULT 0,
    creator                 VARCHAR(64) DEFAULT '',
    create_time             TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater                 VARCHAR(64) DEFAULT '',
    update_time             TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted                 SMALLINT NOT NULL DEFAULT 0,
    tenant_id               BIGINT NOT NULL DEFAULT 0
);
CREATE INDEX idx_campaign_traffic_source ON river_campaign_campaign(traffic_source_id);
CREATE INDEX idx_campaign_status ON river_campaign_campaign(status);
CREATE INDEX idx_campaign_type ON river_campaign_campaign(type);
COMMENT ON TABLE river_campaign_campaign IS 'Campaign/广告活动表';

-- 广告组表
CREATE TABLE river_campaign_ad_group (
    id                  BIGINT PRIMARY KEY,
    campaign_id         BIGINT NOT NULL,
    name                VARCHAR(200) NOT NULL,
    targeting           TEXT,
    bid_strategy        VARCHAR(100),
    external_ad_group_id VARCHAR(200),
    status              SMALLINT NOT NULL DEFAULT 0,
    creator             VARCHAR(64) DEFAULT '',
    create_time         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater             VARCHAR(64) DEFAULT '',
    update_time         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted             SMALLINT NOT NULL DEFAULT 0,
    tenant_id           BIGINT NOT NULL DEFAULT 0
);
CREATE INDEX idx_ad_group_campaign ON river_campaign_ad_group(campaign_id);
CREATE INDEX idx_ad_group_status ON river_campaign_ad_group(status);
COMMENT ON TABLE river_campaign_ad_group IS '广告组表';

-- 落地页表
CREATE TABLE river_campaign_landing_page (
    id              BIGINT PRIMARY KEY,
    name            VARCHAR(200) NOT NULL,
    slug            VARCHAR(100) NOT NULL,
    type            SMALLINT NOT NULL DEFAULT 1,
    url             VARCHAR(1000),
    offer_id        BIGINT,
    content         TEXT,
    status          SMALLINT NOT NULL DEFAULT 0,
    creator         VARCHAR(64) DEFAULT '',
    create_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64) DEFAULT '',
    update_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         SMALLINT NOT NULL DEFAULT 0,
    tenant_id       BIGINT NOT NULL DEFAULT 0
);
CREATE UNIQUE INDEX uk_landing_page_slug ON river_campaign_landing_page(slug, tenant_id) WHERE deleted = 0;
CREATE INDEX idx_landing_page_status ON river_campaign_landing_page(status);
CREATE INDEX idx_landing_page_type ON river_campaign_landing_page(type);
COMMENT ON TABLE river_campaign_landing_page IS '落地页表';

-- 成本记录表
CREATE TABLE river_campaign_cost_record (
    id              BIGINT PRIMARY KEY,
    campaign_id     BIGINT NOT NULL,
    ad_group_id     BIGINT,
    date            DATE NOT NULL,
    impressions     INT DEFAULT 0,
    clicks          INT DEFAULT 0,
    cost            DECIMAL(12,4) NOT NULL DEFAULT 0,
    currency        VARCHAR(10) NOT NULL DEFAULT 'USD',
    source          SMALLINT NOT NULL DEFAULT 1,
    creator         VARCHAR(64) DEFAULT '',
    create_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64) DEFAULT '',
    update_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         SMALLINT NOT NULL DEFAULT 0,
    tenant_id       BIGINT NOT NULL DEFAULT 0
);
CREATE INDEX idx_cost_record_campaign ON river_campaign_cost_record(campaign_id);
CREATE INDEX idx_cost_record_ad_group ON river_campaign_cost_record(ad_group_id);
CREATE INDEX idx_cost_record_date ON river_campaign_cost_record(date);
COMMENT ON TABLE river_campaign_cost_record IS '成本记录表';

-- 货币表
CREATE TABLE river_campaign_currency (
    id              BIGINT PRIMARY KEY,
    code            VARCHAR(10) NOT NULL,
    name            VARCHAR(50) NOT NULL,
    symbol          VARCHAR(10),
    decimal_places  SMALLINT NOT NULL DEFAULT 2,
    status          SMALLINT NOT NULL DEFAULT 0,
    creator         VARCHAR(64) DEFAULT '',
    create_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64) DEFAULT '',
    update_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         SMALLINT NOT NULL DEFAULT 0,
    tenant_id       BIGINT NOT NULL DEFAULT 0
);
CREATE UNIQUE INDEX uk_currency_code ON river_campaign_currency(code, tenant_id) WHERE deleted = 0;
COMMENT ON TABLE river_campaign_currency IS '货币表';

-- 汇率表
CREATE TABLE river_campaign_fx_rate (
    id              BIGINT PRIMARY KEY,
    from_currency   VARCHAR(10) NOT NULL,
    to_currency     VARCHAR(10) NOT NULL,
    rate            DECIMAL(18,8) NOT NULL,
    effective_date  DATE NOT NULL,
    creator         VARCHAR(64) DEFAULT '',
    create_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64) DEFAULT '',
    update_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         SMALLINT NOT NULL DEFAULT 0,
    tenant_id       BIGINT NOT NULL DEFAULT 0
);
CREATE INDEX idx_fx_rate_currencies ON river_campaign_fx_rate(from_currency, to_currency);
CREATE INDEX idx_fx_rate_effective_date ON river_campaign_fx_rate(effective_date);
COMMENT ON TABLE river_campaign_fx_rate IS '汇率表';

-- 序列
CREATE SEQUENCE IF NOT EXISTS river_campaign_traffic_source_seq START 1;
CREATE SEQUENCE IF NOT EXISTS river_campaign_campaign_seq START 1;
CREATE SEQUENCE IF NOT EXISTS river_campaign_ad_group_seq START 1;
CREATE SEQUENCE IF NOT EXISTS river_campaign_landing_page_seq START 1;
CREATE SEQUENCE IF NOT EXISTS river_campaign_cost_record_seq START 1;
CREATE SEQUENCE IF NOT EXISTS river_campaign_currency_seq START 1;
CREATE SEQUENCE IF NOT EXISTS river_campaign_fx_rate_seq START 1;
