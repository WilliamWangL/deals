package com.river.module.affiliate.service.network.admitad;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.river.module.affiliate.dal.dataobject.NetworkCredentialDO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class AdmitadClient {

    private static final String BASE_URL = "https://api.admitad.com";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    private final ConcurrentHashMap<Long, TokenCache> tokenCacheMap = new ConcurrentHashMap<>();

    public List<AdmitadCampaign> getCampaigns(NetworkCredentialDO credential, int offset, int limit) {
        String token = getValidToken(credential);

        String url = String.format("%s/advcampaigns/?offset=%d&limit=%d", BASE_URL, offset, limit);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        try {
            ResponseEntity<CampaignResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(headers), CampaignResponse.class);

            if (response.getBody() != null) {
                log.info("Fetched {} campaigns from Admitad for credential {}",
                    response.getBody().getResults().size(), credential.getId());
                return response.getBody().getResults();
            }
        } catch (Exception e) {
            log.error("Failed to fetch campaigns from Admitad: {}", e.getMessage());
        }

        return List.of();
    }

    /**
     * 获取优惠券列表
     * Admitad API: GET /coupons/website/{websiteId}/
     *
     * @param credential 凭证
     * @param websiteId  网站 ID（从 credentials 中获取）
     * @param offset     偏移量
     * @param limit      每页数量
     * @return 优惠券列表
     */
    public List<AdmitadCoupon> getCoupons(NetworkCredentialDO credential, Long websiteId, int offset, int limit) {
        String token = getValidToken(credential);

        String url = String.format("%s/coupons/website/%d/?offset=%d&limit=%d",
            BASE_URL, websiteId, offset, limit);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        try {
            ResponseEntity<CouponResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(headers), CouponResponse.class);

            if (response.getBody() != null) {
                log.info("Fetched {} coupons from Admitad for website {}",
                    response.getBody().getResults().size(), websiteId);
                return response.getBody().getResults();
            }
        } catch (Exception e) {
            log.error("Failed to fetch coupons from Admitad: {}", e.getMessage());
        }

        return List.of();
    }

    private synchronized String getValidToken(NetworkCredentialDO credential) {
        Long credentialId = credential.getId();
        TokenCache cache = tokenCacheMap.get(credentialId);
        
        if (cache != null && cache.isValid()) {
            return cache.accessToken;
        }
        
        String newToken = refreshToken(credential);
        return newToken;
    }

    private String refreshToken(NetworkCredentialDO credential) {
        try {
            Map<String, String> creds = parseCredentials(credential.getCredentials());
            String clientId = creds.get("clientId");
            String clientSecret = creds.get("clientSecret");
            String scope = creds.getOrDefault("scope", "advcampaigns");

            String url = BASE_URL + "/token/";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "client_credentials");
            body.add("client_id", clientId);
            body.add("client_secret", clientSecret);
            body.add("scope", scope);

            ResponseEntity<TokenResponse> response = restTemplate.exchange(
                url, HttpMethod.POST, new HttpEntity<>(body, headers), TokenResponse.class);

            if (response.getBody() != null) {
                String accessToken = response.getBody().getAccessToken();
                LocalDateTime expiry = LocalDateTime.now().plusSeconds(response.getBody().getExpiresIn() - 300);
                
                tokenCacheMap.put(credential.getId(), new TokenCache(accessToken, expiry));
                
                log.info("Admitad token refreshed for credential {}, expires at {}", 
                    credential.getId(), expiry);
                return accessToken;
            }
            throw new RuntimeException("Empty token response from Admitad");
        } catch (Exception e) {
            log.error("Failed to refresh Admitad token for credential {}: {}", 
                credential.getId(), e.getMessage());
            throw new RuntimeException("Failed to authenticate with Admitad", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> parseCredentials(String credentialsJson) {
        try {
            return objectMapper.readValue(credentialsJson, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Admitad credentials", e);
        }
    }

    @Data
    private static class TokenCache {
        private final String accessToken;
        private final LocalDateTime expiry;
        
        TokenCache(String accessToken, LocalDateTime expiry) {
            this.accessToken = accessToken;
            this.expiry = expiry;
        }
        
        boolean isValid() {
            return expiry != null && LocalDateTime.now().isBefore(expiry);
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

    @Data
    public static class CouponResponse {
        private List<AdmitadCoupon> results;
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
