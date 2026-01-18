package com.river.module.coupon.dal.mysql;

import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.module.coupon.dal.dataobject.MerchantDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * 商家 Mapper（只读）
 *
 * 用于 coupon 模块查询商家信息，避免与 affiliate 模块的循环依赖
 */
@Mapper
public interface MerchantMapper extends BaseMapperX<MerchantDO> {

    default List<MerchantDO> selectListByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return selectBatchIds(ids);
    }

}
