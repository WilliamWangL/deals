-- V20260122__tracking_redesign.sql
-- 通用追踪系统改造

-- 1. 修改 river_tracking_link 表
ALTER TABLE river_tracking_link ADD COLUMN target_type SMALLINT;
ALTER TABLE river_tracking_link ADD COLUMN target_id BIGINT;
ALTER TABLE river_tracking_link ADD COLUMN tracking_url TEXT;

-- 迁移现有数据: offer_id -> target_type=2 (OFFER), target_id=offer_id
UPDATE river_tracking_link SET target_type = 2, target_id = offer_id WHERE offer_id IS NOT NULL;

-- 设置非空约束
ALTER TABLE river_tracking_link ALTER COLUMN target_type SET NOT NULL;
ALTER TABLE river_tracking_link ALTER COLUMN target_id SET NOT NULL;

-- 删除旧字段
ALTER TABLE river_tracking_link DROP COLUMN offer_id;

-- 创建新索引
DROP INDEX IF EXISTS idx_tracking_link_offer;
CREATE UNIQUE INDEX uk_tracking_link_target ON river_tracking_link(target_type, target_id, tenant_id) WHERE deleted = 0;

COMMENT ON COLUMN river_tracking_link.target_type IS '目标类型: 1=商家, 2=Offer, 3=Deal, 4=优惠券';
COMMENT ON COLUMN river_tracking_link.target_id IS '目标实体 ID';
COMMENT ON COLUMN river_tracking_link.tracking_url IS '追加参数后的完整追踪链接';

-- 2. 修改 river_tracking_click 表
ALTER TABLE river_tracking_click ADD COLUMN target_type SMALLINT;
ALTER TABLE river_tracking_click ADD COLUMN target_id BIGINT;
ALTER TABLE river_tracking_click ADD COLUMN merchant_id BIGINT;

-- 迁移现有数据
UPDATE river_tracking_click SET target_type = 2, target_id = offer_id WHERE offer_id IS NOT NULL;

-- 设置非空约束
ALTER TABLE river_tracking_click ALTER COLUMN target_type SET NOT NULL;
ALTER TABLE river_tracking_click ALTER COLUMN target_id SET NOT NULL;

-- 删除旧字段
ALTER TABLE river_tracking_click DROP COLUMN offer_id;

-- 创建新索引
DROP INDEX IF EXISTS idx_tracking_click_offer;
CREATE INDEX idx_tracking_click_target ON river_tracking_click(target_type, target_id);
CREATE INDEX idx_tracking_click_merchant ON river_tracking_click(merchant_id);

COMMENT ON COLUMN river_tracking_click.target_type IS '目标类型: 1=商家, 2=Offer, 3=Deal, 4=优惠券';
COMMENT ON COLUMN river_tracking_click.target_id IS '目标实体 ID';
COMMENT ON COLUMN river_tracking_click.merchant_id IS '商家 ID（冗余字段，便于统计）';

-- 3. 修改 river_tracking_conversion 表（新增冗余字段）
ALTER TABLE river_tracking_conversion ADD COLUMN target_type SMALLINT;
ALTER TABLE river_tracking_conversion ADD COLUMN target_id BIGINT;
ALTER TABLE river_tracking_conversion ADD COLUMN merchant_id BIGINT;

COMMENT ON COLUMN river_tracking_conversion.target_type IS '目标类型（冗余字段）';
COMMENT ON COLUMN river_tracking_conversion.target_id IS '目标实体 ID（冗余字段）';
COMMENT ON COLUMN river_tracking_conversion.merchant_id IS '商家 ID（冗余字段）';

-- 4. 修改 river_affiliate_offer 表
ALTER TABLE river_affiliate_offer RENAME COLUMN tracking_url_template TO goto_url;

-- 5. 修改 river_affiliate_merchant 表
ALTER TABLE river_affiliate_merchant ADD COLUMN default_offer_id BIGINT;
COMMENT ON COLUMN river_affiliate_merchant.default_offer_id IS '默认 Offer ID，用于 Visit Store 追踪';
