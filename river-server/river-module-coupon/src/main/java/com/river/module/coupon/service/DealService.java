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

    /**
     * 根据地区获得 Deal 分页
     *
     * @param pageReqVO 分页查询
     * @param region 地区代码
     * @return Deal 分页
     */
    PageResult<DealDO> getDealPageByRegion(DealPageReqVO pageReqVO, String region);

    /**
     * 根据地区获得 Deal 列表
     *
     * @param region 地区代码
     * @return Deal 列表
     */
    List<DealDO> getDealListByRegion(String region);

}
