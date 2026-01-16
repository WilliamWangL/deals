package com.river.module.campaign.convert;

import com.river.framework.common.pojo.PageResult;
import com.river.module.campaign.controller.admin.campaign.vo.CampaignRespVO;
import com.river.module.campaign.controller.admin.campaign.vo.CampaignSaveReqVO;
import com.river.module.campaign.dal.dataobject.CampaignDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CampaignConvert {

    CampaignConvert INSTANCE = Mappers.getMapper(CampaignConvert.class);

    CampaignDO convert(CampaignSaveReqVO bean);

    CampaignRespVO convert(CampaignDO bean);

    List<CampaignRespVO> convertList(List<CampaignDO> list);

    PageResult<CampaignRespVO> convertPage(PageResult<CampaignDO> page);
}
