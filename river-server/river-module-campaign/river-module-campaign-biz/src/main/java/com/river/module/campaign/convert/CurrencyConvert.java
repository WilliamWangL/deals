package com.river.module.campaign.convert;

import com.river.framework.common.pojo.PageResult;
import com.river.module.campaign.controller.admin.currency.vo.CurrencyRespVO;
import com.river.module.campaign.controller.admin.currency.vo.CurrencySaveReqVO;
import com.river.module.campaign.dal.dataobject.CurrencyDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CurrencyConvert {

    CurrencyConvert INSTANCE = Mappers.getMapper(CurrencyConvert.class);

    CurrencyDO convert(CurrencySaveReqVO bean);

    CurrencyRespVO convert(CurrencyDO bean);

    List<CurrencyRespVO> convertList(List<CurrencyDO> list);

    PageResult<CurrencyRespVO> convertPage(PageResult<CurrencyDO> page);
}
