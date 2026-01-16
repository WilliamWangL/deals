package com.river.module.affiliate.controller.admin.merchant;

import com.river.framework.common.pojo.CommonResult;
import com.river.framework.common.pojo.PageResult;
import com.river.module.affiliate.controller.admin.merchant.vo.MerchantPageReqVO;
import com.river.module.affiliate.controller.admin.merchant.vo.MerchantRespVO;
import com.river.module.affiliate.controller.admin.merchant.vo.MerchantSaveReqVO;
import com.river.module.affiliate.convert.MerchantConvert;
import com.river.module.affiliate.dal.dataobject.MerchantDO;
import com.river.module.affiliate.service.MerchantService;
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

@Tag(name = "管理后台 - 商家")
@RestController
@RequestMapping("/affiliate/merchant")
@Validated
public class MerchantController {

    @Resource
    private MerchantService merchantService;

    @PostMapping("/create")
    @Operation(summary = "创建商家")
    @PreAuthorize("@ss.hasPermission('affiliate:merchant:create')")
    public CommonResult<Long> createMerchant(@Valid @RequestBody MerchantSaveReqVO createReqVO) {
        return success(merchantService.createMerchant(MerchantConvert.INSTANCE.convert(createReqVO)));
    }

    @PutMapping("/update")
    @Operation(summary = "更新商家")
    @PreAuthorize("@ss.hasPermission('affiliate:merchant:update')")
    public CommonResult<Boolean> updateMerchant(@Valid @RequestBody MerchantSaveReqVO updateReqVO) {
        merchantService.updateMerchant(MerchantConvert.INSTANCE.convert(updateReqVO));
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除商家")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('affiliate:merchant:delete')")
    public CommonResult<Boolean> deleteMerchant(@RequestParam("id") Long id) {
        merchantService.deleteMerchant(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取商家")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('affiliate:merchant:query')")
    public CommonResult<MerchantRespVO> getMerchant(@RequestParam("id") Long id) {
        MerchantDO merchant = merchantService.getMerchant(id);
        return success(MerchantConvert.INSTANCE.convert(merchant));
    }

    @GetMapping("/list")
    @Operation(summary = "获取商家列表")
    @PreAuthorize("@ss.hasPermission('affiliate:merchant:query')")
    public CommonResult<List<MerchantRespVO>> getMerchantList() {
        List<MerchantDO> list = merchantService.getMerchantList();
        return success(MerchantConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/page")
    @Operation(summary = "获取商家分页")
    @PreAuthorize("@ss.hasPermission('affiliate:merchant:query')")
    public CommonResult<PageResult<MerchantRespVO>> getMerchantPage(@Valid MerchantPageReqVO pageReqVO) {
        PageResult<MerchantDO> pageResult = merchantService.getMerchantPage(pageReqVO);
        return success(MerchantConvert.INSTANCE.convertPage(pageResult));
    }
}
