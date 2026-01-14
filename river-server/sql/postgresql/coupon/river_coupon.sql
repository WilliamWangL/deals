-- =============================================
-- River Coupon Module - PostgreSQL Schema
-- =============================================

-- 优惠券表
CREATE TABLE river_coupon_coupon (
    id              BIGINT PRIMARY KEY,
    merchant_id     BIGINT NOT NULL,
    offer_id        BIGINT,
    code            VARCHAR(100) NOT NULL,
    discount_type   SMALLINT NOT NULL DEFAULT 1,
    discount_value  DECIMAL(10,2) NOT NULL DEFAULT 0,
    min_purchase    DECIMAL(10,2),
    start_time      TIMESTAMP,
    end_time        TIMESTAMP,
    terms           TEXT,
    source          SMALLINT NOT NULL DEFAULT 2,
    verified        BOOLEAN NOT NULL DEFAULT FALSE,
    hot_score       INT NOT NULL DEFAULT 0,
    status          SMALLINT NOT NULL DEFAULT 1,
    creator         VARCHAR(64) DEFAULT '',
    create_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64) DEFAULT '',
    update_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         SMALLINT NOT NULL DEFAULT 0,
    tenant_id       BIGINT NOT NULL DEFAULT 0
);
CREATE INDEX idx_coupon_merchant ON river_coupon_coupon(merchant_id);
CREATE INDEX idx_coupon_offer ON river_coupon_coupon(offer_id);
CREATE INDEX idx_coupon_status ON river_coupon_coupon(status);
CREATE INDEX idx_coupon_verified ON river_coupon_coupon(verified) WHERE verified = TRUE;
CREATE INDEX idx_coupon_end_time ON river_coupon_coupon(end_time);
COMMENT ON TABLE river_coupon_coupon IS '优惠券表';

-- Deal 表
CREATE TABLE river_coupon_deal (
    id              BIGINT PRIMARY KEY,
    merchant_id     BIGINT NOT NULL,
    offer_id        BIGINT,
    title           VARCHAR(300) NOT NULL,
    slug            VARCHAR(300),
    description     TEXT,
    original_price  DECIMAL(10,2),
    deal_price      DECIMAL(10,2),
    discount_percent SMALLINT,
    start_time      TIMESTAMP,
    end_time        TIMESTAMP,
    stock_limit     INT,
    image_url       VARCHAR(500),
    hot_score       INT NOT NULL DEFAULT 0,
    featured        BOOLEAN NOT NULL DEFAULT FALSE,
    status          SMALLINT NOT NULL DEFAULT 1,
    creator         VARCHAR(64) DEFAULT '',
    create_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64) DEFAULT '',
    update_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         SMALLINT NOT NULL DEFAULT 0,
    tenant_id       BIGINT NOT NULL DEFAULT 0
);
CREATE INDEX idx_deal_merchant ON river_coupon_deal(merchant_id);
CREATE INDEX idx_deal_offer ON river_coupon_deal(offer_id);
CREATE INDEX idx_deal_status ON river_coupon_deal(status);
CREATE UNIQUE INDEX uk_deal_slug ON river_coupon_deal(slug, tenant_id) WHERE deleted = 0 AND slug IS NOT NULL;
CREATE INDEX idx_deal_featured ON river_coupon_deal(featured) WHERE featured = TRUE;
CREATE INDEX idx_deal_end_time ON river_coupon_deal(end_time);
COMMENT ON TABLE river_coupon_deal IS 'Deal/优惠活动表';

-- 序列
CREATE SEQUENCE IF NOT EXISTS river_coupon_coupon_seq START 1;
CREATE SEQUENCE IF NOT EXISTS river_coupon_deal_seq START 1;
