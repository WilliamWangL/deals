package com.river.module.affiliate.convert;

import com.river.framework.common.pojo.PageResult;
import com.river.module.affiliate.controller.admin.network.vo.AffiliateNetworkRespVO;
import com.river.module.affiliate.controller.admin.network.vo.AffiliateNetworkSaveReqVO;
import com.river.module.affiliate.dal.dataobject.AffiliateNetworkDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AffiliateNetworkConvert {

    AffiliateNetworkConvert INSTANCE = Mappers.getMapper(AffiliateNetworkConvert.class);

    AffiliateNetworkDO convert(AffiliateNetworkSaveReqVO bean);

    AffiliateNetworkRespVO convert(AffiliateNetworkDO bean);

    List<AffiliateNetworkRespVO> convertList(List<AffiliateNetworkDO> list);

    PageResult<AffiliateNetworkRespVO> convertPage(PageResult<AffiliateNetworkDO> page);
}
