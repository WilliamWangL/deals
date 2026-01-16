package com.river.module.tracking.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.tracking.controller.admin.attribution.vo.AttributionPageReqVO;
import com.river.module.tracking.dal.dataobject.AttributionDO;

/**
 * 归因记录 Service 接口
 */
public interface AttributionService {

    /**
     * 获取归因记录
     *
     * @param id ID
     * @return 归因记录
     */
    AttributionDO getAttribution(Long id);

    /**
     * 获取归因记录分页
     *
     * @param pageReqVO 分页查询
     * @return 归因记录分页
     */
    PageResult<AttributionDO> getAttributionPage(AttributionPageReqVO pageReqVO);

}
