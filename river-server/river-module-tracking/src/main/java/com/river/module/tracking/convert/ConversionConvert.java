package com.river.module.tracking.convert;

import com.river.framework.common.pojo.PageResult;
import com.river.module.tracking.controller.admin.conversion.vo.ConversionRespVO;
import com.river.module.tracking.dal.dataobject.ConversionDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ConversionConvert {

    ConversionConvert INSTANCE = Mappers.getMapper(ConversionConvert.class);

    ConversionRespVO convert(ConversionDO bean);

    List<ConversionRespVO> convertList(List<ConversionDO> list);

    PageResult<ConversionRespVO> convertPage(PageResult<ConversionDO> page);

}
