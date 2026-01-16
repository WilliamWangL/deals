package com.river.module.coupon.convert;

import com.river.framework.common.pojo.PageResult;
import com.river.module.coupon.controller.admin.coupon.vo.CouponRespVO;
import com.river.module.coupon.controller.admin.coupon.vo.CouponSaveReqVO;
import com.river.module.coupon.dal.dataobject.CouponDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CouponConvert {

    CouponConvert INSTANCE = Mappers.getMapper(CouponConvert.class);

    CouponDO convert(CouponSaveReqVO bean);

    CouponRespVO convert(CouponDO bean);

    List<CouponRespVO> convertList(List<CouponDO> list);

    PageResult<CouponRespVO> convertPage(PageResult<CouponDO> page);
}
