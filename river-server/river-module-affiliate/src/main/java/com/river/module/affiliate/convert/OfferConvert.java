package com.river.module.affiliate.convert;

import com.river.framework.common.pojo.PageResult;
import com.river.module.affiliate.controller.admin.offer.vo.OfferRespVO;
import com.river.module.affiliate.controller.admin.offer.vo.OfferSaveReqVO;
import com.river.module.affiliate.dal.dataobject.OfferDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface OfferConvert {

    OfferConvert INSTANCE = Mappers.getMapper(OfferConvert.class);

    OfferDO convert(OfferSaveReqVO bean);

    OfferRespVO convert(OfferDO bean);

    List<OfferRespVO> convertList(List<OfferDO> list);

    PageResult<OfferRespVO> convertPage(PageResult<OfferDO> page);
}
