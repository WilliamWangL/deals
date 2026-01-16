package com.river.module.tracking.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.tracking.controller.admin.unattributed.vo.UnattributedConversionPageReqVO;
import com.river.module.tracking.dal.dataobject.UnattributedConversionDO;

/**
 * 未归因转化 Service 接口
 */
public interface UnattributedConversionService {

    /**
     * 获取未归因转化
     *
     * @param id ID
     * @return 未归因转化
     */
    UnattributedConversionDO getUnattributedConversion(Long id);

    /**
     * 获取未归因转化分页
     *
     * @param pageReqVO 分页查询
     * @return 未归因转化分页
     */
    PageResult<UnattributedConversionDO> getUnattributedConversionPage(UnattributedConversionPageReqVO pageReqVO);

}
