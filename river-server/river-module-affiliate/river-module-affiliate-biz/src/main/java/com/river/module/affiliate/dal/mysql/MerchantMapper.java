package com.river.module.affiliate.dal.mysql;

import com.river.framework.common.pojo.PageResult;
import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.river.module.affiliate.controller.admin.merchant.vo.MerchantPageReqVO;
import com.river.module.affiliate.dal.dataobject.MerchantDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MerchantMapper extends BaseMapperX<MerchantDO> {

    default PageResult<MerchantDO> selectPage(MerchantPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MerchantDO>()
                .eqIfPresent(MerchantDO::getNetworkId, reqVO.getNetworkId())
                .likeIfPresent(MerchantDO::getName, reqVO.getName())
                .likeIfPresent(MerchantDO::getDomain, reqVO.getDomain())
                .eqIfPresent(MerchantDO::getStatus, reqVO.getStatus())
                .orderByDesc(MerchantDO::getId));
    }

    default MerchantDO selectBySlug(String slug) {
        return selectOne(MerchantDO::getSlug, slug);
    }

    default MerchantDO selectByNetworkAndExternalId(Long networkId, String externalId) {
        return selectOne(new LambdaQueryWrapperX<MerchantDO>()
                .eq(MerchantDO::getNetworkId, networkId)
                .eq(MerchantDO::getExternalId, externalId));
    }

}
