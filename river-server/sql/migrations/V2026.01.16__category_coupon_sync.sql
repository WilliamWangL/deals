-- =====================================================
-- 分类与优惠券同步功能 - 数据库迁移脚本
-- 版本: V2026.01.16
-- 描述: 添加分类映射表，扩展 Coupon/Deal 表字段
-- =====================================================

-- =====================================================
-- 1. 创建分类映射表
-- =====================================================
CREATE TABLE IF NOT EXISTS river_affiliate_category_mapping (
    id bigint NOT NULL,
    network_id bigint NOT NULL,                              -- 联盟网络 ID
    external_id varchar(50) NOT NULL,                        -- 联盟分类 ID
    external_name varchar(200),                              -- 联盟分类名称（原始）
    external_parent_id varchar(50),                          -- 联盟父分类 ID
    category_id bigint,                                      -- 本地分类 ID（可为空，表示未映射）
    auto_created boolean DEFAULT false,                      -- 是否自动创建的映射
    creator varchar(64) DEFAULT '',
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater varchar(64) DEFAULT '',
    update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted smallint NOT NULL DEFAULT 0,
    tenant_id bigint NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

-- 联盟+外部ID唯一索引
CREATE UNIQUE INDEX IF NOT EXISTS uk_category_mapping_network_external
ON river_affiliate_category_mapping(network_id, external_id)
WHERE deleted = 0;

-- 本地分类索引（用于查询哪些联盟分类映射到了某个本地分类）
CREATE INDEX IF NOT EXISTS idx_category_mapping_category_id
ON river_affiliate_category_mapping(category_id)
WHERE deleted = 0;

COMMENT ON TABLE river_affiliate_category_mapping IS '联盟分类映射表';
COMMENT ON COLUMN river_affiliate_category_mapping.network_id IS '联盟网络ID';
COMMENT ON COLUMN river_affiliate_category_mapping.external_id IS '联盟原始分类ID';
COMMENT ON COLUMN river_affiliate_category_mapping.external_name IS '联盟原始分类名称';
COMMENT ON COLUMN river_affiliate_category_mapping.external_parent_id IS '联盟父分类ID';
COMMENT ON COLUMN river_affiliate_category_mapping.category_id IS '映射的本地分类ID';
COMMENT ON COLUMN river_affiliate_category_mapping.auto_created IS '是否自动创建';

-- =====================================================
-- 2. 扩展 river_coupon_coupon 表
-- =====================================================
-- 添加外部ID字段（用于联盟同步去重）
ALTER TABLE river_coupon_coupon
ADD COLUMN IF NOT EXISTS external_id varchar(100);

-- 添加联盟网络ID
ALTER TABLE river_coupon_coupon
ADD COLUMN IF NOT EXISTS network_id bigint;

-- 添加标题字段
ALTER TABLE river_coupon_coupon
ADD COLUMN IF NOT EXISTS title varchar(300);

-- 添加适用地区（ISO 代码，逗号分隔）
ALTER TABLE river_coupon_coupon
ADD COLUMN IF NOT EXISTS regions text;

-- 添加分类ID（逗号分隔）
ALTER TABLE river_coupon_coupon
ADD COLUMN IF NOT EXISTS category_ids text;

-- 添加图片URL
ALTER TABLE river_coupon_coupon
ADD COLUMN IF NOT EXISTS image_url varchar(500);

-- 添加跳转链接
ALTER TABLE river_coupon_coupon
ADD COLUMN IF NOT EXISTS goto_url varchar(1000);

-- 添加是否独家
ALTER TABLE river_coupon_coupon
ADD COLUMN IF NOT EXISTS exclusive boolean DEFAULT false;

-- 添加优惠券类型：1=promocode, 2=sale, 3=deal
ALTER TABLE river_coupon_coupon
ADD COLUMN IF NOT EXISTS coupon_type smallint DEFAULT 1;

-- 创建联盟+外部ID唯一索引（防止重复同步）
CREATE UNIQUE INDEX IF NOT EXISTS uk_coupon_network_external
ON river_coupon_coupon(network_id, external_id)
WHERE deleted = 0 AND external_id IS NOT NULL;

-- 创建地区索引（用于按地区筛选）
CREATE INDEX IF NOT EXISTS idx_coupon_regions
ON river_coupon_coupon USING gin(to_tsvector('simple', COALESCE(regions, '')));

COMMENT ON COLUMN river_coupon_coupon.external_id IS '联盟原始ID';
COMMENT ON COLUMN river_coupon_coupon.network_id IS '来源联盟网络ID';
COMMENT ON COLUMN river_coupon_coupon.title IS '优惠券标题';
COMMENT ON COLUMN river_coupon_coupon.regions IS '适用地区(ISO代码,逗号分隔)';
COMMENT ON COLUMN river_coupon_coupon.category_ids IS '分类ID(逗号分隔)';
COMMENT ON COLUMN river_coupon_coupon.image_url IS '图片URL';
COMMENT ON COLUMN river_coupon_coupon.goto_url IS '跳转链接';
COMMENT ON COLUMN river_coupon_coupon.exclusive IS '是否独家';
COMMENT ON COLUMN river_coupon_coupon.coupon_type IS '类型:1=promocode,2=sale,3=deal';

-- =====================================================
-- 3. 扩展 river_coupon_deal 表
-- =====================================================
-- 添加外部ID字段
ALTER TABLE river_coupon_deal
ADD COLUMN IF NOT EXISTS external_id varchar(100);

-- 添加联盟网络ID
ALTER TABLE river_coupon_deal
ADD COLUMN IF NOT EXISTS network_id bigint;

-- 添加适用地区
ALTER TABLE river_coupon_deal
ADD COLUMN IF NOT EXISTS regions text;

-- 添加分类ID
ALTER TABLE river_coupon_deal
ADD COLUMN IF NOT EXISTS category_ids text;

-- 添加跳转链接
ALTER TABLE river_coupon_deal
ADD COLUMN IF NOT EXISTS goto_url varchar(1000);

-- 添加是否独家
ALTER TABLE river_coupon_deal
ADD COLUMN IF NOT EXISTS exclusive boolean DEFAULT false;

-- 创建联盟+外部ID唯一索引
CREATE UNIQUE INDEX IF NOT EXISTS uk_deal_network_external
ON river_coupon_deal(network_id, external_id)
WHERE deleted = 0 AND external_id IS NOT NULL;

-- 创建地区索引
CREATE INDEX IF NOT EXISTS idx_deal_regions
ON river_coupon_deal USING gin(to_tsvector('simple', COALESCE(regions, '')));

COMMENT ON COLUMN river_coupon_deal.external_id IS '联盟原始ID';
COMMENT ON COLUMN river_coupon_deal.network_id IS '来源联盟网络ID';
COMMENT ON COLUMN river_coupon_deal.regions IS '适用地区(ISO代码,逗号分隔)';
COMMENT ON COLUMN river_coupon_deal.category_ids IS '分类ID(逗号分隔)';
COMMENT ON COLUMN river_coupon_deal.goto_url IS '跳转链接';
COMMENT ON COLUMN river_coupon_deal.exclusive IS '是否独家';

-- =====================================================
-- 4. 验证脚本
-- =====================================================
-- 执行完成后可运行以下查询验证：
--
-- -- 检查分类映射表
-- SELECT COUNT(*) FROM river_affiliate_category_mapping;
--
-- -- 检查 Coupon 表新字段
-- SELECT column_name FROM information_schema.columns
-- WHERE table_name = 'river_coupon_coupon'
-- AND column_name IN ('external_id', 'network_id', 'title', 'regions', 'category_ids', 'image_url', 'goto_url', 'exclusive', 'coupon_type');
--
-- -- 检查 Deal 表新字段
-- SELECT column_name FROM information_schema.columns
-- WHERE table_name = 'river_coupon_deal'
-- AND column_name IN ('external_id', 'network_id', 'regions', 'category_ids', 'goto_url', 'exclusive');
