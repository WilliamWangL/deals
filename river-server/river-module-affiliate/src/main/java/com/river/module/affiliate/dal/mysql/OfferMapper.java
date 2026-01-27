package com.river.module.affiliate.dal.mysql;

import com.river.framework.common.pojo.PageResult;
import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.river.module.affiliate.controller.admin.offer.vo.OfferPageReqVO;
import com.river.module.affiliate.dal.dataobject.OfferDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

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

    /**
     * 批量查询商家下的 Offers（按 externalId 列表）
     * 用于同步时预加载已存在数据，实现幂等写入
     *
     * @param merchantId  商家 ID
     * @param externalIds 外部 ID 列表
     * @return Offer 列表
     */
    default List<OfferDO> selectListByMerchantAndExternalIds(Long merchantId, List<String> externalIds) {
        if (externalIds == null || externalIds.isEmpty()) {
            return List.of();
        }
        return selectList(new LambdaQueryWrapperX<OfferDO>()
                .eq(OfferDO::getMerchantId, merchantId)
                .in(OfferDO::getExternalId, externalIds));
    }

    /**
     * 批量查询网络下的 Offers（按 externalId 列表）
     * 用于同步时预加载已存在数据，基于唯一约束 (network_id, external_id, tenant_id)
     *
     * @param networkId   网络 ID
     * @param externalIds 外部 ID 列表
     * @return Offer 列表
     */
    default List<OfferDO> selectListByNetworkAndExternalIds(Long networkId, List<String> externalIds) {
        if (externalIds == null || externalIds.isEmpty()) {
            return List.of();
        }
        return selectList(new LambdaQueryWrapperX<OfferDO>()
                .eq(OfferDO::getNetworkId, networkId)
                .in(OfferDO::getExternalId, externalIds));
    }

}
