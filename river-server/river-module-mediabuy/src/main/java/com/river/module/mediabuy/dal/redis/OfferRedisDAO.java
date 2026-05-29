package com.river.module.mediabuy.dal.redis;

import com.river.framework.common.util.json.JsonUtils;
import com.river.module.affiliate.dal.dataobject.OfferDO;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

import static com.river.module.mediabuy.dal.redis.RedisKeyConstants.OFFER;

/**
 * {@link OfferDO} 的缓存 DAO（按租户隔离）
 */
@Repository
public class OfferRedisDAO {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * Offer 缓存 TTL（秒）
     */
    @Value("${river.mediabuy.offer-cache-seconds:300}")
    private long offerCacheSeconds;

    public OfferDO get(Long tenantId, Long offerId) {
        String redisKey = formatKey(tenantId, offerId);
        return JsonUtils.parseObject(stringRedisTemplate.opsForValue().get(redisKey), OfferDO.class);
    }

    public void set(Long tenantId, OfferDO offer) {
        String redisKey = formatKey(tenantId, offer.getId());
        // 清理多余字段，避免缓存
        offer.setUpdater(null).setUpdateTime(null).setCreateTime(null).setCreator(null).setDeleted(null);
        stringRedisTemplate.opsForValue().set(redisKey, JsonUtils.toJsonString(offer), offerCacheSeconds, TimeUnit.SECONDS);
    }

    public void delete(Long tenantId, Long offerId) {
        stringRedisTemplate.delete(formatKey(tenantId, offerId));
    }

    private static String formatKey(Long tenantId, Long offerId) {
        return String.format(OFFER, tenantId, offerId);
    }

}

