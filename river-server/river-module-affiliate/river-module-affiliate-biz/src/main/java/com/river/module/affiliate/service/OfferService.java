package com.river.module.affiliate.service;

import com.river.module.affiliate.dal.dataobject.OfferDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * Offer Service 接口
 */
public interface OfferService {

    /**
     * 创建 Offer
     *
     * @param offer Offer
     * @return 编号
     */
    Long createOffer(@Valid OfferDO offer);

    /**
     * 更新 Offer
     *
     * @param offer Offer
     */
    void updateOffer(@Valid OfferDO offer);

    /**
     * 删除 Offer
     *
     * @param id 编号
     */
    void deleteOffer(Long id);

    /**
     * 获得 Offer
     *
     * @param id 编号
     * @return Offer
     */
    OfferDO getOffer(Long id);

    /**
     * 获得 Offer 列表
     *
     * @return Offer 列表
     */
    List<OfferDO> getOfferList();

    /**
     * 根据商家获得 Offer 列表
     *
     * @param merchantId 商家编号
     * @return Offer 列表
     */
    List<OfferDO> getOfferListByMerchantId(Long merchantId);

    /**
     * 根据联盟网络获得 Offer 列表
     *
     * @param networkId 联盟网络编号
     * @return Offer 列表
     */
    List<OfferDO> getOfferListByNetworkId(Long networkId);

    /**
     * 校验 Offer 是否存在
     *
     * @param id 编号
     */
    void validateOfferExists(Long id);

}
