package com.river.module.campaign.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.campaign.controller.admin.currency.vo.CurrencyPageReqVO;
import com.river.module.campaign.dal.dataobject.CurrencyDO;
import jakarta.validation.Valid;

import java.util.List;

public interface CurrencyService {

    Long createCurrency(@Valid CurrencyDO currency);

    void updateCurrency(@Valid CurrencyDO currency);

    void deleteCurrency(Long id);

    CurrencyDO getCurrency(Long id);

    CurrencyDO getCurrencyByCode(String code);

    List<CurrencyDO> getCurrencyList();

    PageResult<CurrencyDO> getCurrencyPage(CurrencyPageReqVO pageReqVO);

    void validateCurrencyExists(Long id);
}
