package com.river.module.campaign.dal.mysql;

import com.river.framework.common.pojo.PageResult;
import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.river.module.campaign.controller.admin.currency.vo.CurrencyPageReqVO;
import com.river.module.campaign.dal.dataobject.CurrencyDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CurrencyMapper extends BaseMapperX<CurrencyDO> {

    default PageResult<CurrencyDO> selectPage(CurrencyPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CurrencyDO>()
                .likeIfPresent(CurrencyDO::getName, reqVO.getName())
                .likeIfPresent(CurrencyDO::getCode, reqVO.getCode())
                .eqIfPresent(CurrencyDO::getStatus, reqVO.getStatus())
                .orderByDesc(CurrencyDO::getId));
    }

    default CurrencyDO selectByCode(String code) {
        return selectOne(CurrencyDO::getCode, code);
    }
}
