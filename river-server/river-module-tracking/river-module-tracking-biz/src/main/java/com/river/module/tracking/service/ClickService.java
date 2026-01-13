package com.river.module.tracking.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.tracking.controller.admin.click.vo.ClickPageReqVO;
import com.river.module.tracking.dal.dataobject.ClickDO;

public interface ClickService {

    ClickDO getClick(String clickId);

    PageResult<ClickDO> getClickPage(ClickPageReqVO pageReqVO);

    String createClick(ClickDO click);

    void validateClickExists(String clickId);

}
