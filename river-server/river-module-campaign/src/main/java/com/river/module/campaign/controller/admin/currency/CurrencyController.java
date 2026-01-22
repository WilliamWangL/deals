package com.river.module.campaign.controller.admin.currency;

import com.river.framework.common.pojo.CommonResult;
import com.river.framework.common.pojo.PageResult;
import com.river.module.campaign.controller.admin.currency.vo.CurrencyPageReqVO;
import com.river.module.campaign.controller.admin.currency.vo.CurrencyRespVO;
import com.river.module.campaign.controller.admin.currency.vo.CurrencySaveReqVO;
import com.river.framework.common.util.object.BeanUtils;
import com.river.module.campaign.dal.dataobject.CurrencyDO;
import com.river.module.campaign.service.CurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.river.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 货币")
@RestController
@RequestMapping("/campaign/currency")
@Validated
public class CurrencyController {

    @Resource
    private CurrencyService currencyService;

    @PostMapping("/create")
    @Operation(summary = "创建货币")
    @PreAuthorize("@ss.hasPermission('campaign:currency:create')")
    public CommonResult<Long> createCurrency(@Valid @RequestBody CurrencySaveReqVO createReqVO) {
        return success(currencyService.createCurrency(BeanUtils.toBean(createReqVO, CurrencyDO.class)));
    }

    @PutMapping("/update")
    @Operation(summary = "更新货币")
    @PreAuthorize("@ss.hasPermission('campaign:currency:update')")
    public CommonResult<Boolean> updateCurrency(@Valid @RequestBody CurrencySaveReqVO updateReqVO) {
        currencyService.updateCurrency(BeanUtils.toBean(updateReqVO, CurrencyDO.class));
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除货币")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('campaign:currency:delete')")
    public CommonResult<Boolean> deleteCurrency(@RequestParam("id") Long id) {
        currencyService.deleteCurrency(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取货币")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('campaign:currency:query')")
    public CommonResult<CurrencyRespVO> getCurrency(@RequestParam("id") Long id) {
        CurrencyDO currency = currencyService.getCurrency(id);
        return success(BeanUtils.toBean(currency, CurrencyRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获取货币列表")
    @PreAuthorize("@ss.hasPermission('campaign:currency:query')")
    public CommonResult<List<CurrencyRespVO>> getCurrencyList() {
        List<CurrencyDO> list = currencyService.getCurrencyList();
        return success(BeanUtils.toBean(list, CurrencyRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获取货币分页")
    @PreAuthorize("@ss.hasPermission('campaign:currency:query')")
    public CommonResult<PageResult<CurrencyRespVO>> getCurrencyPage(@Valid CurrencyPageReqVO pageReqVO) {
        PageResult<CurrencyDO> pageResult = currencyService.getCurrencyPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, CurrencyRespVO.class));
    }
}
