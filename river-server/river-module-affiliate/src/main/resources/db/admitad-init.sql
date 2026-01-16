-- Admitad 联盟网络初始化脚本
-- 使用前请替换凭证中的占位符为真实值

-- 1. 插入 Admitad 网络记录
INSERT INTO river_affiliate_network (id, code, name, type, api_base_url, status, website_url, logo_url, description, creator, create_time, updater, update_time, deleted, tenant_id)
VALUES (1, 'admitad', 'Admitad', 1, 'https://api.admitad.com', 1, 'https://www.admitad.com', 'https://www.admitad.com/favicon.ico', 'Admitad CPS Affiliate Network', '1', NOW(), '1', NOW(), 0, 1)
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    api_base_url = EXCLUDED.api_base_url,
    status = EXCLUDED.status,
    update_time = NOW();

-- 2. 插入凭证记录
-- 注意：请替换 YOUR_CLIENT_ID, YOUR_CLIENT_SECRET, YOUR_WEBSITE_ID 为真实值
INSERT INTO river_affiliate_network_credential (id, network_id, auth_type, credentials, enabled, creator, create_time, updater, update_time, deleted, tenant_id)
VALUES (
    1,
    1,
    1, -- OAUTH2
    '{"clientId":"YOUR_CLIENT_ID","clientSecret":"YOUR_CLIENT_SECRET","scope":"public_data advcampaigns advcampaigns_for_website coupons coupons_for_website validate_links deeplink_generator statistics","websiteId":"YOUR_WEBSITE_ID"}',
    true,
    '1',
    NOW(),
    '1',
    NOW(),
    0,
    1
)
ON CONFLICT (id) DO UPDATE SET
    credentials = EXCLUDED.credentials,
    enabled = EXCLUDED.enabled,
    update_time = NOW();

  ALTER TABLE river_affiliate_merchant ALTER COLUMN rating TYPE numeric(5,2); 
-- 验证插入结果
SELECT n.id, n.code, n.name, n.status, c.enabled
FROM river_affiliate_network n
LEFT JOIN river_affiliate_network_credential c ON n.id = c.network_id AND c.deleted = 0
WHERE n.code = 'admitad' AND n.deleted = 0;

