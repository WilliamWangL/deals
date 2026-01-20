package com.river.module.affiliate.dal.mysql;

import com.river.framework.common.pojo.PageResult;
import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.river.module.affiliate.controller.admin.merchant.vo.MerchantPageReqVO;
import com.river.module.affiliate.dal.dataobject.MerchantDO;
import org.apache.ibatis.annotations.Mapper;

import static com.river.framework.common.util.region.RegionUtils.GLOBAL;
import static com.river.framework.common.util.region.RegionUtils.GLOBAL_CODE;

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

    /**
     * 根据地区查询商家分页（SQL层面过滤，效率更高）
     * 过滤逻辑：
     * - region 为空或 GLOBAL：返回所有数据
     * - 否则：返回匹配 region 的数据，或 regions 包含 GLOBAL_CODE/为空的数据
     */
    default PageResult<MerchantDO> selectPageByRegion(MerchantPageReqVO reqVO, String region) {
        LambdaQueryWrapperX<MerchantDO> wrapper = new LambdaQueryWrapperX<MerchantDO>()
                .eqIfPresent(MerchantDO::getNetworkId, reqVO.getNetworkId())
                .likeIfPresent(MerchantDO::getName, reqVO.getName())
                .likeIfPresent(MerchantDO::getDomain, reqVO.getDomain())
                .eqIfPresent(MerchantDO::getStatus, reqVO.getStatus())
                .orderByDesc(MerchantDO::getId);

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
                .eq(MerchantDO::getRegions, GLOBAL_CODE)
                .or()
                .isNull(MerchantDO::getRegions)
                .or()
                .eq(MerchantDO::getRegions, "")
            );
        }
        // GLOBAL 或空值不过滤，返回所有数据

        return selectPage(reqVO, wrapper);
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
