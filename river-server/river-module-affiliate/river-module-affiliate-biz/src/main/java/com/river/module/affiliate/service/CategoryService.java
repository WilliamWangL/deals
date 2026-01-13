package com.river.module.affiliate.service;

import com.river.module.affiliate.controller.admin.category.vo.CategoryListReqVO;
import com.river.module.affiliate.dal.dataobject.CategoryDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 分类 Service 接口
 */
public interface CategoryService {

    /**
     * 创建分类
     *
     * @param category 分类
     * @return 编号
     */
    Long createCategory(@Valid CategoryDO category);

    /**
     * 更新分类
     *
     * @param category 分类
     */
    void updateCategory(@Valid CategoryDO category);

    /**
     * 删除分类
     *
     * @param id 编号
     */
    void deleteCategory(Long id);

    /**
     * 获得分类
     *
     * @param id 编号
     * @return 分类
     */
    CategoryDO getCategory(Long id);

    /**
     * 获得分类列表
     *
     * @return 分类列表
     */
    List<CategoryDO> getCategoryList();

    /**
     * 获得分类列表
     *
     * @param listReqVO 列表查询
     * @return 分类列表
     */
    List<CategoryDO> getCategoryList(CategoryListReqVO listReqVO);

    /**
     * 获得子分类列表
     *
     * @param parentId 父分类编号
     * @return 子分类列表
     */
    List<CategoryDO> getCategoryListByParentId(Long parentId);

    /**
     * 校验分类是否存在
     *
     * @param id 编号
     */
    void validateCategoryExists(Long id);

}
