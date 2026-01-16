package com.river.module.coupon.dal.mysql;

import com.river.framework.common.pojo.PageResult;
import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.river.module.coupon.controller.admin.coupon.vo.CouponPageReqVO;
import com.river.module.coupon.dal.dataobject.CouponDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CouponMapper extends BaseMapperX<CouponDO> {

    default PageResult<CouponDO> selectPage(CouponPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CouponDO>()
                .eqIfPresent(CouponDO::getMerchantId, reqVO.getMerchantId())
                .eqIfPresent(CouponDO::getOfferId, reqVO.getOfferId())
                .likeIfPresent(CouponDO::getCode, reqVO.getCode())
                .eqIfPresent(CouponDO::getDiscountType, reqVO.getDiscountType())
                .eqIfPresent(CouponDO::getSource, reqVO.getSource())
                .eqIfPresent(CouponDO::getVerified, reqVO.getVerified())
                .eqIfPresent(CouponDO::getStatus, reqVO.getStatus())
                .orderByDesc(CouponDO::getId));
    }

    default Long selectCountByMerchantId(Long merchantId) {
        return selectCount(new LambdaQueryWrapperX<CouponDO>()
                .eq(CouponDO::getMerchantId, merchantId));
    }

    /**
     * 根据联盟网络ID和外部ID查询优惠券
     * 用于同步时判重
     */
    default CouponDO selectByNetworkAndExternalId(Long networkId, String externalId) {
        return selectOne(new LambdaQueryWrapperX<CouponDO>()
                .eq(CouponDO::getNetworkId, networkId)
                .eq(CouponDO::getExternalId, externalId));
    }

}
