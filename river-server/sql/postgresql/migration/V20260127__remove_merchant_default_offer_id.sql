-- 移除 merchant.default_offer_id 字段
-- 改为动态选择 Offer，不再为 Merchant 创建固定的 TrackingLink

-- 1. 删除 merchant 表的 default_offer_id 列
ALTER TABLE river_affiliate_merchant DROP COLUMN IF EXISTS default_offer_id;

-- 2. 删除 target_type=1 (MERCHANT) 的 tracking_link 记录
DELETE FROM river_tracking_link WHERE target_type = 1;
