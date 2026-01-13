-- river_stats 模块 SQL 脚本
-- 适用于 PostgreSQL 17

-- 日报统计表
CREATE TABLE river_stats_daily (
    id int8 NOT NULL,
    date date NOT NULL,
    dimension_type int2 NOT NULL,
    dimension_id int8 NOT NULL,
    clicks int4 NOT NULL DEFAULT 0,
    conversions int4 NOT NULL DEFAULT 0,
    revenue numeric(12,4) NOT NULL DEFAULT 0,
    cost numeric(12,4) NOT NULL DEFAULT 0,
    profit numeric(12,4) NOT NULL DEFAULT 0,
    epc numeric(10,4) NOT NULL DEFAULT 0,
    cr numeric(8,4) NOT NULL DEFAULT 0,
    roi numeric(10,4) NOT NULL DEFAULT 0,
    creator varchar(64) DEFAULT '',
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater varchar(64) DEFAULT '',
    update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted int2 NOT NULL DEFAULT 0,
    tenant_id int8 NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE INDEX idx_stats_daily_date ON river_stats_daily(date);
CREATE INDEX idx_stats_daily_dimension ON river_stats_daily(dimension_type, dimension_id);
CREATE INDEX idx_stats_daily_tenant ON river_stats_daily(tenant_id);
CREATE UNIQUE INDEX uk_stats_daily_dimension_date ON river_stats_daily(date, dimension_type, dimension_id, tenant_id) WHERE deleted = 0;

COMMENT ON TABLE river_stats_daily IS '日报统计表';
COMMENT ON COLUMN river_stats_daily.id IS '主键';
COMMENT ON COLUMN river_stats_daily.date IS '统计日期';
COMMENT ON COLUMN river_stats_daily.dimension_type IS '维度类型：1-OFFER 2-CAMPAIGN 3-SOURCE 4-MERCHANT 5-CATEGORY 6-AUTHOR';
COMMENT ON COLUMN river_stats_daily.dimension_id IS '维度 ID';
COMMENT ON COLUMN river_stats_daily.clicks IS '点击数';
COMMENT ON COLUMN river_stats_daily.conversions IS '转化数';
COMMENT ON COLUMN river_stats_daily.revenue IS '收入';
COMMENT ON COLUMN river_stats_daily.cost IS '成本';
COMMENT ON COLUMN river_stats_daily.profit IS '利润';
COMMENT ON COLUMN river_stats_daily.epc IS '每次点击收益';
COMMENT ON COLUMN river_stats_daily.cr IS '转化率';
COMMENT ON COLUMN river_stats_daily.roi IS '投资回报率';

-- 小时统计表
CREATE TABLE river_stats_hourly (
    id int8 NOT NULL,
    hour timestamp NOT NULL,
    dimension_type int2 NOT NULL,
    dimension_id int8 NOT NULL,
    clicks int4 NOT NULL DEFAULT 0,
    conversions int4 NOT NULL DEFAULT 0,
    revenue numeric(12,4) NOT NULL DEFAULT 0,
    cost numeric(12,4) NOT NULL DEFAULT 0,
    creator varchar(64) DEFAULT '',
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater varchar(64) DEFAULT '',
    update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted int2 NOT NULL DEFAULT 0,
    tenant_id int8 NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE INDEX idx_stats_hourly_hour ON river_stats_hourly(hour);
CREATE INDEX idx_stats_hourly_dimension ON river_stats_hourly(dimension_type, dimension_id);
CREATE INDEX idx_stats_hourly_tenant ON river_stats_hourly(tenant_id);
CREATE UNIQUE INDEX uk_stats_hourly_dimension_hour ON river_stats_hourly(hour, dimension_type, dimension_id, tenant_id) WHERE deleted = 0;

COMMENT ON TABLE river_stats_hourly IS '小时统计表（保留 7 天）';
COMMENT ON COLUMN river_stats_hourly.id IS '主键';
COMMENT ON COLUMN river_stats_hourly.hour IS '小时时间点';
COMMENT ON COLUMN river_stats_hourly.dimension_type IS '维度类型：1-OFFER 2-CAMPAIGN 3-SOURCE 4-MERCHANT 5-CATEGORY 6-AUTHOR';
COMMENT ON COLUMN river_stats_hourly.dimension_id IS '维度 ID';
COMMENT ON COLUMN river_stats_hourly.clicks IS '点击数';
COMMENT ON COLUMN river_stats_hourly.conversions IS '转化数';
COMMENT ON COLUMN river_stats_hourly.revenue IS '收入';
COMMENT ON COLUMN river_stats_hourly.cost IS '成本';
