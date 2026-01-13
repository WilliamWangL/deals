-- =============================================
-- River Tracking Module - PostgreSQL Schema
-- =============================================

-- 点击记录表 (ULID 主键，支持月分区)
CREATE TABLE river_tracking_click (
    click_id            VARCHAR(26) PRIMARY KEY,
    offer_id            BIGINT NOT NULL,
    campaign_id         BIGINT,
    landing_page_id     BIGINT,
    sub1                VARCHAR(200),
    sub2                VARCHAR(200),
    sub3                VARCHAR(200),
    sub4                VARCHAR(200),
    sub5                VARCHAR(200),
    ip                  VARCHAR(50),
    user_agent          TEXT,
    referer             TEXT,
    device_type         VARCHAR(50),
    country             VARCHAR(10),
    click_time          TIMESTAMP NOT NULL,
    creator             VARCHAR(64) DEFAULT '',
    create_time         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater             VARCHAR(64) DEFAULT '',
    update_time         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted             SMALLINT NOT NULL DEFAULT 0,
    tenant_id           BIGINT NOT NULL DEFAULT 0
);
CREATE INDEX idx_tracking_click_offer ON river_tracking_click(offer_id);
CREATE INDEX idx_tracking_click_campaign ON river_tracking_click(campaign_id);
CREATE INDEX idx_tracking_click_time ON river_tracking_click(click_time);
CREATE INDEX idx_tracking_click_tenant_time ON river_tracking_click(tenant_id, click_time);
COMMENT ON TABLE river_tracking_click IS '点击追踪记录表';
COMMENT ON COLUMN river_tracking_click.click_id IS 'ULID 格式主键';
COMMENT ON COLUMN river_tracking_click.sub1 IS '自定义追踪参数1';
COMMENT ON COLUMN river_tracking_click.sub2 IS '自定义追踪参数2';
COMMENT ON COLUMN river_tracking_click.sub3 IS '自定义追踪参数3';
COMMENT ON COLUMN river_tracking_click.sub4 IS '自定义追踪参数4';
COMMENT ON COLUMN river_tracking_click.sub5 IS '自定义追踪参数5';

-- 转化记录表
CREATE TABLE river_tracking_conversion (
    id                      BIGINT PRIMARY KEY,
    click_id                VARCHAR(26),
    network_code            VARCHAR(50) NOT NULL,
    external_conversion_id  VARCHAR(200) NOT NULL,
    conversion_type         SMALLINT NOT NULL DEFAULT 2,
    commission              DECIMAL(12,4) NOT NULL DEFAULT 0,
    currency                VARCHAR(10) NOT NULL DEFAULT 'USD',
    status                  SMALLINT NOT NULL DEFAULT 0,
    network_payload         TEXT,
    conversion_time         TIMESTAMP NOT NULL,
    creator                 VARCHAR(64) DEFAULT '',
    create_time             TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater                 VARCHAR(64) DEFAULT '',
    update_time             TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted                 SMALLINT NOT NULL DEFAULT 0,
    tenant_id               BIGINT NOT NULL DEFAULT 0
);
CREATE UNIQUE INDEX uk_conversion_network_external ON river_tracking_conversion(tenant_id, network_code, external_conversion_id) WHERE deleted = 0;
CREATE INDEX idx_conversion_click ON river_tracking_conversion(click_id);
CREATE INDEX idx_conversion_status ON river_tracking_conversion(status);
CREATE INDEX idx_conversion_time ON river_tracking_conversion(conversion_time);
COMMENT ON TABLE river_tracking_conversion IS '转化记录表';
COMMENT ON COLUMN river_tracking_conversion.conversion_type IS '转化类型: 1=Lead, 2=Sale, 3=Install, 4=Signup';
COMMENT ON COLUMN river_tracking_conversion.status IS '状态: 0=待确认, 1=已确认, 2=已拒绝, 3=已撤销';

-- 追踪链接表
CREATE TABLE river_tracking_link (
    id              BIGINT PRIMARY KEY,
    offer_id        BIGINT NOT NULL,
    slug            VARCHAR(100) NOT NULL,
    preset_sub1     VARCHAR(200),
    preset_sub2     VARCHAR(200),
    preset_sub3     VARCHAR(200),
    preset_sub4     VARCHAR(200),
    preset_sub5     VARCHAR(200),
    utm_params      TEXT,
    status          SMALLINT NOT NULL DEFAULT 1,
    creator         VARCHAR(64) DEFAULT '',
    create_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64) DEFAULT '',
    update_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         SMALLINT NOT NULL DEFAULT 0,
    tenant_id       BIGINT NOT NULL DEFAULT 0
);
CREATE UNIQUE INDEX uk_tracking_link_slug ON river_tracking_link(slug, tenant_id) WHERE deleted = 0;
CREATE INDEX idx_tracking_link_offer ON river_tracking_link(offer_id);
COMMENT ON TABLE river_tracking_link IS '追踪链接表';
COMMENT ON COLUMN river_tracking_link.slug IS '短链接标识';
COMMENT ON COLUMN river_tracking_link.status IS '状态: 0=禁用, 1=启用';

-- 未归因转化表
CREATE TABLE river_tracking_unattributed_conversion (
    id                      BIGINT PRIMARY KEY,
    network_code            VARCHAR(50) NOT NULL,
    external_conversion_id  VARCHAR(200) NOT NULL,
    conversion_type         SMALLINT,
    commission              DECIMAL(12,4),
    currency                VARCHAR(10),
    network_payload         TEXT,
    raw_request             TEXT,
    attribution_fail_reason VARCHAR(500),
    conversion_time         TIMESTAMP,
    creator                 VARCHAR(64) DEFAULT '',
    create_time             TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater                 VARCHAR(64) DEFAULT '',
    update_time             TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted                 SMALLINT NOT NULL DEFAULT 0,
    tenant_id               BIGINT NOT NULL DEFAULT 0
);
CREATE INDEX idx_unattributed_network ON river_tracking_unattributed_conversion(network_code);
CREATE INDEX idx_unattributed_time ON river_tracking_unattributed_conversion(create_time);
COMMENT ON TABLE river_tracking_unattributed_conversion IS '未归因转化记录表';
COMMENT ON COLUMN river_tracking_unattributed_conversion.attribution_fail_reason IS '归因失败原因';

-- 序列
CREATE SEQUENCE IF NOT EXISTS river_tracking_conversion_seq START 1;
CREATE SEQUENCE IF NOT EXISTS river_tracking_link_seq START 1;
CREATE SEQUENCE IF NOT EXISTS river_tracking_unattributed_conversion_seq START 1;
