package com.river.module.affiliate.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.affiliate.controller.admin.merchant.vo.MerchantPageReqVO;
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
     * 获得商家分页
     *
     * @param pageReqVO 分页查询
     * @return 商家分页
     */
    PageResult<MerchantDO> getMerchantPage(MerchantPageReqVO pageReqVO);

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

    /**
     * 根据 slug 获得商家
     *
     * @param slug URL友好标识
     * @return 商家
     */
    MerchantDO getMerchantBySlug(String slug);

    /**
     * 根据编号列表获得商家列表
     *
     * @param ids 编号列表
     * @return 商家列表
     */
    List<MerchantDO> getMerchantListByIds(java.util.Collection<Long> ids);

}
