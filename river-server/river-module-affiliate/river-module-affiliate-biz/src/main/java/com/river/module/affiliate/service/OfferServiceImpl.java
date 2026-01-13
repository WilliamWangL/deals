package com.river.module.affiliate.service;

import com.river.module.affiliate.dal.dataobject.OfferDO;
import com.river.module.affiliate.dal.mysql.OfferMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.river.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.river.module.affiliate.enums.ErrorCodeConstants.*;

/**
 * Offer Service 实现类
 */
@Service
@Validated
public class OfferServiceImpl implements OfferService {

    @Resource
    private OfferMapper offerMapper;

    @Resource
    private MerchantService merchantService;

    @Resource
    private AffiliateNetworkService networkService;

    @Override
    public Long createOffer(OfferDO offer) {
        // 校验商家存在
        merchantService.validateMerchantExists(offer.getMerchantId());
        // 校验联盟网络存在
        networkService.validateNetworkExists(offer.getNetworkId());
        // 插入
        offerMapper.insert(offer);
        return offer.getId();
    }

    @Override
    public void updateOffer(OfferDO offer) {
        // 校验存在
        validateOfferExists(offer.getId());
        // 校验商家存在
        merchantService.validateMerchantExists(offer.getMerchantId());
        // 校验联盟网络存在
        networkService.validateNetworkExists(offer.getNetworkId());
        // 更新
        offerMapper.updateById(offer);
    }

    @Override
    public void deleteOffer(Long id) {
        // 校验存在
        validateOfferExists(id);
        // 删除
        offerMapper.deleteById(id);
    }

    @Override
    public OfferDO getOffer(Long id) {
        return offerMapper.selectById(id);
    }

    @Override
    public List<OfferDO> getOfferList() {
        return offerMapper.selectList();
    }

    @Override
    public List<OfferDO> getOfferListByMerchantId(Long merchantId) {
        return offerMapper.selectList(OfferDO::getMerchantId, merchantId);
    }

    @Override
    public List<OfferDO> getOfferListByNetworkId(Long networkId) {
        return offerMapper.selectList(OfferDO::getNetworkId, networkId);
    }

    @Override
    public void validateOfferExists(Long id) {
        if (offerMapper.selectById(id) == null) {
            throw exception(OFFER_NOT_EXISTS);
        }
    }

}
