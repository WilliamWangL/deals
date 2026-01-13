package com.river.module.affiliate.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.affiliate.dal.dataobject.AffiliateNetworkDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 联盟网络 Service 接口
 */
public interface AffiliateNetworkService {

    /**
     * 创建联盟网络
     *
     * @param network 联盟网络
     * @return 编号
     */
    Long createNetwork(@Valid AffiliateNetworkDO network);

    /**
     * 更新联盟网络
     *
     * @param network 联盟网络
     */
    void updateNetwork(@Valid AffiliateNetworkDO network);

    /**
     * 删除联盟网络
     *
     * @param id 编号
     */
    void deleteNetwork(Long id);

    /**
     * 获得联盟网络
     *
     * @param id 编号
     * @return 联盟网络
     */
    AffiliateNetworkDO getNetwork(Long id);

    /**
     * 获得联盟网络列表
     *
     * @return 联盟网络列表
     */
    List<AffiliateNetworkDO> getNetworkList();

    /**
     * 校验联盟网络是否存在
     *
     * @param id 编号
     */
    void validateNetworkExists(Long id);

}
