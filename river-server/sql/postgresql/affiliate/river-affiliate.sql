-- =============================================
-- River Affiliate Module - PostgreSQL Database Schema
-- Version: 1.0.0
-- Date: 2026-01-12
-- =============================================

-- =============================================
-- 1. river_affiliate_network - 广告联盟配置
-- =============================================
DROP TABLE IF EXISTS river_affiliate_network;
CREATE TABLE river_affiliate_network (
    id int8 NOT NULL,
    code varchar(32) NOT NULL,
    name varchar(64) NOT NULL,
    logo varchar(500) DEFAULT '',
    website varchar(500) DEFAULT '',
    api_base_url varchar(256) DEFAULT '',
    api_config jsonb NOT NULL DEFAULT '{}'::jsonb,
    status int2 NOT NULL DEFAULT 0,
    remark varchar(500) DEFAULT '',
    creator varchar(64) DEFAULT '',
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater varchar(64) DEFAULT '',
    update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted int2 NOT NULL DEFAULT 0,
    tenant_id int8 NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX uk_affiliate_network_tenant_code ON river_affiliate_network (tenant_id, code) WHERE deleted = 0;
CREATE INDEX idx_affiliate_network_status ON river_affiliate_network (tenant_id, status) WHERE deleted = 0;

DROP SEQUENCE IF EXISTS river_affiliate_network_seq;
CREATE SEQUENCE river_affiliate_network_seq START 1;

COMMENT ON TABLE river_affiliate_network IS '广告联盟配置表';
COMMENT ON COLUMN river_affiliate_network.id IS '编号';
COMMENT ON COLUMN river_affiliate_network.code IS '联盟代码(admitad/awin/cj/custom)';
COMMENT ON COLUMN river_affiliate_network.name IS '联盟名称';
COMMENT ON COLUMN river_affiliate_network.logo IS 'Logo URL';
COMMENT ON COLUMN river_affiliate_network.website IS '官网地址';
COMMENT ON COLUMN river_affiliate_network.api_base_url IS 'API基础地址';
COMMENT ON COLUMN river_affiliate_network.api_config IS 'API配置(JSON格式,含token/keys等)';
COMMENT ON COLUMN river_affiliate_network.status IS '状态(0正常 1停用)';
COMMENT ON COLUMN river_affiliate_network.remark IS '备注';

-- =============================================
-- 2. river_campaign - 商家/活动(广告主)
-- =============================================
DROP TABLE IF EXISTS river_campaign;
CREATE TABLE river_campaign (
    id int8 NOT NULL,
    network_id int8 NOT NULL,
    external_id varchar(64) NOT NULL,
    name varchar(200) NOT NULL,
    logo varchar(500) DEFAULT '',
    site_url varchar(500) DEFAULT '',
    description text DEFAULT '',
    status int2 NOT NULL DEFAULT 0,
    raw_payload jsonb NOT NULL DEFAULT '{}'::jsonb,
    creator varchar(64) DEFAULT '',
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater varchar(64) DEFAULT '',
    update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted int2 NOT NULL DEFAULT 0,
    tenant_id int8 NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX uk_campaign_tenant_network_external ON river_campaign (tenant_id, network_id, external_id) WHERE deleted = 0;
CREATE INDEX idx_campaign_tenant_network ON river_campaign (tenant_id, network_id) WHERE deleted = 0;
CREATE INDEX idx_campaign_tenant_status ON river_campaign (tenant_id, status) WHERE deleted = 0;

DROP SEQUENCE IF EXISTS river_campaign_seq;
CREATE SEQUENCE river_campaign_seq START 1;

COMMENT ON TABLE river_campaign IS '商家/活动表(对应联盟的advertiser/campaign)';
COMMENT ON COLUMN river_campaign.id IS '编号';
COMMENT ON COLUMN river_campaign.network_id IS '联盟编号';
COMMENT ON COLUMN river_campaign.external_id IS '外部ID(联盟侧ID)';
COMMENT ON COLUMN river_campaign.name IS '商家名称';
COMMENT ON COLUMN river_campaign.logo IS 'Logo URL';
COMMENT ON COLUMN river_campaign.site_url IS '商家官网';
COMMENT ON COLUMN river_campaign.description IS '商家描述';
COMMENT ON COLUMN river_campaign.status IS '状态(0正常 1停用)';
COMMENT ON COLUMN river_campaign.raw_payload IS '原始API返回(JSON)';

-- =============================================
-- 3. river_category - 站内分类(树形结构)
-- =============================================
DROP TABLE IF EXISTS river_category;
CREATE TABLE river_category (
    id int8 NOT NULL,
    parent_id int8 NOT NULL DEFAULT 0,
    name varchar(100) NOT NULL,
    slug varchar(100) DEFAULT '',
    icon varchar(200) DEFAULT '',
    sort int4 NOT NULL DEFAULT 0,
    status int2 NOT NULL DEFAULT 0,
    creator varchar(64) DEFAULT '',
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater varchar(64) DEFAULT '',
    update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted int2 NOT NULL DEFAULT 0,
    tenant_id int8 NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE INDEX idx_category_tenant_parent ON river_category (tenant_id, parent_id) WHERE deleted = 0;
CREATE INDEX idx_category_tenant_status ON river_category (tenant_id, status) WHERE deleted = 0;
CREATE UNIQUE INDEX uk_category_tenant_slug ON river_category (tenant_id, slug) WHERE deleted = 0 AND slug <> '';

DROP SEQUENCE IF EXISTS river_category_seq;
CREATE SEQUENCE river_category_seq START 1;

COMMENT ON TABLE river_category IS '站内分类表(树形结构)';
COMMENT ON COLUMN river_category.id IS '编号';
COMMENT ON COLUMN river_category.parent_id IS '父分类编号(0为顶级)';
COMMENT ON COLUMN river_category.name IS '分类名称';
COMMENT ON COLUMN river_category.slug IS 'URL友好标识(SEO)';
COMMENT ON COLUMN river_category.icon IS '图标';
COMMENT ON COLUMN river_category.sort IS '排序';
COMMENT ON COLUMN river_category.status IS '状态(0正常 1停用)';

-- =============================================
-- 4. river_network_category - 外部联盟分类
-- =============================================
DROP TABLE IF EXISTS river_network_category;
CREATE TABLE river_network_category (
    id int8 NOT NULL,
    network_id int8 NOT NULL,
    external_id varchar(64) NOT NULL,
    parent_external_id varchar(64) DEFAULT '',
    name varchar(200) NOT NULL,
    raw_payload jsonb NOT NULL DEFAULT '{}'::jsonb,
    creator varchar(64) DEFAULT '',
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater varchar(64) DEFAULT '',
    update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted int2 NOT NULL DEFAULT 0,
    tenant_id int8 NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX uk_network_category_tenant_network_external ON river_network_category (tenant_id, network_id, external_id) WHERE deleted = 0;
CREATE INDEX idx_network_category_tenant_network ON river_network_category (tenant_id, network_id) WHERE deleted = 0;

DROP SEQUENCE IF EXISTS river_network_category_seq;
CREATE SEQUENCE river_network_category_seq START 1;

COMMENT ON TABLE river_network_category IS '外部联盟分类表';
COMMENT ON COLUMN river_network_category.id IS '编号';
COMMENT ON COLUMN river_network_category.network_id IS '联盟编号';
COMMENT ON COLUMN river_network_category.external_id IS '外部分类ID';
COMMENT ON COLUMN river_network_category.parent_external_id IS '外部父分类ID';
COMMENT ON COLUMN river_network_category.name IS '分类名称';
COMMENT ON COLUMN river_network_category.raw_payload IS '原始API返回(JSON)';

-- =============================================
-- 5. river_category_map - 分类映射(外部→站内)
-- =============================================
DROP TABLE IF EXISTS river_category_map;
CREATE TABLE river_category_map (
    id int8 NOT NULL,
    network_category_id int8 NOT NULL,
    category_id int8 NOT NULL,
    creator varchar(64) DEFAULT '',
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater varchar(64) DEFAULT '',
    update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted int2 NOT NULL DEFAULT 0,
    tenant_id int8 NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX uk_category_map_tenant_mapping ON river_category_map (tenant_id, network_category_id, category_id) WHERE deleted = 0;
CREATE INDEX idx_category_map_tenant_category ON river_category_map (tenant_id, category_id) WHERE deleted = 0;

DROP SEQUENCE IF EXISTS river_category_map_seq;
CREATE SEQUENCE river_category_map_seq START 1;

COMMENT ON TABLE river_category_map IS '分类映射表(外部分类→站内分类,多对多)';
COMMENT ON COLUMN river_category_map.id IS '编号';
COMMENT ON COLUMN river_category_map.network_category_id IS '外部分类编号';
COMMENT ON COLUMN river_category_map.category_id IS '站内分类编号';

-- =============================================
-- 6. river_offer - 统一内容表(Coupon/Deal)
-- =============================================
DROP TABLE IF EXISTS river_offer;
CREATE TABLE river_offer (
    id int8 NOT NULL,
    network_id int8 NOT NULL,
    campaign_id int8 NOT NULL DEFAULT 0,
    external_id varchar(64) NOT NULL,
    
    type int2 NOT NULL DEFAULT 0,
    species varchar(32) DEFAULT '',
    
    title varchar(500) NOT NULL,
    description text DEFAULT '',
    terms text DEFAULT '',
    language varchar(16) DEFAULT '',
    
    promocode varchar(100) DEFAULT '',
    discount varchar(64) DEFAULT '',
    
    goto_link varchar(1000) NOT NULL,
    url_tracking varchar(1000) DEFAULT '',
    image_url varchar(500) DEFAULT '',
    
    date_start timestamp NULL,
    date_end timestamp NULL,
    
    regions text[] NOT NULL DEFAULT ARRAY[]::text[],
    
    exclusive int2 NOT NULL DEFAULT 0,
    verified int2 NOT NULL DEFAULT 0,
    featured int2 NOT NULL DEFAULT 0,
    
    status varchar(32) DEFAULT 'active',
    sort int4 NOT NULL DEFAULT 0,
    
    raw_payload jsonb NOT NULL DEFAULT '{}'::jsonb,
    
    creator varchar(64) DEFAULT '',
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater varchar(64) DEFAULT '',
    update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted int2 NOT NULL DEFAULT 0,
    tenant_id int8 NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX uk_offer_tenant_network_type_external ON river_offer (tenant_id, network_id, type, external_id) WHERE deleted = 0;
CREATE INDEX idx_offer_tenant_campaign ON river_offer (tenant_id, campaign_id) WHERE deleted = 0;
CREATE INDEX idx_offer_tenant_network ON river_offer (tenant_id, network_id) WHERE deleted = 0;
CREATE INDEX idx_offer_tenant_type_status ON river_offer (tenant_id, type, status) WHERE deleted = 0;
CREATE INDEX idx_offer_tenant_featured ON river_offer (tenant_id, featured) WHERE deleted = 0 AND featured = 1;
CREATE INDEX idx_offer_tenant_date_end ON river_offer (tenant_id, date_end) WHERE deleted = 0;
CREATE INDEX idx_offer_tenant_language ON river_offer (tenant_id, language) WHERE deleted = 0;

DROP SEQUENCE IF EXISTS river_offer_seq;
CREATE SEQUENCE river_offer_seq START 1;

COMMENT ON TABLE river_offer IS '统一内容表(Coupon/Deal/Banner/Article)';
COMMENT ON COLUMN river_offer.id IS '编号';
COMMENT ON COLUMN river_offer.network_id IS '联盟编号';
COMMENT ON COLUMN river_offer.campaign_id IS '商家编号';
COMMENT ON COLUMN river_offer.external_id IS '外部ID(联盟侧ID)';
COMMENT ON COLUMN river_offer.type IS '类型(0=coupon 1=deal 2=banner 3=article)';
COMMENT ON COLUMN river_offer.species IS '联盟原始类型(promocode/action等)';
COMMENT ON COLUMN river_offer.title IS '标题';
COMMENT ON COLUMN river_offer.description IS '描述';
COMMENT ON COLUMN river_offer.terms IS '使用条款';
COMMENT ON COLUMN river_offer.language IS '语言(en/ru/zh等)';
COMMENT ON COLUMN river_offer.promocode IS '优惠码';
COMMENT ON COLUMN river_offer.discount IS '折扣信息(如20%/¥50)';
COMMENT ON COLUMN river_offer.goto_link IS '目标链接(最终跳转URL)';
COMMENT ON COLUMN river_offer.url_tracking IS '追踪链接(联盟追踪URL)';
COMMENT ON COLUMN river_offer.image_url IS '图片URL';
COMMENT ON COLUMN river_offer.date_start IS '开始时间';
COMMENT ON COLUMN river_offer.date_end IS '结束时间(null=永久)';
COMMENT ON COLUMN river_offer.regions IS '目标地区数组';
COMMENT ON COLUMN river_offer.exclusive IS '是否独家(0否 1是)';
COMMENT ON COLUMN river_offer.verified IS '是否验证(0否 1是)';
COMMENT ON COLUMN river_offer.featured IS '是否精选(0否 1是)';
COMMENT ON COLUMN river_offer.status IS '状态(active/expired/upcoming)';
COMMENT ON COLUMN river_offer.sort IS '排序';
COMMENT ON COLUMN river_offer.raw_payload IS '原始API返回(JSON)';

-- =============================================
-- 7. river_offer_category - Offer与分类关联(多对多)
-- =============================================
DROP TABLE IF EXISTS river_offer_category;
CREATE TABLE river_offer_category (
    id int8 NOT NULL,
    offer_id int8 NOT NULL,
    category_id int8 NOT NULL,
    creator varchar(64) DEFAULT '',
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater varchar(64) DEFAULT '',
    update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted int2 NOT NULL DEFAULT 0,
    tenant_id int8 NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX uk_offer_category_tenant_mapping ON river_offer_category (tenant_id, offer_id, category_id) WHERE deleted = 0;
CREATE INDEX idx_offer_category_tenant_category ON river_offer_category (tenant_id, category_id) WHERE deleted = 0;
CREATE INDEX idx_offer_category_tenant_offer ON river_offer_category (tenant_id, offer_id) WHERE deleted = 0;

DROP SEQUENCE IF EXISTS river_offer_category_seq;
CREATE SEQUENCE river_offer_category_seq START 1;

COMMENT ON TABLE river_offer_category IS 'Offer与分类关联表(多对多)';
COMMENT ON COLUMN river_offer_category.id IS '编号';
COMMENT ON COLUMN river_offer_category.offer_id IS 'Offer编号';
COMMENT ON COLUMN river_offer_category.category_id IS '分类编号';

-- =============================================
-- 8. river_click_event - 点击事件表(分区)
-- =============================================
DROP TABLE IF EXISTS river_click_event;
CREATE TABLE river_click_event (
    id int8 NOT NULL,
    offer_id int8 NOT NULL,
    campaign_id int8 NOT NULL DEFAULT 0,
    network_id int8 NOT NULL DEFAULT 0,
    
    event_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    click_id varchar(64) DEFAULT '',
    sub_id varchar(128) DEFAULT '',
    
    user_id int8 NOT NULL DEFAULT 0,
    session_id varchar(64) DEFAULT '',
    
    ip inet NULL,
    user_agent varchar(512) DEFAULT '',
    referer varchar(1000) DEFAULT '',
    
    device_type varchar(32) DEFAULT '',
    country varchar(8) DEFAULT '',
    
    extra jsonb NOT NULL DEFAULT '{}'::jsonb,
    
    creator varchar(64) DEFAULT '',
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater varchar(64) DEFAULT '',
    update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted int2 NOT NULL DEFAULT 0,
    tenant_id int8 NOT NULL DEFAULT 0,
    PRIMARY KEY (id, event_time)
) PARTITION BY RANGE (event_time);

DROP SEQUENCE IF EXISTS river_click_event_seq;
CREATE SEQUENCE river_click_event_seq START 1;

COMMENT ON TABLE river_click_event IS '点击事件表(按月分区)';
COMMENT ON COLUMN river_click_event.id IS '编号';
COMMENT ON COLUMN river_click_event.offer_id IS 'Offer编号';
COMMENT ON COLUMN river_click_event.campaign_id IS '商家编号(冗余)';
COMMENT ON COLUMN river_click_event.network_id IS '联盟编号(冗余)';
COMMENT ON COLUMN river_click_event.event_time IS '事件时间';
COMMENT ON COLUMN river_click_event.click_id IS '点击ID(用于转化追踪)';
COMMENT ON COLUMN river_click_event.sub_id IS '子ID(自定义追踪参数)';
COMMENT ON COLUMN river_click_event.user_id IS '用户编号(0=匿名)';
COMMENT ON COLUMN river_click_event.session_id IS '会话ID';
COMMENT ON COLUMN river_click_event.ip IS 'IP地址';
COMMENT ON COLUMN river_click_event.user_agent IS '浏览器UA';
COMMENT ON COLUMN river_click_event.referer IS '来源页面';
COMMENT ON COLUMN river_click_event.device_type IS '设备类型(mobile/desktop/tablet)';
COMMENT ON COLUMN river_click_event.country IS '国家代码';
COMMENT ON COLUMN river_click_event.extra IS '额外参数(JSON)';

-- 创建2026年分区
CREATE TABLE river_click_event_2026_01 PARTITION OF river_click_event FOR VALUES FROM ('2026-01-01') TO ('2026-02-01');
CREATE TABLE river_click_event_2026_02 PARTITION OF river_click_event FOR VALUES FROM ('2026-02-01') TO ('2026-03-01');
CREATE TABLE river_click_event_2026_03 PARTITION OF river_click_event FOR VALUES FROM ('2026-03-01') TO ('2026-04-01');
CREATE TABLE river_click_event_2026_04 PARTITION OF river_click_event FOR VALUES FROM ('2026-04-01') TO ('2026-05-01');
CREATE TABLE river_click_event_2026_05 PARTITION OF river_click_event FOR VALUES FROM ('2026-05-01') TO ('2026-06-01');
CREATE TABLE river_click_event_2026_06 PARTITION OF river_click_event FOR VALUES FROM ('2026-06-01') TO ('2026-07-01');
CREATE TABLE river_click_event_2026_07 PARTITION OF river_click_event FOR VALUES FROM ('2026-07-01') TO ('2026-08-01');
CREATE TABLE river_click_event_2026_08 PARTITION OF river_click_event FOR VALUES FROM ('2026-08-01') TO ('2026-09-01');
CREATE TABLE river_click_event_2026_09 PARTITION OF river_click_event FOR VALUES FROM ('2026-09-01') TO ('2026-10-01');
CREATE TABLE river_click_event_2026_10 PARTITION OF river_click_event FOR VALUES FROM ('2026-10-01') TO ('2026-11-01');
CREATE TABLE river_click_event_2026_11 PARTITION OF river_click_event FOR VALUES FROM ('2026-11-01') TO ('2026-12-01');
CREATE TABLE river_click_event_2026_12 PARTITION OF river_click_event FOR VALUES FROM ('2026-12-01') TO ('2027-01-01');

-- 分区索引(每个分区自动继承)
CREATE INDEX idx_click_event_tenant_offer_time ON river_click_event (tenant_id, offer_id, event_time);
CREATE INDEX idx_click_event_tenant_time ON river_click_event (tenant_id, event_time);
CREATE INDEX idx_click_event_tenant_click_id ON river_click_event (tenant_id, click_id) WHERE click_id <> '';
CREATE INDEX idx_click_event_tenant_sub_id ON river_click_event (tenant_id, sub_id) WHERE sub_id <> '';

-- =============================================
-- 9. river_offer_stats_daily - 日统计汇总表
-- =============================================
DROP TABLE IF EXISTS river_offer_stats_daily;
CREATE TABLE river_offer_stats_daily (
    id int8 NOT NULL,
    stat_date date NOT NULL,
    offer_id int8 NOT NULL,
    campaign_id int8 NOT NULL DEFAULT 0,
    network_id int8 NOT NULL DEFAULT 0,
    
    clicks int8 NOT NULL DEFAULT 0,
    unique_clicks int8 NOT NULL DEFAULT 0,
    
    creator varchar(64) DEFAULT '',
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater varchar(64) DEFAULT '',
    update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted int2 NOT NULL DEFAULT 0,
    tenant_id int8 NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX uk_offer_stats_daily_tenant_date_offer ON river_offer_stats_daily (tenant_id, stat_date, offer_id) WHERE deleted = 0;
CREATE INDEX idx_offer_stats_daily_tenant_offer_date ON river_offer_stats_daily (tenant_id, offer_id, stat_date) WHERE deleted = 0;
CREATE INDEX idx_offer_stats_daily_tenant_date ON river_offer_stats_daily (tenant_id, stat_date) WHERE deleted = 0;

DROP SEQUENCE IF EXISTS river_offer_stats_daily_seq;
CREATE SEQUENCE river_offer_stats_daily_seq START 1;

COMMENT ON TABLE river_offer_stats_daily IS 'Offer日统计汇总表';
COMMENT ON COLUMN river_offer_stats_daily.id IS '编号';
COMMENT ON COLUMN river_offer_stats_daily.stat_date IS '统计日期';
COMMENT ON COLUMN river_offer_stats_daily.offer_id IS 'Offer编号';
COMMENT ON COLUMN river_offer_stats_daily.campaign_id IS '商家编号(冗余)';
COMMENT ON COLUMN river_offer_stats_daily.network_id IS '联盟编号(冗余)';
COMMENT ON COLUMN river_offer_stats_daily.clicks IS '点击次数';
COMMENT ON COLUMN river_offer_stats_daily.unique_clicks IS '独立点击次数(去重)';

-- =============================================
-- 初始化数据
-- =============================================

-- 初始化广告联盟
INSERT INTO river_affiliate_network (id, code, name, logo, website, api_base_url, api_config, status, remark, creator, create_time, updater, update_time, deleted, tenant_id)
VALUES 
(nextval('river_affiliate_network_seq'), 'admitad', 'Admitad', '', 'https://www.admitad.com', 'https://api.admitad.com', '{"client_id": "", "client_secret": "", "access_token": ""}', 0, '俄罗斯/国际广告联盟', 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 0, 1),
(nextval('river_affiliate_network_seq'), 'awin', 'Awin', '', 'https://www.awin.com', 'https://api.awin.com', '{"publisher_id": "", "api_token": ""}', 0, '全球广告联盟(含ShareASale)', 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 0, 1),
(nextval('river_affiliate_network_seq'), 'cj', 'CJ Affiliate', '', 'https://www.cj.com', 'https://commissions.api.cj.com', '{"website_id": "", "personal_access_token": ""}', 0, 'Commission Junction', 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 0, 1),
(nextval('river_affiliate_network_seq'), 'custom', 'Custom', '', '', '', '{}', 0, '自定义/手动录入', 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 0, 1);

-- 初始化示例分类
INSERT INTO river_category (id, parent_id, name, slug, icon, sort, status, creator, create_time, updater, update_time, deleted, tenant_id)
VALUES
(nextval('river_category_seq'), 0, 'Electronics', 'electronics', 'ep:monitor', 1, 0, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 0, 1),
(nextval('river_category_seq'), 0, 'Fashion', 'fashion', 'ep:goods', 2, 0, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 0, 1),
(nextval('river_category_seq'), 0, 'Travel', 'travel', 'ep:place', 3, 0, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 0, 1),
(nextval('river_category_seq'), 0, 'Food & Dining', 'food-dining', 'ep:food', 4, 0, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 0, 1),
(nextval('river_category_seq'), 0, 'Health & Beauty', 'health-beauty', 'ep:first-aid-kit', 5, 0, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 0, 1);
