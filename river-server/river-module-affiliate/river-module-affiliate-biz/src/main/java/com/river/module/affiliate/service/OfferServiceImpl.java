package com.river.module.affiliate.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.affiliate.controller.admin.offer.vo.OfferPageReqVO;
import com.river.module.affiliate.dal.dataobject.OfferDO;
import com.river.module.affiliate.dal.mysql.OfferMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.river.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.river.module.affiliate.enums.ErrorCodeConstants.*;

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
        merchantService.validateMerchantExists(offer.getMerchantId());
        networkService.validateNetworkExists(offer.getNetworkId());
        offerMapper.insert(offer);
        return offer.getId();
    }

    @Override
    public void updateOffer(OfferDO offer) {
        validateOfferExists(offer.getId());
        merchantService.validateMerchantExists(offer.getMerchantId());
        networkService.validateNetworkExists(offer.getNetworkId());
        offerMapper.updateById(offer);
    }

    @Override
    public void deleteOffer(Long id) {
        validateOfferExists(id);
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
    public PageResult<OfferDO> getOfferPage(OfferPageReqVO pageReqVO) {
        return offerMapper.selectPage(pageReqVO);
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
