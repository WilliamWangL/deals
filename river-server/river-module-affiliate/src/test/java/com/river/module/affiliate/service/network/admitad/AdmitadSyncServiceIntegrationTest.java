package com.river.module.affiliate.service.network.admitad;

import com.river.module.affiliate.dal.dataobject.NetworkCredentialDO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Admitad 完整同步流程集成测试
 *
 * 测试从 API 获取 campaigns 并同步到本地数据库
 * 需要启动完整的 Spring Boot 应用上下文
 *
 * 手动运行: mvn test -Dtest=AdmitadSyncServiceIntegrationTest
 */
class AdmitadSyncServiceIntegrationTest {

    private AdmitadClient admitadClient;
    private AdmitadSyncService admitadSyncService;
    private NetworkCredentialDO credential;

    private static final String CLIENT_ID = "0SBUL9QO86fkgnSxVAszxdWzm86XEb";
    private static final String CLIENT_SECRET = "v8TIdZLrQufEPCrmEHN92Tdj1muGyg";
    private static final String SCOPE = "public_data advcampaigns advcampaigns_for_website coupons coupons_for_website validate_links deeplink_generator statistics";
    private static final String WEBSITE_ID = "2749844";

    @BeforeEach
    void setUp() {
        admitadClient = new AdmitadClient();

        String credentialsJson = String.format(
            "{\"clientId\":\"%s\",\"clientSecret\":\"%s\",\"scope\":\"%s\",\"websiteId\":\"%s\"}",
            CLIENT_ID, CLIENT_SECRET, SCOPE, WEBSITE_ID
        );

        credential = new NetworkCredentialDO();
        credential.setId(1L);
        credential.setNetworkId(1L);
        credential.setCredentials(credentialsJson);
        credential.setEnabled(true);
    }

    @Test
    @Disabled("需要数据库连接 - 通过 Spring Boot 应用运行")
    void testSyncCampaigns() {
        // 此测试需要完整的 Spring 上下文
        // 通过启动应用后手动触发 AdmitadSyncJob 来测试
        // 或者通过 API 调用 /admin-api/affiliate/sync/admitad
    }

    @Test
    void testCampaignDataMapping() {
        // 测试数据映射逻辑 - 不需要数据库
        var campaigns = admitadClient.getCampaigns(credential, 0, 5);

        assertFalse(campaigns.isEmpty(), "Should fetch at least one campaign");

        var campaign = campaigns.get(0);
        assertNotNull(campaign.getId(), "Campaign should have ID");
        assertNotNull(campaign.getName(), "Campaign should have name");

        System.out.println("========================================");
        System.out.println("Campaign data mapping test:");
        System.out.println("========================================");
        System.out.println("ID: " + campaign.getId());
        System.out.println("Name: " + campaign.getName());
        System.out.println("Site URL: " + campaign.getSiteUrl());
        System.out.println("Status: " + campaign.getStatus());
        System.out.println("Rating: " + campaign.getRating());
        System.out.println("Description: " + (campaign.getDescription() != null ?
            campaign.getDescription().substring(0, Math.min(100, campaign.getDescription().length())) + "..." : "null"));

        if (campaign.getCategories() != null && !campaign.getCategories().isEmpty()) {
            System.out.println("Categories: " + campaign.getCategories().size());
            campaign.getCategories().forEach(c -> System.out.println("  - " + c.getName()));
        }

        if (campaign.getRegions() != null && !campaign.getRegions().isEmpty()) {
            System.out.println("Regions: " + campaign.getRegions().size());
            campaign.getRegions().forEach(r -> System.out.println("  - " + r.getRegion()));
        }

        if (campaign.getActions() != null && !campaign.getActions().isEmpty()) {
            System.out.println("Actions: " + campaign.getActions().size());
            campaign.getActions().forEach(a ->
                System.out.println("  - " + a.getName() + " (" + a.getType() + "): " + a.getPayment()));
        }

        // 打印完整的 JSON（通过 ObjectMapper）
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.SerializationFeature INDENT_OUTPUT = com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
            mapper.enable(INDENT_OUTPUT);
            String json = mapper.writeValueAsString(campaign);
            System.out.println("\n========================================");
            System.out.println("Complete JSON for first campaign:");
            System.out.println("========================================");
            System.out.println(json);
        } catch (Exception e) {
            System.out.println("Failed to serialize to JSON: " + e.getMessage());
        }
    }

    @Test
    void testDeeplinkGeneration() {
        // 对比手工构建的链接和 API 返回的 deeplink
        Long campaignId = 92L; // 使用 Lineage 2 的 campaign ID
        String subid = "test_click_123";

        // 手工构建的链接（当前实现方式）
        String manualLink = String.format(
            "https://ad.admitad.com/g/%s/?subid=%s&subid1={sub1}&subid2={sub2}",
            campaignId, subid);

        // API 生成的 deeplink
        String apiDeeplink = admitadClient.generateDeeplink(credential, campaignId, subid);

        System.out.println("========================================");
        System.out.println("Deeplink 对比测试 (Campaign ID: " + campaignId + ")");
        System.out.println("========================================");
        System.out.println("手工构建: " + manualLink);
        System.out.println("API 生成: " + apiDeeplink);

        if (apiDeeplink != null) {
            System.out.println("\n分析:");
            System.out.println("  - 基础 URL 一致: " + manualLink.startsWith("https://ad.admitad.com/g/" + campaignId));
            System.out.println("  - subid 参数一致: " + apiDeeplink.contains("subid=" + subid));

            // 提取 API 返回链接的关键部分
            if (apiDeeplink.contains("&ulp=")) {
                String ulp = apiDeeplink.substring(apiDeeplink.indexOf("&ulp=") + 5);
                System.out.println("  - ULP 参数: " + ulp);
            }

            // 检查是否有其他参数
            String manualBase = "https://ad.admitad.com/g/" + campaignId + "/";
            String apiBase = apiDeeplink.contains("?") ? apiDeeplink.substring(0, apiDeeplink.indexOf("?")) : apiDeeplink;
            System.out.println("  - 基础路径一致: " + manualBase.equals(apiBase));
        }
    }
}
