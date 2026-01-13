package com.river.module.campaign.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.campaign.controller.admin.costrecord.vo.CostRecordPageReqVO;
import com.river.module.campaign.dal.dataobject.CostRecordDO;
import jakarta.validation.Valid;

public interface CostRecordService {

    Long createCostRecord(@Valid CostRecordDO costRecord);

    void updateCostRecord(@Valid CostRecordDO costRecord);

    void deleteCostRecord(Long id);

    CostRecordDO getCostRecord(Long id);

    PageResult<CostRecordDO> getCostRecordPage(CostRecordPageReqVO pageReqVO);
}
