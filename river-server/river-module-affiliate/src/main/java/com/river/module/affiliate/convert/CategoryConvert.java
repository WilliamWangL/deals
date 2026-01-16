package com.river.module.affiliate.convert;

import com.river.module.affiliate.controller.admin.category.vo.CategoryRespVO;
import com.river.module.affiliate.controller.admin.category.vo.CategorySaveReqVO;
import com.river.module.affiliate.dal.dataobject.CategoryDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CategoryConvert {

    CategoryConvert INSTANCE = Mappers.getMapper(CategoryConvert.class);

    CategoryDO convert(CategorySaveReqVO bean);

    CategoryRespVO convert(CategoryDO bean);

    List<CategoryRespVO> convertList(List<CategoryDO> list);
}
