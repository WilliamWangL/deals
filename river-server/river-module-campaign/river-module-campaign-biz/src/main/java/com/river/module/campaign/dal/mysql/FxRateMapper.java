package com.river.module.campaign.dal.mysql;

import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.river.module.campaign.dal.dataobject.FxRateDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;

@Mapper
public interface FxRateMapper extends BaseMapperX<FxRateDO> {

    default FxRateDO selectLatestRate(String fromCurrency, String toCurrency) {
        return selectOne(new LambdaQueryWrapperX<FxRateDO>()
                .eq(FxRateDO::getFromCurrency, fromCurrency)
                .eq(FxRateDO::getToCurrency, toCurrency)
                .le(FxRateDO::getEffectiveDate, LocalDate.now())
                .orderByDesc(FxRateDO::getEffectiveDate)
                .last("LIMIT 1"));
    }
}
