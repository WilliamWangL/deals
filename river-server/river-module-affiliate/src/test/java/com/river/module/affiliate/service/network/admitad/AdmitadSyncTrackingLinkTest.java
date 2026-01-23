package com.river.module.affiliate.service.network.admitad;

import com.river.module.affiliate.dal.dataobject.NetworkCredentialDO;
import com.river.module.affiliate.dal.mysql.MerchantMapper;
import com.river.module.affiliate.dal.mysql.OfferMapper;
import com.river.module.affiliate.dal.dataobject.MerchantDO;
import com.river.module.affiliate.dal.dataobject.OfferDO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Admitad 同步集成测试 - 验证 TrackingLink 创建
 * 需要完整的 Spring Boot 应用上下文
 */
@SpringBootTest
@ActiveProfiles("unit-test")
@Disabled("需要数据库和应用启动 - 手动运行测试")
class AdmitadSyncTrackingLinkTest {

    @Autowired
    private AdmitadSyncService admitadSyncService;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private OfferMapper offerMapper;

    private static final String CLIENT_ID = "0SBUL9QO86fkgnSxVAszxdWzm86XEb";
    private static final String CLIENT_SECRET = "v8TIdZLrQufEPCrmEHN92Tdj1muGyg";
    private static final String SCOPE = "public_data advcampaigns advcampaigns_for_website coupons coupons_for_website validate_links deeplink_generator statistics";
    private static final String WEBSITE_ID = "2749844";

    /**
     * 测试 Campaign 同步后 TrackingLink 是否被创建
     * 运行方式: 启动应用后执行
     */
    @Test
    void testSyncCampaignAndCreateTrackingLink() {
        // 准备凭证
        NetworkCredentialDO credential = new NetworkCredentialDO();
        credential.setId(1L);
        credential.setNetworkId(1L);
        String credentialsJson = String.format(
            "{\"clientId\":\"%s\",\"clientSecret\":\"%s\",\"scope\":\"%s\",\"websiteId\":\"%s\"}",
            CLIENT_ID, CLIENT_SECRET, SCOPE, WEBSITE_ID);
        credential.setCredentials(credentialsJson);
        credential.setEnabled(true);

        // 执行同步
        admitadSyncService.syncCampaigns(credential);

        // 验证 Merchant 的 default_offer_id 是否被设置
        MerchantDO merchant = merchantMapper.selectByNetworkAndExternalId(1L, "158686");
        assertNotNull(merchant, "Merchant should exist after sync");
        assertNotNull(merchant.getDefaultOfferId(), "Merchant should have default_offer_id set");

        // 验证 Offer 是否被创建
        OfferDO offer = offerMapper.selectById(merchant.getDefaultOfferId());
        assertNotNull(offer, "Default Offer should exist");
        assertNotNull(offer.getGotoUrl(), "Offer should have goto_url");

        // TODO: 验证 TrackingLink 是否被创建
        // 由于 tracking 模块的 API 是跨模块调用，需要单独测试
        System.out.println("========================================");
        System.out.println("同步结果验证:");
        System.out.println("========================================");
        System.out.println("Merchant ID: " + merchant.getId());
        System.out.println("Merchant Name: " + merchant.getName());
        System.out.println("Default Offer ID: " + merchant.getDefaultOfferId());
        System.out.println("Offer Name: " + offer.getName());
        System.out.println("Offer goto_url: " + offer.getGotoUrl());
        System.out.println("========================================");
        System.out.println("TrackingLink 创建需要通过 tracking 模块 API 验证");
    }
}
