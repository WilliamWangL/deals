package com.river.module.tracking.dal.mysql;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.river.framework.common.pojo.PageResult;
import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.river.module.tracking.controller.admin.conversion.vo.ConversionPageReqVO;
import com.river.module.tracking.dal.dataobject.ConversionDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface ConversionMapper extends BaseMapperX<ConversionDO> {

    default PageResult<ConversionDO> selectPage(ConversionPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ConversionDO>()
                .likeIfPresent(ConversionDO::getClickId, reqVO.getClickId())
                .eqIfPresent(ConversionDO::getNetworkCode, reqVO.getNetworkCode())
                .eqIfPresent(ConversionDO::getConversionType, reqVO.getConversionType())
                .eqIfPresent(ConversionDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ConversionDO::getConversionTime, reqVO.getConversionTime())
                .orderByDesc(ConversionDO::getId));
    }

    default ConversionDO selectByNetworkAndExternalId(String networkCode, String externalConversionId, Long tenantId) {
        return selectOne(new LambdaQueryWrapperX<ConversionDO>()
                .eq(ConversionDO::getNetworkCode, networkCode)
                .eq(ConversionDO::getExternalConversionId, externalConversionId));
    }

    /**
     * 按 ClickId 聚合指定日期的转化数据
     * 返回 clickId -> (conversions, revenue)
     */
    default List<Map<String, Object>> selectConversionsGroupByClickId(LocalDate date) {
        QueryWrapper<ConversionDO> wrapper = new QueryWrapper<>();
        wrapper.select("click_id as clickId",
                       "COUNT(*) as conversions",
                       "COALESCE(SUM(commission), 0) as revenue")
                .apply("DATE(conversion_time) = {0}", date)
                .isNotNull("click_id")
                .groupBy("click_id");
        return selectMaps(wrapper);
    }

}
