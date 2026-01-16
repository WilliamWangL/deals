package com.river.module.affiliate.controller.admin.network;

import com.river.framework.common.pojo.CommonResult;
import com.river.framework.common.pojo.PageResult;
import com.river.module.affiliate.controller.admin.network.vo.AffiliateNetworkPageReqVO;
import com.river.module.affiliate.controller.admin.network.vo.AffiliateNetworkRespVO;
import com.river.module.affiliate.controller.admin.network.vo.AffiliateNetworkSaveReqVO;
import com.river.module.affiliate.convert.AffiliateNetworkConvert;
import com.river.module.affiliate.dal.dataobject.AffiliateNetworkDO;
import com.river.module.affiliate.dal.dataobject.NetworkCredentialDO;
import com.river.module.affiliate.dal.mysql.NetworkCredentialMapper;
import com.river.module.affiliate.service.AffiliateNetworkService;
import com.river.module.affiliate.service.network.admitad.AdmitadSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.river.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 联盟网络")
@RestController
@RequestMapping("/affiliate/network")
@Validated
@Slf4j
public class AffiliateNetworkController {

    @Resource
    private AffiliateNetworkService networkService;

    @Resource
    private AdmitadSyncService admitadSyncService;

    @Resource
    private NetworkCredentialMapper credentialMapper;

    @PostMapping("/create")
    @Operation(summary = "创建联盟网络")
    @PreAuthorize("@ss.hasPermission('affiliate:network:create')")
    public CommonResult<Long> createNetwork(@Valid @RequestBody AffiliateNetworkSaveReqVO createReqVO) {
        return success(networkService.createNetwork(AffiliateNetworkConvert.INSTANCE.convert(createReqVO)));
    }

    @PutMapping("/update")
    @Operation(summary = "更新联盟网络")
    @PreAuthorize("@ss.hasPermission('affiliate:network:update')")
    public CommonResult<Boolean> updateNetwork(@Valid @RequestBody AffiliateNetworkSaveReqVO updateReqVO) {
        networkService.updateNetwork(AffiliateNetworkConvert.INSTANCE.convert(updateReqVO));
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除联盟网络")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('affiliate:network:delete')")
    public CommonResult<Boolean> deleteNetwork(@RequestParam("id") Long id) {
        networkService.deleteNetwork(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取联盟网络")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('affiliate:network:query')")
    public CommonResult<AffiliateNetworkRespVO> getNetwork(@RequestParam("id") Long id) {
        AffiliateNetworkDO network = networkService.getNetwork(id);
        return success(AffiliateNetworkConvert.INSTANCE.convert(network));
    }

    @GetMapping("/list")
    @Operation(summary = "获取联盟网络列表")
    @PreAuthorize("@ss.hasPermission('affiliate:network:query')")
    public CommonResult<List<AffiliateNetworkRespVO>> getNetworkList() {
        List<AffiliateNetworkDO> list = networkService.getNetworkList();
        return success(AffiliateNetworkConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/page")
    @Operation(summary = "获取联盟网络分页")
    @PreAuthorize("@ss.hasPermission('affiliate:network:query')")
    public CommonResult<PageResult<AffiliateNetworkRespVO>> getNetworkPage(@Valid AffiliateNetworkPageReqVO pageReqVO) {
        PageResult<AffiliateNetworkDO> pageResult = networkService.getNetworkPage(pageReqVO);
        return success(AffiliateNetworkConvert.INSTANCE.convertPage(pageResult));
    }

    @PostMapping("/sync")
    @Operation(summary = "同步商家和Offer数据")
    @Parameter(name = "code", description = "联盟编码", required = true, example = "admitad")
    @PreAuthorize("@ss.hasPermission('affiliate:network:update')")
    public CommonResult<String> syncNetwork(@RequestParam("code") String code) {
        log.info("[syncNetwork] Starting sync for network: {}", code);

        List<NetworkCredentialDO> credentials = credentialMapper.selectEnabledByNetworkCode(code);
        if (credentials.isEmpty()) {
            return success("No enabled credentials found for network: " + code);
        }

        int successCount = 0;
        for (NetworkCredentialDO credential : credentials) {
            try {
                if ("admitad".equalsIgnoreCase(code)) {
                    admitadSyncService.syncCampaigns(credential);
                    successCount++;
                }
            } catch (Exception e) {
                log.error("[syncNetwork] Failed to sync for credential id={}: {}", credential.getId(), e.getMessage());
            }
        }

        String result = String.format("Synced %d/%d credentials for %s", successCount, credentials.size(), code);
        log.info("[syncNetwork] {}", result);
        return success(result);
    }

    @PostMapping("/sync-coupons")
    @Operation(summary = "同步优惠券和Deal数据")
    @Parameter(name = "code", description = "联盟编码", required = true, example = "admitad")
    @PreAuthorize("@ss.hasPermission('affiliate:network:update')")
    public CommonResult<String> syncCoupons(@RequestParam("code") String code) {
        log.info("[syncCoupons] Starting coupon sync for network: {}", code);

        List<NetworkCredentialDO> credentials = credentialMapper.selectEnabledByNetworkCode(code);
        if (credentials.isEmpty()) {
            return success("No enabled credentials found for network: " + code);
        }

        int successCount = 0;
        for (NetworkCredentialDO credential : credentials) {
            try {
                if ("admitad".equalsIgnoreCase(code)) {
                    admitadSyncService.syncCoupons(credential);
                    successCount++;
                }
            } catch (Exception e) {
                log.error("[syncCoupons] Failed to sync coupons for credential id={}: {}",
                    credential.getId(), e.getMessage());
            }
        }

        String result = String.format("Synced coupons for %d/%d credentials for %s",
            successCount, credentials.size(), code);
        log.info("[syncCoupons] {}", result);
        return success(result);
    }

    @PostMapping("/sync-all")
    @Operation(summary = "全量同步（商家、Offer、优惠券、Deal）")
    @Parameter(name = "code", description = "联盟编码", required = true, example = "admitad")
    @PreAuthorize("@ss.hasPermission('affiliate:network:update')")
    public CommonResult<String> syncAll(@RequestParam("code") String code) {
        log.info("[syncAll] Starting full sync for network: {}", code);

        List<NetworkCredentialDO> credentials = credentialMapper.selectEnabledByNetworkCode(code);
        if (credentials.isEmpty()) {
            return success("No enabled credentials found for network: " + code);
        }

        int merchantSuccess = 0;
        int couponSuccess = 0;

        for (NetworkCredentialDO credential : credentials) {
            try {
                if ("admitad".equalsIgnoreCase(code)) {
                    // 先同步商家和Offer
                    admitadSyncService.syncCampaigns(credential);
                    merchantSuccess++;

                    // 再同步优惠券和Deal
                    admitadSyncService.syncCoupons(credential);
                    couponSuccess++;
                }
            } catch (Exception e) {
                log.error("[syncAll] Failed for credential id={}: {}", credential.getId(), e.getMessage());
            }
        }

        String result = String.format("Full sync completed: merchants %d/%d, coupons %d/%d for %s",
            merchantSuccess, credentials.size(), couponSuccess, credentials.size(), code);
        log.info("[syncAll] {}", result);
        return success(result);
    }
}
