package com.river.module.tracking.service;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Component
@Slf4j
public class SignatureValidator {

    private static final String HMAC_SHA256 = "HmacSHA256";

    public boolean validate(String secretKey, String algorithm, String payload, String signature) {
        if (StrUtil.isBlank(secretKey) || StrUtil.isBlank(signature)) {
            return false;
        }

        try {
            String expectedSignature = computeHmac(secretKey, algorithm, payload);
            return expectedSignature.equalsIgnoreCase(signature);
        } catch (Exception e) {
            log.error("[validate][签名验证失败] algorithm={}, error={}", algorithm, e.getMessage());
            return false;
        }
    }

    private String computeHmac(String secretKey, String algorithm, String payload)
            throws NoSuchAlgorithmException, InvalidKeyException {
        String macAlgorithm = normalizeAlgorithm(algorithm);
        Mac mac = Mac.getInstance(macAlgorithm);
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), macAlgorithm);
        mac.init(keySpec);
        byte[] hmacBytes = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hmacBytes);
    }

    private String normalizeAlgorithm(String algorithm) {
        if (StrUtil.isBlank(algorithm)) {
            return HMAC_SHA256;
        }
        return switch (algorithm.toUpperCase().replace("-", "")) {
            case "HMACSHA256", "SHA256" -> HMAC_SHA256;
            case "HMACSHA512", "SHA512" -> "HmacSHA512";
            case "HMACSHA1", "SHA1" -> "HmacSHA1";
            default -> HMAC_SHA256;
        };
    }

}
