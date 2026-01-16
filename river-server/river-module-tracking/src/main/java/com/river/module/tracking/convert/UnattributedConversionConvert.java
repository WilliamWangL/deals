package com.river.module.tracking.convert;

import com.river.framework.common.pojo.PageResult;
import com.river.module.tracking.controller.admin.unattributed.vo.UnattributedConversionPageReqVO;
import com.river.module.tracking.controller.admin.unattributed.vo.UnattributedConversionRespVO;
import com.river.module.tracking.dal.dataobject.UnattributedConversionDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UnattributedConversionConvert {

    UnattributedConversionConvert INSTANCE = Mappers.getMapper(UnattributedConversionConvert.class);

    PageResult<UnattributedConversionRespVO> convert(PageResult<UnattributedConversionDO> pageResult);

    List<UnattributedConversionRespVO> convertList(List<UnattributedConversionDO> list);

    UnattributedConversionRespVO convert(UnattributedConversionDO bean);

}
