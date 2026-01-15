package com.river.module.stats.framework.web.config;

import com.river.framework.swagger.config.RiverSwaggerAutoConfiguration;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class StatsWebConfiguration {

    @Bean
    public GroupedOpenApi statsGroupedOpenApi() {
        return RiverSwaggerAutoConfiguration.buildGroupedOpenApi("stats");
    }

}
