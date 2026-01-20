package com.river.module.coupon.dal.mysql;

import com.river.framework.common.pojo.PageResult;
import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.river.module.coupon.controller.admin.deal.vo.DealPageReqVO;
import com.river.module.coupon.dal.dataobject.DealDO;
import org.apache.ibatis.annotations.Mapper;

import static com.river.framework.common.util.region.RegionUtils.GLOBAL;
import static com.river.framework.common.util.region.RegionUtils.GLOBAL_CODE;

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

    /**
     * 根据地区查询 Deal 分页（SQL层面过滤，效率更高）
     * 过滤逻辑：
     * - region 为空或 GLOBAL：返回所有数据
     * - 否则：返回匹配 region 的数据，或 regions 包含 GLOBAL_CODE/为空的数据
     */
    default PageResult<DealDO> selectPageByRegion(DealPageReqVO reqVO, String region) {
        LambdaQueryWrapperX<DealDO> wrapper = new LambdaQueryWrapperX<DealDO>()
                .eqIfPresent(DealDO::getMerchantId, reqVO.getMerchantId())
                .eqIfPresent(DealDO::getOfferId, reqVO.getOfferId())
                .likeIfPresent(DealDO::getTitle, reqVO.getTitle())
                .eqIfPresent(DealDO::getFeatured, reqVO.getFeatured())
                .eqIfPresent(DealDO::getStatus, reqVO.getStatus())
                .orderByDesc(DealDO::getId);

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
                .eq(DealDO::getRegions, GLOBAL_CODE)
                .or()
                .isNull(DealDO::getRegions)
                .or()
                .eq(DealDO::getRegions, "")
            );
        }
        // GLOBAL 或空值不过滤，返回所有数据

        return selectPage(reqVO, wrapper);
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
