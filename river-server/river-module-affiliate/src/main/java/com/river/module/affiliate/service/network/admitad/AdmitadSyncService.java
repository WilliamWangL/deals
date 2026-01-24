package com.river.module.affiliate.service.network.admitad;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.river.module.affiliate.dal.dataobject.CategoryDO;
import com.river.module.affiliate.dal.dataobject.CategoryMappingDO;
import com.river.module.affiliate.dal.dataobject.MerchantDO;
import com.river.module.affiliate.dal.dataobject.NetworkCredentialDO;
import com.river.module.affiliate.dal.dataobject.OfferDO;
import com.river.module.affiliate.dal.mysql.CategoryMapper;
import com.river.module.affiliate.dal.mysql.CategoryMappingMapper;
import com.river.module.affiliate.dal.mysql.MerchantMapper;
import com.river.module.affiliate.dal.mysql.NetworkCredentialMapper;
import com.river.module.affiliate.dal.mysql.OfferMapper;
import com.river.module.affiliate.enums.PayoutModelEnum;
import com.river.module.coupon.dal.dataobject.CouponDO;
import com.river.module.coupon.dal.dataobject.DealDO;
import com.river.module.coupon.dal.mysql.CouponMapper;
import com.river.module.coupon.dal.mysql.DealMapper;
import com.river.framework.common.enums.CommonStatusEnum;
import com.river.module.coupon.enums.CouponTypeEnum;
import com.river.module.coupon.enums.DiscountTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.river.framework.common.biz.tracking.TrackingLinkCommonApi;
import com.river.framework.common.biz.tracking.dto.TrackingLinkCreateReqDTO;
import com.river.module.affiliate.controller.admin.network.AffiliateNetworkController;

@Slf4j
@Service
public class AdmitadSyncService {

    /** 目标类型常量 */
    private static final int TARGET_TYPE_MERCHANT = 1;
    private static final int TARGET_TYPE_OFFER = 2;
    private static final int TARGET_TYPE_DEAL = 3;
    private static final int TARGET_TYPE_COUPON = 4;

    @Resource
    private AdmitadClient admitadClient;

    @Resource
    private MerchantMapper merchantMapper;

    @Resource
    private OfferMapper offerMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private CategoryMappingMapper categoryMappingMapper;

    @Resource
    private CouponMapper couponMapper;

    @Resource
    private DealMapper dealMapper;

    @Resource
    private TrackingLinkCommonApi trackingLinkCommonApi;

    @Resource
    private NetworkCredentialMapper credentialMapper;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Sync statistics tracking
    private volatile int lastSyncMerchants = 0;
    private volatile int lastSyncOffers = 0;
    private volatile int lastSyncCoupons = 0;
    private volatile int lastSyncDeals = 0;
    private volatile int lastSyncFailed = 0;
    private volatile LocalDateTime lastSyncTime = null;

