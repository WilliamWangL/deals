package com.river.module.affiliate.convert;

import com.river.framework.common.pojo.PageResult;
import com.river.module.affiliate.controller.admin.merchant.vo.MerchantRespVO;
import com.river.module.affiliate.controller.admin.merchant.vo.MerchantSaveReqVO;
import com.river.module.affiliate.dal.dataobject.MerchantDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface MerchantConvert {

    MerchantConvert INSTANCE = Mappers.getMapper(MerchantConvert.class);

    MerchantDO convert(MerchantSaveReqVO bean);

    MerchantRespVO convert(MerchantDO bean);

    List<MerchantRespVO> convertList(List<MerchantDO> list);

    PageResult<MerchantRespVO> convertPage(PageResult<MerchantDO> page);
}
