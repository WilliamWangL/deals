package com.river.module.coupon.dal.mysql;

import cn.hutool.core.collection.CollUtil;
import com.river.framework.common.pojo.PageResult;
import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.river.module.coupon.controller.admin.coupon.vo.CouponPageReqVO;
import com.river.module.coupon.dal.dataobject.CouponDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface CouponMapper extends BaseMapperX<CouponDO> {

    default PageResult<CouponDO> selectPage(CouponPageReqVO reqVO) {
        String regionSql = "";
        if (CollUtil.isNotEmpty(reqVO.getRegions())) {
            // PostgreSQL 使用 string_to_array + ANY 替代 MySQL 的 FIND_IN_SET
            regionSql = "(" + reqVO.getRegions().stream()
                    .map(region -> "string_to_array(regions, ',') @> ARRAY['" + region + "']")
                    .collect(Collectors.joining(" OR ")) + ")";
        }
        LambdaQueryWrapperX<CouponDO> qry = new LambdaQueryWrapperX<CouponDO>()
                .eqIfPresent(CouponDO::getMerchantId, reqVO.getMerchantId())
                .eqIfPresent(CouponDO::getOfferId, reqVO.getOfferId())
                .likeIfPresent(CouponDO::getCode, reqVO.getCode())
                .eqIfPresent(CouponDO::getDiscountType, reqVO.getDiscountType())
                .eqIfPresent(CouponDO::getSource, reqVO.getSource())
                .eqIfPresent(CouponDO::getVerified, reqVO.getVerified())
                .eqIfPresent(CouponDO::getStatus, reqVO.getStatus())
                .orderByDesc(CouponDO::getId);
        if (!regionSql.isEmpty()) {
            qry.apply(regionSql);
        }
        // PostgreSQL 使用 string_to_array + @> 操作符检查 category_ids 是否包含指定的 categoryId
        if (reqVO.getCategoryId() != null) {
            qry.apply("string_to_array(category_ids, ',') @> ARRAY[{0}]::varchar[]",
                    String.valueOf(reqVO.getCategoryId()));
        }
        return selectPage(reqVO, qry);
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

    /**
     * 批量查询联盟网络下的优惠券（按 externalId 列表）
     * 用于同步时预加载已存在数据，实现幂等写入
     *
     * @param networkId   联盟网络 ID
     * @param externalIds 外部 ID 列表
     * @return 优惠券列表
     */
    default List<CouponDO> selectListByNetworkAndExternalIds(Long networkId, List<String> externalIds) {
        if (externalIds == null || externalIds.isEmpty()) {
            return List.of();
        }
        return selectList(new LambdaQueryWrapperX<CouponDO>()
                .eq(CouponDO::getNetworkId, networkId)
                .in(CouponDO::getExternalId, externalIds));
    }

}
