package com.river.framework.idempotent.config;

import com.river.framework.idempotent.core.aop.IdempotentAspect;
import com.river.framework.idempotent.core.keyresolver.impl.DefaultIdempotentKeyResolver;
import com.river.framework.idempotent.core.keyresolver.impl.ExpressionIdempotentKeyResolver;
import com.river.framework.idempotent.core.keyresolver.IdempotentKeyResolver;
import com.river.framework.idempotent.core.keyresolver.impl.UserIdempotentKeyResolver;
import com.river.framework.idempotent.core.redis.IdempotentRedisDAO;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import com.river.framework.redis.config.RiverRedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

@AutoConfiguration(after = RiverRedisAutoConfiguration.class)
public class RiverIdempotentConfiguration {

    @Bean
    public IdempotentAspect idempotentAspect(List<IdempotentKeyResolver> keyResolvers, IdempotentRedisDAO idempotentRedisDAO) {
        return new IdempotentAspect(keyResolvers, idempotentRedisDAO);
    }

    @Bean
    public IdempotentRedisDAO idempotentRedisDAO(StringRedisTemplate stringRedisTemplate) {
        return new IdempotentRedisDAO(stringRedisTemplate);
    }

    // ========== 各种 IdempotentKeyResolver Bean ==========

    @Bean
    public DefaultIdempotentKeyResolver defaultIdempotentKeyResolver() {
        return new DefaultIdempotentKeyResolver();
    }

    @Bean
    public UserIdempotentKeyResolver userIdempotentKeyResolver() {
        return new UserIdempotentKeyResolver();
    }

    @Bean
    public ExpressionIdempotentKeyResolver expressionIdempotentKeyResolver() {
        return new ExpressionIdempotentKeyResolver();
    }

}
