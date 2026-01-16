package com.river.module.affiliate.dal.mysql;

import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.river.module.affiliate.dal.dataobject.CategoryMappingDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMappingMapper extends BaseMapperX<CategoryMappingDO> {

    /**
     * 根据联盟网络ID和外部分类ID查询映射
     */
    default CategoryMappingDO selectByNetworkAndExternalId(Long networkId, String externalId) {
        return selectOne(new LambdaQueryWrapperX<CategoryMappingDO>()
                .eq(CategoryMappingDO::getNetworkId, networkId)
                .eq(CategoryMappingDO::getExternalId, externalId));
    }

    /**
     * 查询联盟网络下所有映射
     */
    default List<CategoryMappingDO> selectListByNetworkId(Long networkId) {
        return selectList(new LambdaQueryWrapperX<CategoryMappingDO>()
                .eq(CategoryMappingDO::getNetworkId, networkId)
                .orderByAsc(CategoryMappingDO::getExternalId));
    }

    /**
     * 查询未映射的分类（categoryId 为空）
     */
    default List<CategoryMappingDO> selectUnmappedByNetworkId(Long networkId) {
        return selectList(new LambdaQueryWrapperX<CategoryMappingDO>()
                .eq(CategoryMappingDO::getNetworkId, networkId)
                .isNull(CategoryMappingDO::getCategoryId)
                .orderByAsc(CategoryMappingDO::getExternalName));
    }

    /**
     * 查询映射到某个本地分类的所有联盟分类
     */
    default List<CategoryMappingDO> selectByCategoryId(Long categoryId) {
        return selectList(new LambdaQueryWrapperX<CategoryMappingDO>()
                .eq(CategoryMappingDO::getCategoryId, categoryId));
    }
}
