package com.river.module.affiliate.service.region;

import com.river.module.affiliate.controller.app.region.vo.RegionRespVO;

import java.util.List;

/**
 * 地区查询服务接口
 */
public interface RegionService {

    /**
     * 获取有数据的可用地区列表
     */
    List<RegionRespVO> getAvailableRegions();
}
