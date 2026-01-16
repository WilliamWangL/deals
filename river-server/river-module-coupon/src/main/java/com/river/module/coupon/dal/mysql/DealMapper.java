package com.river.module.coupon.dal.mysql;

import com.river.framework.common.pojo.PageResult;
import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.river.module.coupon.controller.admin.deal.vo.DealPageReqVO;
import com.river.module.coupon.dal.dataobject.DealDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DealMapper extends BaseMapperX<DealDO> {

    default PageResult<DealDO> selectPage(DealPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DealDO>()
                .eqIfPresent(DealDO::getMerchantId, reqVO.getMerchantId())
                .eqIfPresent(DealDO::getOfferId, reqVO.getOfferId())
                .likeIfPresent(DealDO::getTitle, reqVO.getTitle())
                .eqIfPresent(DealDO::getFeatured, reqVO.getFeatured())
                .eqIfPresent(DealDO::getStatus, reqVO.getStatus())
                .orderByDesc(DealDO::getId));
    }

    default DealDO selectBySlug(String slug) {
        return selectOne(DealDO::getSlug, slug);
    }

    default Long selectCountByMerchantId(Long merchantId) {
        return selectCount(new LambdaQueryWrapperX<DealDO>()
                .eq(DealDO::getMerchantId, merchantId));
    }

    /**
     * 根据联盟网络ID和外部ID查询 Deal
     * 用于同步时判重
     */
    default DealDO selectByNetworkAndExternalId(Long networkId, String externalId) {
        return selectOne(new LambdaQueryWrapperX<DealDO>()
                .eq(DealDO::getNetworkId, networkId)
                .eq(DealDO::getExternalId, externalId));
    }

}
