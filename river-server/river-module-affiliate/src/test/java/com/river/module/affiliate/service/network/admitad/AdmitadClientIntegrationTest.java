package com.river.module.affiliate.service.network.admitad;

import com.river.module.affiliate.dal.dataobject.NetworkCredentialDO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Admitad API 集成测试
 *
 * 手动运行：去掉 @Disabled 注解，或使用命令行：
 * mvn test -Dtest=AdmitadClientIntegrationTest -DfailIfNoTests=false
 */
class AdmitadClientIntegrationTest {

    private AdmitadClient admitadClient;
    private NetworkCredentialDO credential;

    // TODO: 替换为真实凭证后去掉 @Disabled 运行测试
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
    @Disabled("集成测试 - 手动运行: mvn test -Dtest=AdmitadClientIntegrationTest#testGetCampaigns_Success")
    void testGetCampaigns_Success() {
        // When
        List<AdmitadCampaign> campaigns = admitadClient.getCampaigns(credential, 0, 10);

        // Then
        assertNotNull(campaigns);
        System.out.println("========================================");
        System.out.println("Successfully fetched " + campaigns.size() + " campaigns from Admitad");
        System.out.println("========================================");

        for (AdmitadCampaign campaign : campaigns) {
            System.out.println("Campaign: " + campaign.getId() + " - " + campaign.getName());
            System.out.println("  Site URL: " + campaign.getSiteUrl());
            System.out.println("  Status: " + campaign.getStatus());
            System.out.println("  Rating: " + campaign.getRating());

            if (campaign.getActions() != null) {
                System.out.println("  Actions (" + campaign.getActions().size() + "):");
                for (AdmitadCampaign.Action action : campaign.getActions()) {
                    System.out.println("    - " + action.getName() + " (" + action.getType() + "): " + action.getPayment());
                }
            }
            System.out.println();
        }
    }

    @Test
    @Disabled("需要有效的 Admitad API 凭证 - 手动运行时去掉此注解")
    void testGetCampaigns_Pagination() {
        // When - 获取第一页
        List<AdmitadCampaign> page1 = admitadClient.getCampaigns(credential, 0, 5);
        // 获取第二页
        List<AdmitadCampaign> page2 = admitadClient.getCampaigns(credential, 5, 5);

        // Then
        assertNotNull(page1);
        assertNotNull(page2);

        System.out.println("Page 1: " + page1.size() + " campaigns");
        System.out.println("Page 2: " + page2.size() + " campaigns");

        // 如果有足够的数据，验证分页正确
        if (!page1.isEmpty() && !page2.isEmpty()) {
            assertNotEquals(page1.get(0).getId(), page2.get(0).getId(), "分页应该返回不同的数据");
        }
    }
}
