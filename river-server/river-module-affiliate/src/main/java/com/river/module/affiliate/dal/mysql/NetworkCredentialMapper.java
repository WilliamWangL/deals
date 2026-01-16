package com.river.module.affiliate.dal.mysql;

import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.module.affiliate.dal.dataobject.NetworkCredentialDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface NetworkCredentialMapper extends BaseMapperX<NetworkCredentialDO> {

    @Select("""
        SELECT c.* FROM river_affiliate_network_credential c
        INNER JOIN river_affiliate_network n ON c.network_id = n.id AND n.deleted = 0
        WHERE n.code = #{networkCode} AND c.enabled = true AND c.deleted = 0
        """)
    List<NetworkCredentialDO> selectEnabledByNetworkCode(@Param("networkCode") String networkCode);

}
