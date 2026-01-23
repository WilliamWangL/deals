package com.river.framework.common.biz.tracking;

import com.river.framework.common.biz.tracking.dto.TrackingLinkCreateReqDTO;
import com.river.framework.common.biz.tracking.dto.TrackingLinkRespDTO;

/**
 * 追踪链接 API 接口
 * 用于跨模块调用，避免循环依赖
 */
public interface TrackingLinkCommonApi {

    /**
     * 创建或更新追踪链接
     * 如果已存在相同 target_type 和 target_id 的链接，则更新；否则创建新链接
     *
     * @param reqDTO 追踪链接创建请求
     * @return 追踪链接响应
     */
    TrackingLinkRespDTO createOrUpdateTrackingLink(TrackingLinkCreateReqDTO reqDTO);

    /**
     * 根据 target 类型获取追踪链接
     *
     * @param targetType 目标类型
     * @param targetId   目标 ID
     * @return 追踪链接响应，不存在则返回 null
     */
    TrackingLinkRespDTO getTrackingLink(Integer targetType, Long targetId);

}
