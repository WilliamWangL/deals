package com.river.module.coupon.framework.web.config;

import com.river.framework.swagger.config.RiverSwaggerAutoConfiguration;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class CouponWebConfiguration {

    @Bean
    public GroupedOpenApi couponGroupedOpenApi() {
        return RiverSwaggerAutoConfiguration.buildGroupedOpenApi("coupon");
    }

}
