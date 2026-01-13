package com.river.module.campaign.convert;

import com.river.framework.common.pojo.PageResult;
import com.river.module.campaign.controller.admin.costrecord.vo.CostRecordRespVO;
import com.river.module.campaign.controller.admin.costrecord.vo.CostRecordSaveReqVO;
import com.river.module.campaign.dal.dataobject.CostRecordDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CostRecordConvert {

    CostRecordConvert INSTANCE = Mappers.getMapper(CostRecordConvert.class);

    CostRecordDO convert(CostRecordSaveReqVO bean);

    CostRecordRespVO convert(CostRecordDO bean);

    List<CostRecordRespVO> convertList(List<CostRecordDO> list);

    PageResult<CostRecordRespVO> convertPage(PageResult<CostRecordDO> page);
}
