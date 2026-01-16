package com.river.module.tracking.convert;

import com.river.framework.common.pojo.PageResult;
import com.river.module.tracking.controller.admin.attribution.vo.AttributionPageReqVO;
import com.river.module.tracking.controller.admin.attribution.vo.AttributionRespVO;
import com.river.module.tracking.dal.dataobject.AttributionDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AttributionConvert {

    AttributionConvert INSTANCE = Mappers.getMapper(AttributionConvert.class);

    PageResult<AttributionRespVO> convert(PageResult<AttributionDO> pageResult);

    List<AttributionRespVO> convertList(List<AttributionDO> list);

    AttributionRespVO convert(AttributionDO bean);

}
