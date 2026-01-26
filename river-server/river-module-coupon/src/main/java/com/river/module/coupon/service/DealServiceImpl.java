package com.river.module.coupon.service;

import com.river.framework.common.enums.CommonStatusEnum;
import com.river.framework.common.pojo.PageResult;
import com.river.module.coupon.controller.admin.deal.vo.DealPageReqVO;
import com.river.module.coupon.dal.dataobject.DealDO;
import com.river.module.coupon.dal.mysql.DealMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

import static com.river.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.river.module.coupon.enums.ErrorCodeConstants.*;

@Service
@Validated
public class DealServiceImpl implements DealService {

    @Resource
    private DealMapper dealMapper;

    @Override
    public Long createDeal(DealDO deal) {
        dealMapper.insert(deal);
        return deal.getId();
    }

    @Override
    public void updateDeal(DealDO deal) {
        validateDealExists(deal.getId());
        dealMapper.updateById(deal);
    }

    @Override
    public void deleteDeal(Long id) {
        validateDealExists(id);
        dealMapper.deleteById(id);
    }

    @Override
    public DealDO getDeal(Long id) {
        return dealMapper.selectById(id);
    }

    @Override
    public List<DealDO> getDealList() {
        return dealMapper.selectList();
    }

    @Override
    public PageResult<DealDO> getDealPage(DealPageReqVO pageReqVO) {
        return dealMapper.selectPage(pageReqVO);
    }

    @Override
    public void validateDealExists(Long id) {
        if (dealMapper.selectById(id) == null) {
            throw exception(DEAL_NOT_EXISTS);
        }
    }

    @Override
    public DealDO getDealBySlug(String slug) {
        return dealMapper.selectBySlug(slug);
    }

    @Override
    public int updateExpiredDeals() {
        DealDO updateObj = new DealDO().setStatus(CommonStatusEnum.DISABLE.getStatus());
        return dealMapper.update(updateObj,
                new com.river.framework.mybatis.core.query.LambdaQueryWrapperX<DealDO>()
                        .eq(DealDO::getStatus, CommonStatusEnum.ENABLE.getStatus())
                        .isNotNull(DealDO::getEndTime)
                        .lt(DealDO::getEndTime, LocalDateTime.now())
                        .eq(DealDO::getDeleted, false));
    }

}
