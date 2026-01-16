package com.river.module.tracking.dal.mysql;

import com.river.framework.common.pojo.PageResult;
import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.river.module.tracking.controller.admin.unattributed.vo.UnattributedConversionPageReqVO;
import com.river.module.tracking.dal.dataobject.UnattributedConversionDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UnattributedConversionMapper extends BaseMapperX<UnattributedConversionDO> {

    default PageResult<UnattributedConversionDO> selectPage(UnattributedConversionPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<UnattributedConversionDO>()
                .eqIfPresent(UnattributedConversionDO::getNetworkCode, reqVO.getNetworkCode())
                .likeIfPresent(UnattributedConversionDO::getExternalConversionId, reqVO.getExternalConversionId())
                .eqIfPresent(UnattributedConversionDO::getConversionType, reqVO.getConversionType())
                .betweenIfPresent(UnattributedConversionDO::getConversionTime, reqVO.getConversionTime())
                .orderByDesc(UnattributedConversionDO::getConversionTime));
    }

}
