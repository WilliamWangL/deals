package com.river.module.tracking.service;

import cn.hutool.core.util.StrUtil;
import com.river.framework.common.pojo.PageResult;
import com.river.module.affiliate.dal.dataobject.OfferDO;
import com.river.module.affiliate.service.OfferService;
import com.river.module.tracking.controller.admin.click.vo.ClickPageReqVO;
import com.river.module.tracking.dal.dataobject.ClickDO;
import com.river.module.tracking.dal.dataobject.TrackingLinkDO;
import com.river.module.tracking.dal.mysql.ClickMapper;
import com.river.module.tracking.dal.mysql.TrackingLinkMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.river.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.river.module.tracking.enums.ErrorCodeConstants.CLICK_NOT_EXISTS;
import static com.river.module.tracking.enums.ErrorCodeConstants.TRACKING_LINK_NOT_EXISTS;

@Service
@Validated
@Slf4j
public class ClickServiceImpl implements ClickService {

    private static final String FALLBACK_URL = "https://www.google.com";

    @Resource
    private ClickMapper clickMapper;

    @Resource
    private TrackingLinkMapper trackingLinkMapper;

    @Resource
    private OfferService offerService;

    @Override
    public ClickDO getClick(String clickId) {
        return clickMapper.selectByClickId(clickId);
    }

    @Override
    public PageResult<ClickDO> getClickPage(ClickPageReqVO pageReqVO) {
        return clickMapper.selectPage(pageReqVO);
    }

    @Override
    public String createClick(ClickDO click) {
        clickMapper.insert(click);
        return click.getClickId();
    }

    @Override
    public void validateClickExists(String clickId) {
        if (clickMapper.selectByClickId(clickId) == null) {
            throw exception(CLICK_NOT_EXISTS);
        }
    }

    @Override
    public String recordClickAndGetRedirectUrl(String trackingLinkId, String sub1, String sub2,
                                               String sub3, String sub4, String sub5,
                                               String ip, String userAgent, String referer) {
        TrackingLinkDO trackingLink = findTrackingLink(trackingLinkId);
        if (trackingLink == null) {
            throw exception(TRACKING_LINK_NOT_EXISTS);
        }

        OfferDO offer = offerService.getOffer(trackingLink.getOfferId());
        if (offer == null) {
            log.warn("Offer not found for trackingLink: {}", trackingLinkId);
            return FALLBACK_URL;
        }

        String clickId = generateClickId();

        String finalSub1 = StrUtil.firstNonBlank(sub1, trackingLink.getPresetSub1());
        String finalSub2 = StrUtil.firstNonBlank(sub2, trackingLink.getPresetSub2());
        String finalSub3 = StrUtil.firstNonBlank(sub3, trackingLink.getPresetSub3());
        String finalSub4 = StrUtil.firstNonBlank(sub4, trackingLink.getPresetSub4());
        String finalSub5 = StrUtil.firstNonBlank(sub5, trackingLink.getPresetSub5());

        ClickDO click = ClickDO.builder()
                .clickId(clickId)
                .offerId(offer.getId())
                .sub1(finalSub1)
                .sub2(finalSub2)
                .sub3(finalSub3)
                .sub4(finalSub4)
                .sub5(finalSub5)
                .ip(ip)
                .userAgent(userAgent)
                .referer(referer)
                .clickTime(LocalDateTime.now())
                .build();

        clickMapper.insert(click);
        log.debug("Click recorded: clickId={}, offerId={}", clickId, offer.getId());

        return buildRedirectUrl(offer.getTrackingUrlTemplate(), clickId, finalSub1, finalSub2, finalSub3, finalSub4, finalSub5);
    }

    private TrackingLinkDO findTrackingLink(String trackingLinkId) {
        TrackingLinkDO link = trackingLinkMapper.selectBySlug(trackingLinkId);
        if (link != null) {
            return link;
        }
        try {
            Long id = Long.parseLong(trackingLinkId);
            return trackingLinkMapper.selectById(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String generateClickId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 26).toUpperCase();
    }

    private String buildRedirectUrl(String template, String clickId,
                                    String sub1, String sub2, String sub3, String sub4, String sub5) {
        if (StrUtil.isBlank(template)) {
            return FALLBACK_URL;
        }

        String url = template
                .replace("{click_id}", StrUtil.nullToEmpty(clickId))
                .replace("{sub1}", StrUtil.nullToEmpty(sub1))
                .replace("{sub2}", StrUtil.nullToEmpty(sub2))
                .replace("{sub3}", StrUtil.nullToEmpty(sub3))
                .replace("{sub4}", StrUtil.nullToEmpty(sub4))
                .replace("{sub5}", StrUtil.nullToEmpty(sub5));

        return url;
    }

}
