package com.river.module.affiliate.service;

import com.river.module.affiliate.dal.dataobject.MerchantDO;
import com.river.module.affiliate.dal.mysql.MerchantMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.river.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.river.module.affiliate.enums.ErrorCodeConstants.*;

/**
 * 商家 Service 实现类
 */
@Service
@Validated
public class MerchantServiceImpl implements MerchantService {

    @Resource
    private MerchantMapper merchantMapper;

    @Resource
    private AffiliateNetworkService networkService;

    @Override
    public Long createMerchant(MerchantDO merchant) {
        // 校验联盟网络存在
        networkService.validateNetworkExists(merchant.getNetworkId());
        // 插入
        merchantMapper.insert(merchant);
        return merchant.getId();
    }

    @Override
    public void updateMerchant(MerchantDO merchant) {
        // 校验存在
        validateMerchantExists(merchant.getId());
        // 校验联盟网络存在
        networkService.validateNetworkExists(merchant.getNetworkId());
        // 更新
        merchantMapper.updateById(merchant);
    }

    @Override
    public void deleteMerchant(Long id) {
        // 校验存在
        validateMerchantExists(id);
        // 删除
        merchantMapper.deleteById(id);
    }

    @Override
    public MerchantDO getMerchant(Long id) {
        return merchantMapper.selectById(id);
    }

    @Override
    public List<MerchantDO> getMerchantList() {
        return merchantMapper.selectList();
    }

    @Override
    public List<MerchantDO> getMerchantListByNetworkId(Long networkId) {
        return merchantMapper.selectList(MerchantDO::getNetworkId, networkId);
    }

    @Override
    public void validateMerchantExists(Long id) {
        if (merchantMapper.selectById(id) == null) {
            throw exception(MERCHANT_NOT_EXISTS);
        }
    }

}
