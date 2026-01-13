package com.river.module.affiliate.service;

import com.river.module.affiliate.dal.dataobject.MerchantDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 商家 Service 接口
 */
public interface MerchantService {

    /**
     * 创建商家
     *
     * @param merchant 商家
     * @return 编号
     */
    Long createMerchant(@Valid MerchantDO merchant);

    /**
     * 更新商家
     *
     * @param merchant 商家
     */
    void updateMerchant(@Valid MerchantDO merchant);

    /**
     * 删除商家
     *
     * @param id 编号
     */
    void deleteMerchant(Long id);

    /**
     * 获得商家
     *
     * @param id 编号
     * @return 商家
     */
    MerchantDO getMerchant(Long id);

    /**
     * 获得商家列表
     *
     * @return 商家列表
     */
    List<MerchantDO> getMerchantList();

    /**
     * 根据联盟网络获得商家列表
     *
     * @param networkId 联盟网络编号
     * @return 商家列表
     */
    List<MerchantDO> getMerchantListByNetworkId(Long networkId);

    /**
     * 校验商家是否存在
     *
     * @param id 编号
     */
    void validateMerchantExists(Long id);

}
