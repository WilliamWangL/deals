package com.river.module.affiliate.dal.mysql;

import com.river.framework.common.pojo.PageResult;
import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.river.module.affiliate.controller.admin.network.vo.AffiliateNetworkPageReqVO;
import com.river.module.affiliate.dal.dataobject.AffiliateNetworkDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AffiliateNetworkMapper extends BaseMapperX<AffiliateNetworkDO> {

    default PageResult<AffiliateNetworkDO> selectPage(AffiliateNetworkPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AffiliateNetworkDO>()
                .likeIfPresent(AffiliateNetworkDO::getCode, reqVO.getCode())
                .likeIfPresent(AffiliateNetworkDO::getName, reqVO.getName())
                .eqIfPresent(AffiliateNetworkDO::getStatus, reqVO.getStatus())
                .orderByDesc(AffiliateNetworkDO::getId));
    }

}
