package com.river.module.tracking.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.river.module.affiliate.dal.dataobject.AffiliateNetworkDO;
import com.river.module.affiliate.dal.dataobject.PostbackSecretDO;
import com.river.module.affiliate.dal.mysql.AffiliateNetworkMapper;
import com.river.module.affiliate.dal.mysql.PostbackSecretMapper;
import com.river.module.tracking.dal.dataobject.ClickDO;
import com.river.module.tracking.dal.dataobject.ConversionDO;
import com.river.module.tracking.dal.dataobject.UnattributedConversionDO;
import com.river.module.tracking.dal.mysql.UnattributedConversionMapper;
import com.river.module.tracking.enums.ConversionStatusEnum;
import com.river.module.tracking.enums.ConversionTypeEnum;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Validated
@Slf4j
public class PostbackServiceImpl implements PostbackService {

    @Resource
    private AffiliateNetworkMapper affiliateNetworkMapper;

    @Resource
    private PostbackSecretMapper postbackSecretMapper;

    @Resource
    private ClickService clickService;

    @Resource
    private ConversionService conversionService;

    @Resource
    private UnattributedConversionMapper unattributedConversionMapper;

    @Resource
    private SignatureValidator signatureValidator;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processPostback(String networkCode, String clickId, String externalConversionId,
                                String type, BigDecimal amount, String currency,
                                String signature, HttpServletRequest request) {

        AffiliateNetworkDO network = getNetworkByCode(networkCode);
        if (network == null) {
            log.warn("[processPostback][联盟网络不存在] networkCode={}", networkCode);
            saveUnattributedConversion(networkCode, externalConversionId, type, amount, currency,
                    request, "NETWORK_NOT_FOUND");
            return;
        }

        if (!validateSignature(network.getId(), signature, request)) {
            log.warn("[processPostback][签名验证失败] networkCode={}", networkCode);
        }

        if (conversionService.isDuplicateConversion(networkCode, externalConversionId)) {
            log.info("[processPostback][重复回调，已忽略] networkCode={}, externalConversionId={}",
                    networkCode, externalConversionId);
            return;
        }

        ClickDO click = null;
        if (StrUtil.isNotBlank(clickId)) {
            click = clickService.getClick(clickId);
        }

        if (click == null) {
            log.warn("[processPostback][点击记录不存在] clickId={}", clickId);
            saveUnattributedConversion(networkCode, externalConversionId, type, amount, currency,
                    request, StrUtil.isBlank(clickId) ? "CLICK_ID_MISSING" : "CLICK_NOT_FOUND");
            return;
        }

        ConversionDO conversion = buildConversion(click, networkCode, externalConversionId, type, amount, currency);
        conversionService.createConversion(conversion);

        log.info("[processPostback][转化记录创建成功] conversionId={}, clickId={}, networkCode={}",
                conversion.getId(), clickId, networkCode);
    }

    private AffiliateNetworkDO getNetworkByCode(String networkCode) {
        return affiliateNetworkMapper.selectOne(
                new LambdaQueryWrapper<AffiliateNetworkDO>()
                        .eq(AffiliateNetworkDO::getCode, networkCode)
        );
    }

    private boolean validateSignature(Long networkId, String signature, HttpServletRequest request) {
        PostbackSecretDO secret = postbackSecretMapper.selectOne(
                new LambdaQueryWrapper<PostbackSecretDO>()
                        .eq(PostbackSecretDO::getNetworkId, networkId)
        );

        if (secret == null || !Boolean.TRUE.equals(secret.getSignatureEnabled())) {
            return true;
        }

        String payload = buildSignaturePayload(request);
        return signatureValidator.validate(secret.getSecretKey(), secret.getAlgorithm(), payload, signature);
    }

    private String buildSignaturePayload(HttpServletRequest request) {
        return request.getQueryString() != null ? request.getQueryString() : "";
    }

    private void saveUnattributedConversion(String networkCode, String externalConversionId,
                                            String type, BigDecimal amount, String currency,
                                            HttpServletRequest request, String failReason) {
        UnattributedConversionDO unattributed = UnattributedConversionDO.builder()
                .networkCode(networkCode)
                .externalConversionId(externalConversionId)
                .conversionType(parseConversionType(type))
                .commission(amount)
                .currency(currency)
                .rawRequest(buildRawRequest(request))
                .attributionFailReason(failReason)
                .conversionTime(LocalDateTime.now())
                .build();
        unattributedConversionMapper.insert(unattributed);
        log.info("[saveUnattributedConversion][未归因转化已保存] id={}, reason={}",
                unattributed.getId(), failReason);
    }

    private ConversionDO buildConversion(ClickDO click, String networkCode, String externalConversionId,
                                         String type, BigDecimal amount, String currency) {
        return ConversionDO.builder()
                .clickId(click.getClickId())
                .networkCode(networkCode)
                .externalConversionId(externalConversionId)
                .conversionType(parseConversionType(type))
                .commission(amount)
                .currency(currency)
                .status(ConversionStatusEnum.PENDING.getCode())
                .conversionTime(LocalDateTime.now())
                .build();
    }

    private Integer parseConversionType(String type) {
        if (StrUtil.isBlank(type)) {
            return ConversionTypeEnum.SALE.getCode();
        }
        return switch (type.toLowerCase()) {
            case "lead" -> ConversionTypeEnum.LEAD.getCode();
            case "install" -> ConversionTypeEnum.INSTALL.getCode();
            case "signup" -> ConversionTypeEnum.SIGNUP.getCode();
            default -> ConversionTypeEnum.SALE.getCode();
        };
    }

    private String buildRawRequest(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append(request.getMethod()).append(" ").append(request.getRequestURI());
        if (request.getQueryString() != null) {
            sb.append("?").append(request.getQueryString());
        }
        sb.append("\nRemote IP: ").append(JakartaServletUtil.getClientIP(request));
        return sb.toString();
    }

}
