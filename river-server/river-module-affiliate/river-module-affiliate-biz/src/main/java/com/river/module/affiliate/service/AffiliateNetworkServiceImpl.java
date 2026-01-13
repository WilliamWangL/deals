package com.river.module.affiliate.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.affiliate.controller.admin.network.vo.AffiliateNetworkPageReqVO;
import com.river.module.affiliate.dal.dataobject.AffiliateNetworkDO;
import com.river.module.affiliate.dal.mysql.AffiliateNetworkMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.river.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.river.module.affiliate.enums.ErrorCodeConstants.*;

@Service
@Validated
public class AffiliateNetworkServiceImpl implements AffiliateNetworkService {

    @Resource
    private AffiliateNetworkMapper networkMapper;

    @Override
    public Long createNetwork(AffiliateNetworkDO network) {
        validateNetworkCodeUnique(null, network.getCode());
        networkMapper.insert(network);
        return network.getId();
    }

    @Override
    public void updateNetwork(AffiliateNetworkDO network) {
        validateNetworkExists(network.getId());
        validateNetworkCodeUnique(network.getId(), network.getCode());
        networkMapper.updateById(network);
    }

    @Override
    public void deleteNetwork(Long id) {
        validateNetworkExists(id);
        networkMapper.deleteById(id);
    }

    @Override
    public AffiliateNetworkDO getNetwork(Long id) {
        return networkMapper.selectById(id);
    }

    @Override
    public List<AffiliateNetworkDO> getNetworkList() {
        return networkMapper.selectList();
    }

    @Override
    public PageResult<AffiliateNetworkDO> getNetworkPage(AffiliateNetworkPageReqVO pageReqVO) {
        return networkMapper.selectPage(pageReqVO);
    }

    @Override
    public void validateNetworkExists(Long id) {
        if (networkMapper.selectById(id) == null) {
            throw exception(NETWORK_NOT_EXISTS);
        }
    }

    private void validateNetworkCodeUnique(Long id, String code) {
        AffiliateNetworkDO network = networkMapper.selectOne(
                AffiliateNetworkDO::getCode, code);
        if (network == null) {
            return;
        }
        if (id == null || !network.getId().equals(id)) {
            throw exception(NETWORK_CODE_DUPLICATE);
        }
    }

}
