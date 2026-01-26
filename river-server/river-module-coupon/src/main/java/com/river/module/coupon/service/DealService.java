package com.river.module.coupon.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.coupon.controller.admin.deal.vo.DealPageReqVO;
import com.river.module.coupon.dal.dataobject.DealDO;

import java.util.List;

public interface DealService {

    Long createDeal(DealDO deal);

    void updateDeal(DealDO deal);

    void deleteDeal(Long id);

    DealDO getDeal(Long id);

    List<DealDO> getDealList();

    PageResult<DealDO> getDealPage(DealPageReqVO pageReqVO);

    void validateDealExists(Long id);

    DealDO getDealBySlug(String slug);

    int updateExpiredDeals();

}
