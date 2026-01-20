package com.river.module.system.service.region;

import com.river.module.system.controller.app.region.vo.RegionRespVO;

import java.util.List;

public interface RegionService {

    /**
     * 获取有数据的可用地区列表
     */
    List<RegionRespVO> getAvailableRegions();
}
