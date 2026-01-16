package com.river.module.coupon.convert;

import com.river.framework.common.pojo.PageResult;
import com.river.module.coupon.controller.admin.deal.vo.DealRespVO;
import com.river.module.coupon.controller.admin.deal.vo.DealSaveReqVO;
import com.river.module.coupon.dal.dataobject.DealDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DealConvert {

    DealConvert INSTANCE = Mappers.getMapper(DealConvert.class);

    DealDO convert(DealSaveReqVO bean);

    DealRespVO convert(DealDO bean);

    List<DealRespVO> convertList(List<DealDO> list);

    PageResult<DealRespVO> convertPage(PageResult<DealDO> page);
}
