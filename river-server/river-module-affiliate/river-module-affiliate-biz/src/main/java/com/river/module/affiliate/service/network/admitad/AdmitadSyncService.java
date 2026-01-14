package com.river.module.affiliate.service.network.admitad;

import com.river.module.affiliate.dal.dataobject.MerchantDO;
import com.river.module.affiliate.dal.dataobject.OfferDO;
import com.river.module.affiliate.dal.mysql.MerchantMapper;
import com.river.module.affiliate.dal.mysql.OfferMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdmitadSyncService {

    private static final String NETWORK_CODE = "admitad";
    private static final Long NETWORK_ID = 1L;

    @Resource
    private AdmitadClient admitadClient;

    @Resource
    private AdmitadProperties properties;

    @Resource
    private MerchantMapper merchantMapper;

    @Resource
    private OfferMapper offerMapper;

    @Scheduled(cron = "0 0 4 * * ?")
    public void scheduledSync() {
        if (!properties.getEnabled()) {
            log.debug("Admitad sync is disabled");
            return;
        }
        log.info("Starting scheduled Admitad sync");
        syncCampaigns();
    }

    @Transactional
    public void syncCampaigns() {
        int offset = 0;
        int limit = 100;
        int totalSynced = 0;

        while (true) {
            List<AdmitadCampaign> campaigns = admitadClient.getCampaigns(offset, limit);
            if (campaigns.isEmpty()) {
                break;
            }

            for (AdmitadCampaign campaign : campaigns) {
                try {
                    syncCampaign(campaign);
                    totalSynced++;
                } catch (Exception e) {
                    log.error("Failed to sync campaign {}: {}", campaign.getId(), e.getMessage());
                }
            }

            offset += limit;
            if (campaigns.size() < limit) {
                break;
            }
        }

        log.info("Admitad sync completed, synced {} campaigns", totalSynced);
    }

    private void syncCampaign(AdmitadCampaign campaign) {
        MerchantDO existingMerchant = merchantMapper.selectByNetworkAndExternalId(
            NETWORK_ID, String.valueOf(campaign.getId()));

        MerchantDO merchant;
        if (existingMerchant != null) {
            merchant = existingMerchant;
            updateMerchant(merchant, campaign);
            merchantMapper.updateById(merchant);
        } else {
            merchant = createMerchant(campaign);
            merchantMapper.insert(merchant);
        }

        if (campaign.getActions() != null) {
            for (AdmitadCampaign.Action action : campaign.getActions()) {
                syncOffer(merchant.getId(), campaign, action);
            }
        }
    }

    private MerchantDO createMerchant(AdmitadCampaign campaign) {
        MerchantDO merchant = new MerchantDO();
        merchant.setNetworkId(NETWORK_ID);
        merchant.setExternalId(String.valueOf(campaign.getId()));
        updateMerchant(merchant, campaign);
        return merchant;
    }

    private void updateMerchant(MerchantDO merchant, AdmitadCampaign campaign) {
        merchant.setName(campaign.getName());
        merchant.setSlug(generateSlug(campaign.getName()));
        merchant.setDomain(extractDomain(campaign.getSiteUrl()));
        merchant.setLogoUrl(campaign.getLogoUrl());
        merchant.setDescription(campaign.getDescription());
        merchant.setRating(campaign.getRating());
        merchant.setStatus(mapStatus(campaign.getStatus()));
        
        if (campaign.getRegions() != null) {
            String regions = campaign.getRegions().stream()
                .map(AdmitadCampaign.Region::getRegion)
                .collect(Collectors.joining(","));
            merchant.setRegions(regions);
        }
    }

    private void syncOffer(Long merchantId, AdmitadCampaign campaign, AdmitadCampaign.Action action) {
        String externalId = campaign.getId() + "_" + action.getId();
        OfferDO existingOffer = offerMapper.selectByMerchantAndExternalId(merchantId, externalId);

        if (existingOffer != null) {
            updateOffer(existingOffer, campaign, action);
            offerMapper.updateById(existingOffer);
        } else {
            OfferDO offer = createOffer(merchantId, campaign, action);
            offerMapper.insert(offer);
        }
    }

    private OfferDO createOffer(Long merchantId, AdmitadCampaign campaign, AdmitadCampaign.Action action) {
        OfferDO offer = new OfferDO();
        offer.setMerchantId(merchantId);
        offer.setExternalId(campaign.getId() + "_" + action.getId());
        updateOffer(offer, campaign, action);
        return offer;
    }

    private void updateOffer(OfferDO offer, AdmitadCampaign campaign, AdmitadCampaign.Action action) {
        offer.setName(action.getName() != null ? action.getName() : campaign.getName());
        offer.setDescription(campaign.getDescription());
        offer.setCommissionType(mapCommissionType(action.getType()));
        offer.setCommissionValue(action.getPayment());
        offer.setStatus(mapStatus(campaign.getStatus()));
        
        String trackingUrl = String.format(
            "https://ad.admitad.com/g/%s/?subid={click_id}&subid1={sub1}&subid2={sub2}",
            campaign.getId());
        offer.setTrackingUrlTemplate(trackingUrl);
    }

    private String generateSlug(String name) {
        if (name == null) return null;
        return name.toLowerCase()
            .replaceAll("[^a-z0-9\\s-]", "")
            .replaceAll("\\s+", "-")
            .replaceAll("-+", "-")
            .replaceAll("^-|-$", "");
    }

    private String extractDomain(String url) {
        if (url == null) return null;
        return url.replaceAll("https?://", "").replaceAll("/.*", "");
    }

    private Integer mapStatus(String status) {
        return "active".equalsIgnoreCase(status) ? 1 : 0;
    }

    private Integer mapCommissionType(String type) {
        if (type == null) return 1;
        return switch (type.toLowerCase()) {
            case "sale" -> 1;
            case "lead" -> 2;
            case "click" -> 3;
            default -> 1;
        };
    }

}
