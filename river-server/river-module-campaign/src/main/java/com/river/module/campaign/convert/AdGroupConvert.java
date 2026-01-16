package com.river.module.campaign.convert;

import com.river.framework.common.pojo.PageResult;
import com.river.module.campaign.controller.admin.adgroup.vo.AdGroupRespVO;
import com.river.module.campaign.controller.admin.adgroup.vo.AdGroupSaveReqVO;
import com.river.module.campaign.dal.dataobject.AdGroupDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AdGroupConvert {

    AdGroupConvert INSTANCE = Mappers.getMapper(AdGroupConvert.class);

    AdGroupDO convert(AdGroupSaveReqVO bean);

    AdGroupRespVO convert(AdGroupDO bean);

    List<AdGroupRespVO> convertList(List<AdGroupDO> list);

    PageResult<AdGroupRespVO> convertPage(PageResult<AdGroupDO> page);
}
