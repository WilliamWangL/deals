package com.river.module.tracking.convert;

import com.river.framework.common.pojo.PageResult;
import com.river.module.tracking.controller.admin.click.vo.ClickRespVO;
import com.river.module.tracking.dal.dataobject.ClickDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ClickConvert {

    ClickConvert INSTANCE = Mappers.getMapper(ClickConvert.class);

    ClickRespVO convert(ClickDO bean);

    List<ClickRespVO> convertList(List<ClickDO> list);

    PageResult<ClickRespVO> convertPage(PageResult<ClickDO> page);

}
