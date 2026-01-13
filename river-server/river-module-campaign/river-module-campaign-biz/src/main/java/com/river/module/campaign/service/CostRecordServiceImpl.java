package com.river.module.campaign.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.campaign.controller.admin.costrecord.vo.CostRecordPageReqVO;
import com.river.module.campaign.dal.dataobject.CostRecordDO;
import com.river.module.campaign.dal.mysql.CostRecordMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.river.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.river.module.campaign.enums.ErrorCodeConstants.*;

@Service
@Validated
public class CostRecordServiceImpl implements CostRecordService {

    @Resource
    private CostRecordMapper costRecordMapper;

    @Resource
    private CampaignService campaignService;

    @Override
    public Long createCostRecord(CostRecordDO costRecord) {
        campaignService.validateCampaignExists(costRecord.getCampaignId());
        costRecordMapper.insert(costRecord);
        return costRecord.getId();
    }

    @Override
    public void updateCostRecord(CostRecordDO costRecord) {
        validateCostRecordExists(costRecord.getId());
        campaignService.validateCampaignExists(costRecord.getCampaignId());
        costRecordMapper.updateById(costRecord);
    }

    @Override
    public void deleteCostRecord(Long id) {
        validateCostRecordExists(id);
        costRecordMapper.deleteById(id);
    }

    @Override
    public CostRecordDO getCostRecord(Long id) {
        return costRecordMapper.selectById(id);
    }

    @Override
    public PageResult<CostRecordDO> getCostRecordPage(CostRecordPageReqVO pageReqVO) {
        return costRecordMapper.selectPage(pageReqVO);
    }

    private void validateCostRecordExists(Long id) {
        if (costRecordMapper.selectById(id) == null) {
            throw exception(COST_RECORD_NOT_EXISTS);
        }
    }
}
