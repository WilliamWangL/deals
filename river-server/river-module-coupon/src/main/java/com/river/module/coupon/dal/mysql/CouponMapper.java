package com.river.module.coupon.dal.mysql;

import com.river.framework.common.pojo.PageResult;
import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.river.module.coupon.controller.admin.coupon.vo.CouponPageReqVO;
import com.river.module.coupon.dal.dataobject.CouponDO;
import org.apache.ibatis.annotations.Mapper;

import static com.river.framework.common.util.region.RegionUtils.GLOBAL;
import static com.river.framework.common.util.region.RegionUtils.GLOBAL_CODE;

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

    /**
     * 根据地区查询 Coupon 分页（SQL层面过滤，效率更高）
     * 过滤逻辑：
     * - region 为空或 GLOBAL：返回所有数据
     * - 否则：返回匹配 region 的数据，或 regions 包含 GLOBAL_CODE/为空的数据
     */
    default PageResult<CouponDO> selectPageByRegion(CouponPageReqVO reqVO, String region) {
        LambdaQueryWrapperX<CouponDO> wrapper = new LambdaQueryWrapperX<CouponDO>()
                .eqIfPresent(CouponDO::getMerchantId, reqVO.getMerchantId())
                .eqIfPresent(CouponDO::getOfferId, reqVO.getOfferId())
                .likeIfPresent(CouponDO::getCode, reqVO.getCode())
                .eqIfPresent(CouponDO::getDiscountType, reqVO.getDiscountType())
                .eqIfPresent(CouponDO::getSource, reqVO.getSource())
                .eqIfPresent(CouponDO::getVerified, reqVO.getVerified())
                .eqIfPresent(CouponDO::getStatus, reqVO.getStatus())
                .orderByDesc(CouponDO::getId);

        // 地区过滤条件
        if (region != null && !region.isBlank() && !GLOBAL.equals(region)) {
            // 验证 region 格式（2位大写字母）
            if (!region.matches("^[A-Z]{2}$")) {
                // 无效格式，返回空结果
                return new PageResult<>(java.util.List.of(), 0L);
            }
            // 匹配具体国家或全球数据（regions为空/包含GLOBAL_CODE）
            wrapper.and(w -> w
                .apply("FIND_IN_SET({0}, regions)", region)
                .or()
                .eq(CouponDO::getRegions, GLOBAL_CODE)
                .or()
                .isNull(CouponDO::getRegions)
                .or()
                .eq(CouponDO::getRegions, "")
            );
        }
        // GLOBAL 或空值不过滤，返回所有数据

        return selectPage(reqVO, wrapper);
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
