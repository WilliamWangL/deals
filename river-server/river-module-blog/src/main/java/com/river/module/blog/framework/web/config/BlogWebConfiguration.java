package com.river.module.blog.framework.web.config;

import com.river.framework.swagger.config.RiverSwaggerAutoConfiguration;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class BlogWebConfiguration {

    @Bean
    public GroupedOpenApi blogGroupedOpenApi() {
        return RiverSwaggerAutoConfiguration.buildGroupedOpenApi("blog");
    }

}
