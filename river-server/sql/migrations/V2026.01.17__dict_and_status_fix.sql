-- =====================================================
-- River 模块字典改造迁移脚本
-- 1. 补充字典类型和数据
-- 2. 状态值反转（0↔1 互换）以匹配 COMMON_STATUS
-- 3. 修改列默认值
-- =====================================================

-- =====================================================
-- 1. 补充字典类型
-- =====================================================

-- 新增 coupon_type 字典类型
INSERT INTO system_dict_type (id, name, type, status, remark, creator, create_time, updater, update_time, deleted)
VALUES (20052, '优惠券类型', 'coupon_type', 0, '优惠券类型：优惠码、促销、Deal', '1', NOW(), '1', NOW(), 0)
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- 2. 补充字典数据
-- =====================================================

-- affiliate_commission_type 补充 CPL 和 CPM
INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted)
VALUES
(20007, 4, 'CPL (按潜在客户)', '4', 'affiliate_commission_type', 0, 'warning', '', '', '1', NOW(), '1', NOW(), 0),
(20008, 5, 'CPM (按千次展示)', '5', 'affiliate_commission_type', 0, 'info', '', '', '1', NOW(), '1', NOW(), 0)
ON CONFLICT (id) DO NOTHING;

-- coupon_type 字典数据
INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted)
VALUES
(20150, 1, '优惠码', '1', 'coupon_type', 0, 'primary', '', '', '1', NOW(), '1', NOW(), 0),
(20151, 2, '促销', '2', 'coupon_type', 0, 'success', '', '', '1', NOW(), '1', NOW(), 0),
(20152, 3, 'Deal', '3', 'coupon_type', 0, 'warning', '', '', '1', NOW(), '1', NOW(), 0)
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- 3. 状态值反转（0↔1 互换）
-- COMMON_STATUS: 0=开启, 1=关闭
-- River 原来: 1=开启, 0=关闭
-- =====================================================

-- Affiliate 模块
UPDATE river_affiliate_network SET status = CASE WHEN status = 1 THEN 0 WHEN status = 0 THEN 1 ELSE status END WHERE deleted = 0;
UPDATE river_affiliate_merchant SET status = CASE WHEN status = 1 THEN 0 WHEN status = 0 THEN 1 ELSE status END WHERE deleted = 0;
UPDATE river_affiliate_offer SET status = CASE WHEN status = 1 THEN 0 WHEN status = 0 THEN 1 ELSE status END WHERE deleted = 0;
UPDATE river_affiliate_category SET status = CASE WHEN status = 1 THEN 0 WHEN status = 0 THEN 1 ELSE status END WHERE deleted = 0;

-- Coupon 模块
UPDATE river_coupon_coupon SET status = CASE WHEN status = 1 THEN 0 WHEN status = 0 THEN 1 ELSE status END WHERE deleted = 0;
UPDATE river_coupon_deal SET status = CASE WHEN status = 1 THEN 0 WHEN status = 0 THEN 1 ELSE status END WHERE deleted = 0;

-- Blog 模块
UPDATE river_blog_author SET status = CASE WHEN status = 1 THEN 0 WHEN status = 0 THEN 1 ELSE status END WHERE deleted = 0;
UPDATE river_blog_tag SET status = CASE WHEN status = 1 THEN 0 WHEN status = 0 THEN 1 ELSE status END WHERE deleted = 0;
-- blog_post status 是发布状态，不是开启/关闭，不需要反转

-- Campaign 模块
UPDATE river_campaign_traffic_source SET status = CASE WHEN status = 1 THEN 0 WHEN status = 0 THEN 1 ELSE status END WHERE deleted = 0;
UPDATE river_campaign_campaign SET status = CASE WHEN status = 1 THEN 0 WHEN status = 0 THEN 1 ELSE status END WHERE deleted = 0;
UPDATE river_campaign_ad_group SET status = CASE WHEN status = 1 THEN 0 WHEN status = 0 THEN 1 ELSE status END WHERE deleted = 0;
UPDATE river_campaign_landing_page SET status = CASE WHEN status = 1 THEN 0 WHEN status = 0 THEN 1 ELSE status END WHERE deleted = 0;
UPDATE river_campaign_currency SET status = CASE WHEN status = 1 THEN 0 WHEN status = 0 THEN 1 ELSE status END WHERE deleted = 0;

-- Tracking 模块
UPDATE river_tracking_link SET status = CASE WHEN status = 1 THEN 0 WHEN status = 0 THEN 1 ELSE status END WHERE deleted = 0;

-- =====================================================
-- 4. 修改列默认值
-- 新记录默认 status = 0（开启）
-- =====================================================

-- Affiliate 模块
ALTER TABLE river_affiliate_network ALTER COLUMN status SET DEFAULT 0;
ALTER TABLE river_affiliate_merchant ALTER COLUMN status SET DEFAULT 0;
ALTER TABLE river_affiliate_offer ALTER COLUMN status SET DEFAULT 0;
ALTER TABLE river_affiliate_category ALTER COLUMN status SET DEFAULT 0;

-- Coupon 模块
ALTER TABLE river_coupon_coupon ALTER COLUMN status SET DEFAULT 0;
ALTER TABLE river_coupon_deal ALTER COLUMN status SET DEFAULT 0;

-- Blog 模块
ALTER TABLE river_blog_author ALTER COLUMN status SET DEFAULT 0;
ALTER TABLE river_blog_tag ALTER COLUMN status SET DEFAULT 0;

-- Campaign 模块
ALTER TABLE river_campaign_traffic_source ALTER COLUMN status SET DEFAULT 0;
ALTER TABLE river_campaign_campaign ALTER COLUMN status SET DEFAULT 0;
ALTER TABLE river_campaign_ad_group ALTER COLUMN status SET DEFAULT 0;
ALTER TABLE river_campaign_landing_page ALTER COLUMN status SET DEFAULT 0;
ALTER TABLE river_campaign_currency ALTER COLUMN status SET DEFAULT 0;

-- Tracking 模块
ALTER TABLE river_tracking_link ALTER COLUMN status SET DEFAULT 0;
