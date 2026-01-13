package com.river.module.tracking.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.tracking.controller.admin.click.vo.ClickPageReqVO;
import com.river.module.tracking.dal.dataobject.ClickDO;
import com.river.module.tracking.dal.mysql.ClickMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.river.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.river.module.tracking.enums.ErrorCodeConstants.CLICK_NOT_EXISTS;

@Service
@Validated
public class ClickServiceImpl implements ClickService {

    @Resource
    private ClickMapper clickMapper;

    @Override
    public ClickDO getClick(String clickId) {
        return clickMapper.selectByClickId(clickId);
    }

    @Override
    public PageResult<ClickDO> getClickPage(ClickPageReqVO pageReqVO) {
        return clickMapper.selectPage(pageReqVO);
    }

    @Override
    public String createClick(ClickDO click) {
        clickMapper.insert(click);
        return click.getClickId();
    }

    @Override
    public void validateClickExists(String clickId) {
        if (clickMapper.selectByClickId(clickId) == null) {
            throw exception(CLICK_NOT_EXISTS);
        }
    }

}
