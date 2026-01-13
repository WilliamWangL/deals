package com.river.module.tracking.convert;

import com.river.framework.common.pojo.PageResult;
import com.river.module.tracking.controller.admin.trackinglink.vo.TrackingLinkRespVO;
import com.river.module.tracking.controller.admin.trackinglink.vo.TrackingLinkSaveReqVO;
import com.river.module.tracking.dal.dataobject.TrackingLinkDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface TrackingLinkConvert {

    TrackingLinkConvert INSTANCE = Mappers.getMapper(TrackingLinkConvert.class);

    TrackingLinkDO convert(TrackingLinkSaveReqVO bean);

    TrackingLinkRespVO convert(TrackingLinkDO bean);

    List<TrackingLinkRespVO> convertList(List<TrackingLinkDO> list);

    PageResult<TrackingLinkRespVO> convertPage(PageResult<TrackingLinkDO> page);

}
