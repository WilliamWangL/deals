package com.river.module.campaign.convert;

import com.river.framework.common.pojo.PageResult;
import com.river.module.campaign.controller.admin.landingpage.vo.LandingPageRespVO;
import com.river.module.campaign.controller.admin.landingpage.vo.LandingPageSaveReqVO;
import com.river.module.campaign.dal.dataobject.LandingPageDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface LandingPageConvert {

    LandingPageConvert INSTANCE = Mappers.getMapper(LandingPageConvert.class);

    LandingPageDO convert(LandingPageSaveReqVO bean);

    LandingPageRespVO convert(LandingPageDO bean);

    List<LandingPageRespVO> convertList(List<LandingPageDO> list);

    PageResult<LandingPageRespVO> convertPage(PageResult<LandingPageDO> page);
}
