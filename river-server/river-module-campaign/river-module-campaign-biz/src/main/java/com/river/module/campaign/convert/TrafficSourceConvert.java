package com.river.module.campaign.convert;

import com.river.framework.common.pojo.PageResult;
import com.river.module.campaign.controller.admin.trafficsource.vo.TrafficSourceRespVO;
import com.river.module.campaign.controller.admin.trafficsource.vo.TrafficSourceSaveReqVO;
import com.river.module.campaign.dal.dataobject.TrafficSourceDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface TrafficSourceConvert {

    TrafficSourceConvert INSTANCE = Mappers.getMapper(TrafficSourceConvert.class);

    TrafficSourceDO convert(TrafficSourceSaveReqVO bean);

    TrafficSourceRespVO convert(TrafficSourceDO bean);

    List<TrafficSourceRespVO> convertList(List<TrafficSourceDO> list);

    PageResult<TrafficSourceRespVO> convertPage(PageResult<TrafficSourceDO> page);
}
