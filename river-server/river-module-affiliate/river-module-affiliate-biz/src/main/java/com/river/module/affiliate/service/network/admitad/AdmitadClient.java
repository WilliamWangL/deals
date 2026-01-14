package com.river.module.affiliate.service.network.admitad;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Slf4j
@Component
public class AdmitadClient {

    @Resource
    private AdmitadProperties properties;

    private final RestTemplate restTemplate = new RestTemplate();

    private String accessToken;
    private LocalDateTime tokenExpiry;

    public List<AdmitadCampaign> getCampaigns(int offset, int limit) {
        if (!properties.getEnabled()) {
            log.warn("Admitad integration is disabled");
            return List.of();
        }

        ensureValidToken();

        String url = String.format("%s/advcampaigns/?offset=%d&limit=%d", 
            properties.getBaseUrl(), offset, limit);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        try {
            ResponseEntity<CampaignResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(headers), CampaignResponse.class);

            if (response.getBody() != null) {
                log.info("Fetched {} campaigns from Admitad", response.getBody().getResults().size());
                return response.getBody().getResults();
            }
        } catch (Exception e) {
            log.error("Failed to fetch campaigns from Admitad: {}", e.getMessage());
        }

        return List.of();
    }

    private void ensureValidToken() {
        if (accessToken != null && tokenExpiry != null && LocalDateTime.now().isBefore(tokenExpiry)) {
            return;
        }

        refreshToken();
    }

    private void refreshToken() {
        String url = properties.getBaseUrl() + "/token/";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String auth = Base64.getEncoder().encodeToString(
            (properties.getClientId() + ":" + properties.getClientSecret()).getBytes());
        headers.set("Authorization", "Basic " + auth);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("scope", properties.getScope());

        try {
            ResponseEntity<TokenResponse> response = restTemplate.exchange(
                url, HttpMethod.POST, new HttpEntity<>(body, headers), TokenResponse.class);

            if (response.getBody() != null) {
                accessToken = response.getBody().getAccessToken();
                tokenExpiry = LocalDateTime.now().plusSeconds(response.getBody().getExpiresIn() - 300);
                log.info("Admitad token refreshed, expires at {}", tokenExpiry);
            }
        } catch (Exception e) {
            log.error("Failed to refresh Admitad token: {}", e.getMessage());
            throw new RuntimeException("Failed to authenticate with Admitad", e);
        }
    }

    @Data
    public static class TokenResponse {
        @JsonProperty("access_token")
        private String accessToken;
        @JsonProperty("expires_in")
        private Long expiresIn;
        @JsonProperty("token_type")
        private String tokenType;
    }

    @Data
    public static class CampaignResponse {
        private List<AdmitadCampaign> results;
        @JsonProperty("_meta")
        private Meta meta;

        @Data
        public static class Meta {
            private int offset;
            private int limit;
            private int count;
        }
    }

}