    /**
     * Get last sync statistics
     */
    public Map<String, Object> getLastSyncStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("merchants", lastSyncMerchants);
        stats.put("offers", lastSyncOffers);
        stats.put("coupons", lastSyncCoupons);
        stats.put("deals", lastSyncDeals);
        stats.put("failed", lastSyncFailed);
        stats.put("lastSyncTime", lastSyncTime);
        return stats;
    }

    public void syncCampaigns(NetworkCredentialDO credential) {
        // Reset statistics at start
        this.lastSyncMerchants = 0;
        this.lastSyncOffers = 0;
        this.lastSyncCoupons = 0;
        this.lastSyncDeals = 0;
        this.lastSyncFailed = 0;
        this.lastSyncTime = LocalDateTime.now();

        int offset = 0;
        int limit = 100;
        int totalSynced = 0;

        while (true) {
            List<AdmitadCampaign> campaigns = admitadClient.getCampaigns(credential, offset, limit);
            if (campaigns.isEmpty()) {
                break;
            }

            for (AdmitadCampaign campaign : campaigns) {
                try {
                    syncSingleCampaign(credential.getNetworkId(), campaign);
                    totalSynced++;
                    this.lastSyncMerchants++;
                } catch (Exception e) {
                    this.lastSyncFailed++;
                    log.error("Failed to sync campaign {}: {}", campaign.getId(), e.getMessage());
                }
            }

            offset += limit;
            if (campaigns.size() < limit) {
                break;
            }
        }

        log.info("Admitad sync completed for network {}, synced {} campaigns", 
            credential.getNetworkId(), totalSynced);
    }

    @Transactional
    public void syncSingleCampaign(Long networkId, AdmitadCampaign campaign) {
        MerchantDO existingMerchant = merchantMapper.selectByNetworkAndExternalId(
            networkId, String.valueOf(campaign.getId()));

        MerchantDO merchant;
        if (existingMerchant != null) {
            merchant = existingMerchant;
            updateMerchant(merchant, campaign);
            merchantMapper.updateById(merchant);
        } else {
            merchant = createMerchant(networkId, campaign);
            merchantMapper.insert(merchant);
        }

        // 同步 Offers 并记录第一个 Offer ID
        Long firstOfferId = null;
        if (campaign.getActions() != null) {
            for (AdmitadCampaign.Action action : campaign.getActions()) {
                OfferDO offer = syncOffer(networkId, merchant.getId(), campaign, action);
                this.lastSyncOffers++;
                if (firstOfferId == null) {
                    firstOfferId = offer.getId();
                }
            }
        }

        // 设置默认 Offer ID（如果还没有设置）
        if (merchant.getDefaultOfferId() == null && firstOfferId != null) {
            merchant.setDefaultOfferId(firstOfferId);
            merchantMapper.updateById(merchant);
            log.info("Set default_offer_id={} for merchant={}", firstOfferId, merchant.getId());
        }

        // 创建或更新 Merchant 的 TrackingLink
        createOrUpdateMerchantTrackingLink(merchant);
    }

    private MerchantDO createMerchant(Long networkId, AdmitadCampaign campaign) {
        MerchantDO merchant = new MerchantDO();
        merchant.setNetworkId(networkId);
        merchant.setExternalId(String.valueOf(campaign.getId()));
        updateMerchant(merchant, campaign);
        return merchant;
    }

    private void updateMerchant(MerchantDO merchant, AdmitadCampaign campaign) {
        merchant.setName(campaign.getName());
        merchant.setSlug(generateSlug(campaign.getName(), campaign.getId()));
        merchant.setDomain(extractDomain(campaign.getSiteUrl()));
        merchant.setLogoUrl(campaign.getLogoUrl());
        merchant.setDescription(campaign.getDescription());
        merchant.setRating(campaign.getRating());
        merchant.setStatus(mapStatus(campaign.getStatus()));

        if (campaign.getRegions() != null) {
            List<String> regions = campaign.getRegions().stream()
                .map(AdmitadCampaign.Region::getRegion)
                .collect(Collectors.toList());
            merchant.setRegions(regions);
        }

        // 映射分类
        if (campaign.getCategories() != null && !campaign.getCategories().isEmpty()) {
            String categoryIds = mapCategories(merchant.getNetworkId(), campaign.getCategories());
            merchant.setCategoryIds(categoryIds);
        }
    }

    private OfferDO syncOffer(Long networkId, Long merchantId, AdmitadCampaign campaign, AdmitadCampaign.Action action) {
        String externalId = campaign.getId() + "_" + action.getId();
        OfferDO existingOffer = offerMapper.selectByMerchantAndExternalId(merchantId, externalId);

        OfferDO offer;
        if (existingOffer != null) {
            offer = existingOffer;
            updateOffer(offer, campaign, action);
            offerMapper.updateById(offer);
        } else {
            offer = createOffer(networkId, merchantId, campaign, action);
            offerMapper.insert(offer);
        }

        // 创建或更新 Offer 的 TrackingLink
        createOrUpdateOfferTrackingLink(offer);

        return offer;
    }

    private OfferDO createOffer(Long networkId, Long merchantId, AdmitadCampaign campaign, AdmitadCampaign.Action action) {
        OfferDO offer = new OfferDO();
        offer.setNetworkId(networkId);
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
        offer.setGotoUrl(trackingUrl);

        // 设置 Offer 的分类（继承自 Campaign）
        if (campaign.getCategories() != null && !campaign.getCategories().isEmpty()) {
            String categoryIds = mapCategories(offer.getNetworkId(), campaign.getCategories());
            offer.setCategoryIds(categoryIds);
        }

        // 设置 Offer 的地区
        if (campaign.getRegions() != null && !campaign.getRegions().isEmpty()) {
            List<String> regions = campaign.getRegions().stream()
                .map(AdmitadCampaign.Region::getRegion)
                .collect(Collectors.toList());
            offer.setRegions(regions);
        }
    }

    private String generateSlug(String name, Long campaignId) {
        if (name == null) return String.valueOf(campaignId);
        String baseSlug = name.toLowerCase()
            .replaceAll("[^a-z0-9\\s-]", "")
            .replaceAll("\\s+", "-")
            .replaceAll("-+", "-")
            .replaceAll("^-|-$", "");
        // 添加 campaign ID 后缀确保唯一性
        return baseSlug.isEmpty() ? String.valueOf(campaignId) : baseSlug + "-" + campaignId;
    }

    private String extractDomain(String url) {
        if (url == null) return null;
        return url.replaceAll("https?://", "").replaceAll("/.*", "");
    }

    private Integer mapStatus(String status) {
        return "active".equalsIgnoreCase(status)
            ? CommonStatusEnum.ENABLE.getStatus()
            : CommonStatusEnum.DISABLE.getStatus();
    }

    private Integer mapCommissionType(String type) {
        return PayoutModelEnum.fromAdmitadType(type).getCode();
    }

    /**
     * 映射联盟分类到本地分类
     * 如果映射不存在，自动创建本地分类和映射记录
     *
     * @param networkId  联盟网络 ID
     * @param categories 联盟分类列表
     * @return 逗号分隔的本地分类 ID
     */
    private String mapCategories(Long networkId, List<AdmitadCampaign.Category> categories) {
        List<Long> mappedCategoryIds = new ArrayList<>();

        for (AdmitadCampaign.Category category : categories) {
            String externalId = String.valueOf(category.getId());

            // 查找现有映射
            CategoryMappingDO mapping = categoryMappingMapper.selectByNetworkAndExternalId(networkId, externalId);

            if (mapping == null) {
                // 自动创建本地分类
                CategoryDO localCategory = createLocalCategory(category.getName());
                categoryMapper.insert(localCategory);

                // 创建映射记录并绑定本地分类
                mapping = new CategoryMappingDO();
                mapping.setNetworkId(networkId);
                mapping.setExternalId(externalId);
                mapping.setExternalName(category.getName());
                mapping.setCategoryId(localCategory.getId());
                mapping.setAutoCreated(true);
                categoryMappingMapper.insert(mapping);
                log.info("Auto-created category: {} -> local id={}", category.getName(), localCategory.getId());
            } else if (mapping.getCategoryId() == null) {
                // 已有映射但未绑定本地分类，自动创建并绑定
                CategoryDO localCategory = createLocalCategory(category.getName());
                categoryMapper.insert(localCategory);
                mapping.setCategoryId(localCategory.getId());
                categoryMappingMapper.updateById(mapping);
                log.info("Auto-bound category: {} -> local id={}", category.getName(), localCategory.getId());
            }

            // 加入结果
            if (mapping.getCategoryId() != null) {
                mappedCategoryIds.add(mapping.getCategoryId());
            }
        }

        return mappedCategoryIds.isEmpty() ? null :
            mappedCategoryIds.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    /**
     * 创建本地分类
     */
    private CategoryDO createLocalCategory(String name) {
        CategoryDO category = new CategoryDO();
        category.setParentId(0L);
        category.setName(name);
        category.setSlug(generateCategorySlug(name));
        category.setLevel(1);
        category.setSort(0);
        category.setStatus(CommonStatusEnum.ENABLE.getStatus());
        return category;
    }

    /**
     * 生成分类 slug
     */
    private String generateCategorySlug(String name) {
        if (name == null) return "category-" + System.currentTimeMillis();
        // 尝试音译，简单处理：移除非ASCII字符，转小写，空格转连字符
        String slug = name.toLowerCase()
            .replaceAll("[^a-z0-9\\s-]", "")
            .replaceAll("\\s+", "-")
            .replaceAll("-+", "-")
            .replaceAll("^-|-$", "");
        // 如果slug为空（如全是中文/俄文），使用时间戳
        return slug.isEmpty() ? "category-" + System.currentTimeMillis() : slug;
    }

    /**
     * 同步优惠券数据
     * 根据 species 字段分流到 Coupon 或 Deal 表
     */
    public void syncCoupons(NetworkCredentialDO credential) {
        // Reset statistics at start (offers may be synced from campaigns)
        this.lastSyncCoupons = 0;
        this.lastSyncDeals = 0;
        this.lastSyncFailed = 0;
        this.lastSyncTime = LocalDateTime.now();

        Long websiteId = extractWebsiteId(credential);
        if (websiteId == null) {
            log.error("No websiteId found in credential {}", credential.getId());
            this.lastSyncFailed++;
            return;
        }

        int offset = 0;
        int limit = 100;
        int couponCount = 0;
        int dealCount = 0;

        while (true) {
            List<AdmitadCoupon> coupons = admitadClient.getCoupons(credential, websiteId, offset, limit);
            if (coupons.isEmpty()) {
                break;
            }

            for (AdmitadCoupon coupon : coupons) {
                try {
                    if ("promocode".equalsIgnoreCase(coupon.getSpecies())) {
                        syncSingleCoupon(credential.getNetworkId(), coupon);
                        couponCount++;
                        this.lastSyncCoupons++;
                    } else {
                        syncSingleDeal(credential.getNetworkId(), coupon);
                        dealCount++;
                        this.lastSyncDeals++;
                    }
                } catch (Exception e) {
                    this.lastSyncFailed++;
                    log.error("Failed to sync coupon {}: {}", coupon.getId(), e.getMessage());
                }
            }

            offset += limit;
            if (coupons.size() < limit) {
                break;
            }
        }

        log.info("Admitad coupon sync completed for network {}: {} coupons, {} deals",
            credential.getNetworkId(), couponCount, dealCount);
    }

    @Transactional
    public void syncSingleCoupon(Long networkId, AdmitadCoupon admitadCoupon) {
        String externalId = String.valueOf(admitadCoupon.getId());
        CouponDO existingCoupon = couponMapper.selectByNetworkAndExternalId(networkId, externalId);

        // 查找关联的 Merchant
        Long merchantId = null;
        if (admitadCoupon.getCampaign() != null) {
            MerchantDO merchant = merchantMapper.selectByNetworkAndExternalId(
                networkId, String.valueOf(admitadCoupon.getCampaign().getId()));
            if (merchant != null) {
                merchantId = merchant.getId();
            }
        }

        if (existingCoupon != null) {
            updateCoupon(existingCoupon, networkId, merchantId, admitadCoupon);
            couponMapper.updateById(existingCoupon);
            createOrUpdateCouponTrackingLink(existingCoupon);
        } else {
            CouponDO coupon = createCoupon(networkId, merchantId, admitadCoupon);
            couponMapper.insert(coupon);
            createOrUpdateCouponTrackingLink(coupon);
        }
    }

    private CouponDO createCoupon(Long networkId, Long merchantId, AdmitadCoupon admitadCoupon) {
        CouponDO coupon = new CouponDO();
        coupon.setNetworkId(networkId);
        coupon.setExternalId(String.valueOf(admitadCoupon.getId()));
        updateCoupon(coupon, networkId, merchantId, admitadCoupon);
        return coupon;
    }

    private void updateCoupon(CouponDO coupon, Long networkId, Long merchantId, AdmitadCoupon admitadCoupon) {
        coupon.setMerchantId(merchantId);
        coupon.setTitle(admitadCoupon.getName() != null ? admitadCoupon.getName() : admitadCoupon.getShortName());
        coupon.setCode(admitadCoupon.getPromocode());
        coupon.setTerms(admitadCoupon.getDescription());
        coupon.setImageUrl(admitadCoupon.getImage());
        coupon.setGotoUrl(admitadCoupon.getGotoLink());
        coupon.setExclusive(admitadCoupon.getExclusive());
        coupon.setVerified(admitadCoupon.getVerification());
        coupon.setCouponType(mapCouponType(admitadCoupon.getSpecies()));
        coupon.setStatus(CommonStatusEnum.ENABLE.getStatus());

        // 解析折扣
        if (admitadCoupon.getDiscount() != null) {
            parseDiscount(coupon, admitadCoupon.getDiscount());
        }

        // 解析日期
        if (admitadCoupon.getDateStart() != null) {
            coupon.setStartTime(parseDateTime(admitadCoupon.getDateStart()));
        }
        if (admitadCoupon.getDateEnd() != null) {
            coupon.setEndTime(parseDateTime(admitadCoupon.getDateEnd()));
        }

        // 设置地区
        if (admitadCoupon.getRegions() != null && !admitadCoupon.getRegions().isEmpty()) {
            coupon.setRegions(new ArrayList<>(admitadCoupon.getRegions()));
        }

        // 映射分类
        if (admitadCoupon.getCategories() != null && !admitadCoupon.getCategories().isEmpty()) {
            String categoryIds = mapCouponCategories(networkId, admitadCoupon.getCategories());
            coupon.setCategoryIds(categoryIds);
        }
    }

    @Transactional
    public void syncSingleDeal(Long networkId, AdmitadCoupon admitadCoupon) {
        String externalId = String.valueOf(admitadCoupon.getId());
        DealDO existingDeal = dealMapper.selectByNetworkAndExternalId(networkId, externalId);

        // 查找关联的 Merchant
        Long merchantId = null;
        if (admitadCoupon.getCampaign() != null) {
            MerchantDO merchant = merchantMapper.selectByNetworkAndExternalId(
                networkId, String.valueOf(admitadCoupon.getCampaign().getId()));
            if (merchant != null) {
                merchantId = merchant.getId();
            }
        }

        if (existingDeal != null) {
            updateDeal(existingDeal, networkId, merchantId, admitadCoupon);
            dealMapper.updateById(existingDeal);
            createOrUpdateDealTrackingLink(existingDeal);
        } else {
            DealDO deal = createDeal(networkId, merchantId, admitadCoupon);
            dealMapper.insert(deal);
            createOrUpdateDealTrackingLink(deal);
        }
    }

    private DealDO createDeal(Long networkId, Long merchantId, AdmitadCoupon admitadCoupon) {
        DealDO deal = new DealDO();
        deal.setNetworkId(networkId);
        deal.setExternalId(String.valueOf(admitadCoupon.getId()));
        updateDeal(deal, networkId, merchantId, admitadCoupon);
        return deal;
    }

    private void updateDeal(DealDO deal, Long networkId, Long merchantId, AdmitadCoupon admitadCoupon) {
        deal.setMerchantId(merchantId);
        deal.setTitle(admitadCoupon.getName() != null ? admitadCoupon.getName() : admitadCoupon.getShortName());
        deal.setDescription(admitadCoupon.getDescription());
        deal.setImageUrl(admitadCoupon.getImage());
        deal.setGotoUrl(admitadCoupon.getGotoLink());
        deal.setExclusive(admitadCoupon.getExclusive());
        deal.setStatus(CommonStatusEnum.ENABLE.getStatus());

        // 解析折扣百分比
        if (admitadCoupon.getDiscount() != null) {
            deal.setDiscountPercent(parseDiscountPercent(admitadCoupon.getDiscount()));
        }

        // 解析日期
        if (admitadCoupon.getDateStart() != null) {
            deal.setStartTime(parseDateTime(admitadCoupon.getDateStart()));
        }
        if (admitadCoupon.getDateEnd() != null) {
            deal.setEndTime(parseDateTime(admitadCoupon.getDateEnd()));
        }

        // 设置地区
        if (admitadCoupon.getRegions() != null && !admitadCoupon.getRegions().isEmpty()) {
            deal.setRegions(new ArrayList<>(admitadCoupon.getRegions()));
        }

        // 映射分类
        if (admitadCoupon.getCategories() != null && !admitadCoupon.getCategories().isEmpty()) {
            String categoryIds = mapCouponCategories(networkId, admitadCoupon.getCategories());
            deal.setCategoryIds(categoryIds);
        }
    }

    private Long extractWebsiteId(NetworkCredentialDO credential) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> creds = objectMapper.readValue(credential.getCredentials(), Map.class);
            Object websiteId = creds.get("websiteId");
            if (websiteId instanceof Number) {
                return ((Number) websiteId).longValue();
            } else if (websiteId instanceof String) {
                return Long.parseLong((String) websiteId);
            }
        } catch (Exception e) {
            log.error("Failed to extract websiteId from credential {}: {}", credential.getId(), e.getMessage());
        }
        return null;
    }

    private Integer mapCouponType(String species) {
        return CouponTypeEnum.fromAdmitadSpecies(species).getCode();
    }

    private void parseDiscount(CouponDO coupon, String discount) {
        if (discount == null || discount.isEmpty()) return;

        // 尝试解析 "20%" 格式
        if (discount.endsWith("%")) {
            try {
                String value = discount.replace("%", "").trim();
                coupon.setDiscountValue(new BigDecimal(value));
                coupon.setDiscountType(DiscountTypeEnum.PERCENT.getCode());
                return;
            } catch (NumberFormatException ignored) {}
        }

        // 尝试解析 "$10" 或 "10 USD" 格式
        String numericPart = discount.replaceAll("[^0-9.]", "");
        if (!numericPart.isEmpty()) {
            try {
                coupon.setDiscountValue(new BigDecimal(numericPart));
                coupon.setDiscountType(DiscountTypeEnum.FIXED.getCode());
            } catch (NumberFormatException ignored) {}
        }
    }

    private Integer parseDiscountPercent(String discount) {
        if (discount == null || discount.isEmpty()) return null;

        if (discount.endsWith("%")) {
            try {
                String value = discount.replace("%", "").trim();
                return Integer.parseInt(value);
            } catch (NumberFormatException ignored) {}
        }
        return null;
    }

    private LocalDateTime parseDateTime(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        try {
            // 优先使用 ISO 格式（API 返回 "2026-01-14T00:00:00"）
            return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e1) {
            try {
                // 备用格式（旧格式 "2026-01-14 00:00:00"）
                return LocalDateTime.parse(dateStr, DATE_FORMATTER);
            } catch (Exception e2) {
                log.debug("Failed to parse date: {}", dateStr);
                return null;
            }
        }
    }

    /**
     * 映射 Coupon 分类（AdmitadCoupon.Category 类型）
     * 如果映射不存在，自动创建本地分类和映射记录
     */
    private String mapCouponCategories(Long networkId, List<AdmitadCoupon.Category> categories) {
        List<Long> mappedCategoryIds = new ArrayList<>();

        for (AdmitadCoupon.Category category : categories) {
            String externalId = String.valueOf(category.getId());

            CategoryMappingDO mapping = categoryMappingMapper.selectByNetworkAndExternalId(networkId, externalId);

            if (mapping == null) {
                // 自动创建本地分类
                CategoryDO localCategory = createLocalCategory(category.getName());
                categoryMapper.insert(localCategory);

                // 创建映射记录并绑定本地分类
                mapping = new CategoryMappingDO();
                mapping.setNetworkId(networkId);
                mapping.setExternalId(externalId);
                mapping.setExternalName(category.getName());
                mapping.setCategoryId(localCategory.getId());
                mapping.setAutoCreated(true);
                categoryMappingMapper.insert(mapping);
                log.info("Auto-created category: {} -> local id={}", category.getName(), localCategory.getId());
            } else if (mapping.getCategoryId() == null) {
                // 已有映射但未绑定本地分类，自动创建并绑定
                CategoryDO localCategory = createLocalCategory(category.getName());
                categoryMapper.insert(localCategory);
                mapping.setCategoryId(localCategory.getId());
                categoryMappingMapper.updateById(mapping);
                log.info("Auto-bound category: {} -> local id={}", category.getName(), localCategory.getId());
            }

            if (mapping.getCategoryId() != null) {
                mappedCategoryIds.add(mapping.getCategoryId());
            }
        }

        return mappedCategoryIds.isEmpty() ? null :
            mappedCategoryIds.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    /**
     * 创建或更新 Merchant 的 TrackingLink
     */
    private void createOrUpdateMerchantTrackingLink(MerchantDO merchant) {
        try {
            // 构建追踪 URL
            String trackingUrl;
            if (merchant.getDefaultOfferId() != null) {
                // 如果有默认 Offer，使用 Offer 的 goto_url
                OfferDO defaultOffer = offerMapper.selectById(merchant.getDefaultOfferId());
                if (defaultOffer != null && defaultOffer.getGotoUrl() != null) {
                    trackingUrl = defaultOffer.getGotoUrl();
                } else {
                    trackingUrl = generateMerchantTrackingUrl(merchant);
                }
            } else {
                trackingUrl = generateMerchantTrackingUrl(merchant);
            }

            // trackingUrl 已通过上述逻辑生成，确保不为 null
            // 如果仍为 null，记录警告并继续尝试创建（使用空 URL）
            if (trackingUrl == null) {
                log.warn("No tracking URL available for merchant {}, attempting to create with generated URL", merchant.getId());
            }

            // 使用 Merchant 的 slug 作为 TrackingLink 的 slug
            String slug = merchant.getSlug() != null ? merchant.getSlug() : "m-" + merchant.getId();

            TrackingLinkCreateReqDTO reqDTO = new TrackingLinkCreateReqDTO();
            reqDTO.setTargetType(TARGET_TYPE_MERCHANT);
            reqDTO.setTargetId(merchant.getId());
            reqDTO.setSlug(slug);
            reqDTO.setTrackingUrl(trackingUrl);

            trackingLinkCommonApi.createOrUpdateTrackingLink(reqDTO);
            log.debug("Created/updated tracking link for merchant={}", merchant.getId());
        } catch (Exception e) {
            log.error("Failed to create/update tracking link for merchant {}: {}", merchant.getId(), e.getMessage());
        }
    }

    /**
     * 创建或更新 Offer 的 TrackingLink
     */
    private void createOrUpdateOfferTrackingLink(OfferDO offer) {
        try {
            // 获取或生成 tracking URL
            String trackingUrl = offer.getGotoUrl();
            if (trackingUrl == null) {
                // 生成默认追踪 URL，使用 merchant 的 external_id
                MerchantDO merchant = merchantMapper.selectById(offer.getMerchantId());
                if (merchant != null && merchant.getExternalId() != null) {
                    trackingUrl = String.format(
                        "https://ad.admitad.com/g/%s/?subid={click_id}&subid1={sub1}&subid2={sub2}",
                        merchant.getExternalId());
                }
            }

            if (trackingUrl == null) {
                log.warn("Cannot create tracking link for offer {}: no tracking URL available", offer.getId());
                return;
            }

            String slug = "offer-" + offer.getId();

            TrackingLinkCreateReqDTO reqDTO = new TrackingLinkCreateReqDTO();
            reqDTO.setTargetType(TARGET_TYPE_OFFER);
            reqDTO.setTargetId(offer.getId());
            reqDTO.setSlug(slug);
            reqDTO.setTrackingUrl(trackingUrl);

            trackingLinkCommonApi.createOrUpdateTrackingLink(reqDTO);
            log.debug("Created/updated tracking link for offer={}", offer.getId());
        } catch (Exception e) {
            log.error("Failed to create/update tracking link for offer {}: {}", offer.getId(), e.getMessage());
        }
    }

    /**
     * 创建或更新 Coupon 的 TrackingLink
     */
    private void createOrUpdateCouponTrackingLink(CouponDO coupon) {
        try {
            // 获取或生成 tracking URL
            String trackingUrl = coupon.getGotoUrl();
            if (trackingUrl == null && coupon.getMerchantId() != null) {
                // 生成默认追踪 URL，使用 merchant 的 external_id
                MerchantDO merchant = merchantMapper.selectById(coupon.getMerchantId());
                if (merchant != null && merchant.getExternalId() != null) {
                    trackingUrl = String.format(
                        "https://ad.admitad.com/g/%s/?subid={click_id}&subid1={sub1}&subid2={sub2}",
                        merchant.getExternalId());
                }
            }

            if (trackingUrl == null) {
                log.warn("Cannot create tracking link for coupon {}: no tracking URL available", coupon.getId());
                return;
            }

            String slug = "coupon-" + coupon.getId();

            TrackingLinkCreateReqDTO reqDTO = new TrackingLinkCreateReqDTO();
            reqDTO.setTargetType(TARGET_TYPE_COUPON);
            reqDTO.setTargetId(coupon.getId());
            reqDTO.setSlug(slug);
            reqDTO.setTrackingUrl(trackingUrl);

            trackingLinkCommonApi.createOrUpdateTrackingLink(reqDTO);
            log.debug("Created/updated tracking link for coupon={}", coupon.getId());
        } catch (Exception e) {
            log.error("Failed to create/update tracking link for coupon {}: {}", coupon.getId(), e.getMessage());
        }
    }

    /**
     * 创建或更新 Deal 的 TrackingLink
     */
    private void createOrUpdateDealTrackingLink(DealDO deal) {
        try {
            // 获取或生成 tracking URL
            String trackingUrl = deal.getGotoUrl();
            if (trackingUrl == null && deal.getMerchantId() != null) {
                // 生成默认追踪 URL，使用 merchant 的 external_id
                MerchantDO merchant = merchantMapper.selectById(deal.getMerchantId());
                if (merchant != null && merchant.getExternalId() != null) {
                    trackingUrl = String.format(
                        "https://ad.admitad.com/g/%s/?subid={click_id}&subid1={sub1}&subid2={sub2}",
                        merchant.getExternalId());
                }
            }

            if (trackingUrl == null) {
                log.warn("Cannot create tracking link for deal {}: no tracking URL available", deal.getId());
                return;
            }

            // 优先使用 deal 的 slug
            String slug = deal.getSlug() != null ? deal.getSlug() : "deal-" + deal.getId();

            TrackingLinkCreateReqDTO reqDTO = new TrackingLinkCreateReqDTO();
            reqDTO.setTargetType(TARGET_TYPE_DEAL);
            reqDTO.setTargetId(deal.getId());
            reqDTO.setSlug(slug);
            reqDTO.setTrackingUrl(trackingUrl);

            trackingLinkCommonApi.createOrUpdateTrackingLink(reqDTO);
            log.debug("Created/updated tracking link for deal={}", deal.getId());
        } catch (Exception e) {
            log.error("Failed to create/update tracking link for deal {}: {}", deal.getId(), e.getMessage());
        }
    }

    /**
     * 生成 Merchant 的追踪 URL
     */
    private String generateMerchantTrackingUrl(MerchantDO merchant) {
        // 根据 merchant 的 external_id 生成 Admitad 追踪链接
        if (merchant.getExternalId() != null) {
            return String.format("https://ad.admitad.com/g/%s/?subid={click_id}&subid1={sub1}&subid2={sub2}",
                merchant.getExternalId());
        }
        return null;
    }

    /**
     * 同步数据（Merchant + Offer，通过 code 调用）
     * @param networkCode 联盟网络编码
     * @return 同步结果
     */
    public AffiliateNetworkController.SyncResult syncData(String networkCode) {
        NetworkCredentialDO credential = getEnabledCredentialByNetworkCode(networkCode);
        if (credential == null) {
            return AffiliateNetworkController.SyncResult.error("No enabled credentials found for network: " + networkCode);
        }
        // 执行同步（Merchant + Offer）
        syncCampaigns(credential);
        Map<String, Object> stats = new HashMap<>();
        stats.put("merchants", lastSyncMerchants);
        stats.put("offers", lastSyncOffers);
        stats.put("failed", lastSyncFailed);
        return AffiliateNetworkController.SyncResult.success("Data sync completed", stats);
    }

    /**
     * 同步 Deal 数据（通过 code 调用）
     * @param networkCode 联盟网络编码
     * @return 同步结果
     */
    public AffiliateNetworkController.SyncResult syncDeals(String networkCode) {
        NetworkCredentialDO credential = getEnabledCredentialByNetworkCode(networkCode);
        if (credential == null) {
            return AffiliateNetworkController.SyncResult.error("No enabled credentials found for network: " + networkCode);
        }
        syncCoupons(credential);
        Map<String, Object> stats = new HashMap<>();
        stats.put("deals", lastSyncDeals);
        stats.put("coupons", lastSyncCoupons);
        stats.put("failed", lastSyncFailed);
        return AffiliateNetworkController.SyncResult.success("Deal sync completed", stats);
    }

    /**
     * 同步优惠券和Deal数据（通过 code 调用）
     * @param networkCode 联盟网络编码
     * @return 同步结果
     */
    public AffiliateNetworkController.SyncResult syncCouponsOnly(String networkCode) {
        NetworkCredentialDO credential = getEnabledCredentialByNetworkCode(networkCode);
        if (credential == null) {
            return AffiliateNetworkController.SyncResult.error("No enabled credentials found for network: " + networkCode);
        }
        syncCoupons(credential);
        Map<String, Object> stats = new HashMap<>();
        stats.put("coupons", lastSyncCoupons);
        stats.put("deals", lastSyncDeals);
        stats.put("failed", lastSyncFailed);
        return AffiliateNetworkController.SyncResult.success("Coupon sync completed", stats);
    }

    /**
     * 根据 network code 获取启用的凭证
     * @param networkCode 联盟网络编码
     * @return 凭证对象，不存在返回 null
     */
    private NetworkCredentialDO getEnabledCredentialByNetworkCode(String networkCode) {
        List<NetworkCredentialDO> credentials = credentialMapper.selectEnabledByNetworkCode(networkCode);
        return credentials.isEmpty() ? null : credentials.get(0);
    }

}
