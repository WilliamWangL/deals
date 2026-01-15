package com.river.module.affiliate.framework.web.config;

import com.river.framework.swagger.config.RiverSwaggerAutoConfiguration;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class AffiliateWebConfiguration {

    @Bean
    public GroupedOpenApi affiliateGroupedOpenApi() {
        return RiverSwaggerAutoConfiguration.buildGroupedOpenApi("affiliate");
    }

}
