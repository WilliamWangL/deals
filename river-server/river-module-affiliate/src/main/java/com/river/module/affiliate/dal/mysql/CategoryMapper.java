package com.river.module.affiliate.dal.mysql;

import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.river.module.affiliate.controller.admin.category.vo.CategoryListReqVO;
import com.river.module.affiliate.dal.dataobject.CategoryDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper extends BaseMapperX<CategoryDO> {

    default List<CategoryDO> selectList(CategoryListReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<CategoryDO>()
                .likeIfPresent(CategoryDO::getName, reqVO.getName())
                .eqIfPresent(CategoryDO::getStatus, reqVO.getStatus())
                .eqIfPresent(CategoryDO::getParentId, reqVO.getParentId())
                .orderByAsc(CategoryDO::getSort)
                .orderByAsc(CategoryDO::getId));
    }

}
