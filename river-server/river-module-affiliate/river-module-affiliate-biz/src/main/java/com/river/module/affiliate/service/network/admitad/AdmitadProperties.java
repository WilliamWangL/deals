package com.river.module.affiliate.service.network.admitad;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "river.affiliate.admitad")
public class AdmitadProperties {

    private Boolean enabled = false;

    private String clientId;

    private String clientSecret;

    private String baseUrl = "https://api.admitad.com";

    private String scope = "advcampaigns";

}
