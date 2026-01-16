package com.river.module.affiliate.controller.admin.offer;

import com.river.framework.common.pojo.CommonResult;
import com.river.framework.common.pojo.PageResult;
import com.river.module.affiliate.controller.admin.offer.vo.OfferPageReqVO;
import com.river.module.affiliate.controller.admin.offer.vo.OfferRespVO;
import com.river.module.affiliate.controller.admin.offer.vo.OfferSaveReqVO;
import com.river.module.affiliate.convert.OfferConvert;
import com.river.module.affiliate.dal.dataobject.OfferDO;
import com.river.module.affiliate.service.OfferService;
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

@Tag(name = "管理后台 - Offer")
@RestController
@RequestMapping("/affiliate/offer")
@Validated
public class OfferController {

    @Resource
    private OfferService offerService;

    @PostMapping("/create")
    @Operation(summary = "创建 Offer")
    @PreAuthorize("@ss.hasPermission('affiliate:offer:create')")
    public CommonResult<Long> createOffer(@Valid @RequestBody OfferSaveReqVO createReqVO) {
        return success(offerService.createOffer(OfferConvert.INSTANCE.convert(createReqVO)));
    }

    @PutMapping("/update")
    @Operation(summary = "更新 Offer")
    @PreAuthorize("@ss.hasPermission('affiliate:offer:update')")
    public CommonResult<Boolean> updateOffer(@Valid @RequestBody OfferSaveReqVO updateReqVO) {
        offerService.updateOffer(OfferConvert.INSTANCE.convert(updateReqVO));
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除 Offer")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('affiliate:offer:delete')")
    public CommonResult<Boolean> deleteOffer(@RequestParam("id") Long id) {
        offerService.deleteOffer(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取 Offer")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('affiliate:offer:query')")
    public CommonResult<OfferRespVO> getOffer(@RequestParam("id") Long id) {
        OfferDO offer = offerService.getOffer(id);
        return success(OfferConvert.INSTANCE.convert(offer));
    }

    @GetMapping("/list")
    @Operation(summary = "获取 Offer 列表")
    @PreAuthorize("@ss.hasPermission('affiliate:offer:query')")
    public CommonResult<List<OfferRespVO>> getOfferList() {
        List<OfferDO> list = offerService.getOfferList();
        return success(OfferConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/page")
    @Operation(summary = "获取 Offer 分页")
    @PreAuthorize("@ss.hasPermission('affiliate:offer:query')")
    public CommonResult<PageResult<OfferRespVO>> getOfferPage(@Valid OfferPageReqVO pageReqVO) {
        PageResult<OfferDO> pageResult = offerService.getOfferPage(pageReqVO);
        return success(OfferConvert.INSTANCE.convertPage(pageResult));
    }
}
