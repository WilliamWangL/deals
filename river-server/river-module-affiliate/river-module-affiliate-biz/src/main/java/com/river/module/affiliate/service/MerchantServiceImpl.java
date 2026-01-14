package com.river.module.affiliate.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.affiliate.controller.admin.merchant.vo.MerchantPageReqVO;
import com.river.module.affiliate.dal.dataobject.MerchantDO;
import com.river.module.affiliate.dal.mysql.MerchantMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.river.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.river.module.affiliate.enums.ErrorCodeConstants.*;

@Service
@Validated
public class MerchantServiceImpl implements MerchantService {

    @Resource
    private MerchantMapper merchantMapper;

    @Resource
    private AffiliateNetworkService networkService;

    @Override
    public Long createMerchant(MerchantDO merchant) {
        networkService.validateNetworkExists(merchant.getNetworkId());
        merchantMapper.insert(merchant);
        return merchant.getId();
    }

    @Override
    public void updateMerchant(MerchantDO merchant) {
        validateMerchantExists(merchant.getId());
        networkService.validateNetworkExists(merchant.getNetworkId());
        merchantMapper.updateById(merchant);
    }

    @Override
    public void deleteMerchant(Long id) {
        validateMerchantExists(id);
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
    public PageResult<MerchantDO> getMerchantPage(MerchantPageReqVO pageReqVO) {
        return merchantMapper.selectPage(pageReqVO);
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

    @Override
    public MerchantDO getMerchantBySlug(String slug) {
        return merchantMapper.selectBySlug(slug);
    }

}
