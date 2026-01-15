package com.river.module.tracking.framework.web.config;

import com.river.framework.swagger.config.RiverSwaggerAutoConfiguration;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class TrackingWebConfiguration {

    @Bean
    public GroupedOpenApi trackingGroupedOpenApi() {
        return RiverSwaggerAutoConfiguration.buildGroupedOpenApi("tracking");
    }

}
