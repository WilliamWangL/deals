package com.river.module.tracking.service;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

public interface PostbackService {

    void processPostback(String networkCode, String clickId, String externalConversionId,
                         String type, BigDecimal amount, String currency,
                         String signature, HttpServletRequest request);

}
