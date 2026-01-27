package com.river.module.tracking.service;

import cn.hutool.core.util.StrUtil;
import com.river.framework.common.pojo.PageResult;
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

        String clickId = generateClickId();

        String finalSub1 = StrUtil.firstNonBlank(sub1, trackingLink.getPresetSub1());
        String finalSub2 = StrUtil.firstNonBlank(sub2, trackingLink.getPresetSub2());
        String finalSub3 = StrUtil.firstNonBlank(sub3, trackingLink.getPresetSub3());
        String finalSub4 = StrUtil.firstNonBlank(sub4, trackingLink.getPresetSub4());
        String finalSub5 = StrUtil.firstNonBlank(sub5, trackingLink.getPresetSub5());

        // 构建 ClickDO，使用 trackingLink 的 targetType 和 targetId
        ClickDO click = ClickDO.builder()
                .clickId(clickId)
                .targetType(trackingLink.getTargetType())
                .targetId(trackingLink.getTargetId())
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
        log.debug("Click recorded: clickId={}, targetType={}, targetId={}",
                clickId, trackingLink.getTargetType(), trackingLink.getTargetId());

        // 构建最终跳转 URL，处理占位符替换或参数追加
        return buildFinalUrl(trackingLink.getTrackingUrl(), clickId,
                finalSub1, finalSub2, finalSub3, finalSub4, finalSub5);
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

    /**
     * 构建最终跳转 URL
     * 1. 如果 URL 包含占位符，替换它们
     * 2. 如果 URL 不包含占位符，追加参数
     */
    private String buildFinalUrl(String url, String clickId,
                                 String sub1, String sub2, String sub3,
                                 String sub4, String sub5) {
        if (url == null) {
            return FALLBACK_URL;
        }

        // 检查是否包含占位符
        boolean hasPlaceholder = url.contains("{click_id}") ||
                                 url.contains("{sub1}") ||
                                 url.contains("{sub2}");

        if (hasPlaceholder) {
            // 情况1：替换占位符
            url = url.replace("{click_id}", clickId);
            url = url.replace("{sub1}", StrUtil.nullToEmpty(sub1));
            url = url.replace("{sub2}", StrUtil.nullToEmpty(sub2));
            url = url.replace("{sub3}", StrUtil.nullToEmpty(sub3));
            url = url.replace("{sub4}", StrUtil.nullToEmpty(sub4));
            url = url.replace("{sub5}", StrUtil.nullToEmpty(sub5));
        } else {
            // 情况2：追加参数
            StringBuilder params = new StringBuilder();
            params.append("subid=").append(clickId);
            if (StrUtil.isNotBlank(sub1)) {
                params.append("&subid1=").append(sub1);
            }
            if (StrUtil.isNotBlank(sub2)) {
                params.append("&subid2=").append(sub2);
            }
            if (StrUtil.isNotBlank(sub3)) {
                params.append("&subid3=").append(sub3);
            }
            if (StrUtil.isNotBlank(sub4)) {
                params.append("&subid4=").append(sub4);
            }
            if (StrUtil.isNotBlank(sub5)) {
                params.append("&subid5=").append(sub5);
            }

            // 根据 URL 是否已有查询参数决定使用 ? 或 &
            url = url + (url.contains("?") ? "&" : "?") + params;
        }

        return url;
    }

}
