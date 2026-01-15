package com.river.module.campaign.framework.web.config;

import com.river.framework.swagger.config.RiverSwaggerAutoConfiguration;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class CampaignWebConfiguration {

    @Bean
    public GroupedOpenApi campaignGroupedOpenApi() {
        return RiverSwaggerAutoConfiguration.buildGroupedOpenApi("campaign");
    }

}
