package com.river.module.affiliate.dal.mysql;

import com.river.framework.common.pojo.PageResult;
import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.river.module.affiliate.controller.admin.offer.vo.OfferPageReqVO;
import com.river.module.affiliate.dal.dataobject.OfferDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OfferMapper extends BaseMapperX<OfferDO> {

    default PageResult<OfferDO> selectPage(OfferPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<OfferDO>()
                .eqIfPresent(OfferDO::getMerchantId, reqVO.getMerchantId())
                .eqIfPresent(OfferDO::getNetworkId, reqVO.getNetworkId())
                .likeIfPresent(OfferDO::getName, reqVO.getName())
                .eqIfPresent(OfferDO::getCommissionType, reqVO.getCommissionType())
                .eqIfPresent(OfferDO::getStatus, reqVO.getStatus())
                .eqIfPresent(OfferDO::getFeatured, reqVO.getFeatured())
                .orderByDesc(OfferDO::getId));
    }

    default OfferDO selectByMerchantAndExternalId(Long merchantId, String externalId) {
        return selectOne(new LambdaQueryWrapperX<OfferDO>()
                .eq(OfferDO::getMerchantId, merchantId)
                .eq(OfferDO::getExternalId, externalId));
    }

}
