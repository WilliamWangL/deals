package com.river.module.campaign.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.campaign.controller.admin.currency.vo.CurrencyPageReqVO;
import com.river.module.campaign.dal.dataobject.CurrencyDO;
import com.river.module.campaign.dal.mysql.CurrencyMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.river.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.river.module.campaign.enums.ErrorCodeConstants.*;

@Service
@Validated
public class CurrencyServiceImpl implements CurrencyService {

    @Resource
    private CurrencyMapper currencyMapper;

    @Override
    public Long createCurrency(CurrencyDO currency) {
        validateCodeUnique(null, currency.getCode());
        currencyMapper.insert(currency);
        return currency.getId();
    }

    @Override
    public void updateCurrency(CurrencyDO currency) {
        validateCurrencyExists(currency.getId());
        validateCodeUnique(currency.getId(), currency.getCode());
        currencyMapper.updateById(currency);
    }

    @Override
    public void deleteCurrency(Long id) {
        validateCurrencyExists(id);
        currencyMapper.deleteById(id);
    }

    @Override
    public CurrencyDO getCurrency(Long id) {
        return currencyMapper.selectById(id);
    }

    @Override
    public CurrencyDO getCurrencyByCode(String code) {
        return currencyMapper.selectByCode(code);
    }

    @Override
    public List<CurrencyDO> getCurrencyList() {
        return currencyMapper.selectList();
    }

    @Override
    public PageResult<CurrencyDO> getCurrencyPage(CurrencyPageReqVO pageReqVO) {
        return currencyMapper.selectPage(pageReqVO);
    }

    @Override
    public void validateCurrencyExists(Long id) {
        if (currencyMapper.selectById(id) == null) {
            throw exception(CURRENCY_NOT_EXISTS);
        }
    }

    private void validateCodeUnique(Long id, String code) {
        CurrencyDO existing = currencyMapper.selectByCode(code);
        if (existing != null && !existing.getId().equals(id)) {
            throw exception(CURRENCY_CODE_DUPLICATE);
        }
    }
}
