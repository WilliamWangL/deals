package com.river.module.mediabuy.service;

import com.river.framework.tenant.core.aop.TenantIgnore;
import com.river.module.affiliate.dal.dataobject.OfferDO;
import com.river.module.affiliate.service.OfferService;
import com.river.module.mediabuy.dal.redis.OfferRedisDAO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class OfferCacheServiceImpl implements OfferCacheService {

    @Resource
    private OfferService offerService;

    @Resource
    private OfferRedisDAO offerRedisDAO;

    @Override
    @TenantIgnore // offer is tenant-scoped; we cache by tenantId from DB record
    public OfferDO getOffer(Long offerId) {
        // 先查询 DB 获取 tenantId（用于按租户隔离的缓存 key）
        OfferDO offer = offerService.getOffer(offerId);
        if (offer == null) {
            return null;
        }

        OfferDO cached = offerRedisDAO.get(offer.getTenantId(), offerId);
        if (cached != null) {
            return cached;
        }
        offerRedisDAO.set(offer.getTenantId(), offer);
        return offer;
    }

}

