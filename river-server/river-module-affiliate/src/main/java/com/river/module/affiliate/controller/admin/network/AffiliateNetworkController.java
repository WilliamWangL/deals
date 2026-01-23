package com.river.module.affiliate.controller.admin.network;

import com.river.framework.common.pojo.CommonResult;
import com.river.framework.common.pojo.PageResult;
import com.river.module.affiliate.controller.admin.network.vo.AffiliateNetworkPageReqVO;
import com.river.module.affiliate.controller.admin.network.vo.AffiliateNetworkRespVO;
import com.river.module.affiliate.controller.admin.network.vo.AffiliateNetworkSaveReqVO;
import com.river.module.affiliate.dal.dataobject.AffiliateNetworkDO;
import com.river.framework.common.util.object.BeanUtils;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.river.framework.common.pojo.CommonResult.success;
import cn.hutool.core.util.StrUtil;

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
        return success(networkService.createNetwork(BeanUtils.toBean(createReqVO, AffiliateNetworkDO.class)));
    }

    @PutMapping("/update")
    @Operation(summary = "更新联盟网络")
    @PreAuthorize("@ss.hasPermission('affiliate:network:update')")
    public CommonResult<Boolean> updateNetwork(@Valid @RequestBody AffiliateNetworkSaveReqVO updateReqVO) {
        networkService.updateNetwork(BeanUtils.toBean(updateReqVO, AffiliateNetworkDO.class));
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
        return success(BeanUtils.toBean(network, AffiliateNetworkRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获取联盟网络列表")
    @PreAuthorize("@ss.hasPermission('affiliate:network:query')")
    public CommonResult<List<AffiliateNetworkRespVO>> getNetworkList() {
        List<AffiliateNetworkDO> list = networkService.getNetworkList();
        return success(BeanUtils.toBean(list, AffiliateNetworkRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获取联盟网络分页")
    @PreAuthorize("@ss.hasPermission('affiliate:network:query')")
    public CommonResult<PageResult<AffiliateNetworkRespVO>> getNetworkPage(@Valid AffiliateNetworkPageReqVO pageReqVO) {
        PageResult<AffiliateNetworkDO> pageResult = networkService.getNetworkPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AffiliateNetworkRespVO.class));
    }

    @PostMapping("/sync")
    @Operation(summary = "同步商家和Offer数据")
    @Parameter(name = "code", description = "联盟编码", required = true, example = "admitad")
    @PreAuthorize("@ss.hasPermission('affiliate:network:update')")
    public CommonResult<SyncResult> syncNetwork(@RequestParam("code") String code) {
        log.info("[syncNetwork] Starting sync for network: {}", code);

        try {
            List<NetworkCredentialDO> credentials = credentialMapper.selectEnabledByNetworkCode(code);
            if (credentials.isEmpty()) {
                return success(SyncResult.error("No enabled credentials found for network: " + code));
            }

            int successCount = 0;
            int failedCount = 0;
            for (NetworkCredentialDO credential : credentials) {
                try {
                    if ("admitad".equalsIgnoreCase(code)) {
                        admitadSyncService.syncCampaigns(credential);
                        successCount++;
                    }
                } catch (Exception e) {
                    log.error("[syncNetwork] Failed to sync for credential id={}: {}", credential.getId(), e.getMessage());
                    failedCount++;
                }
            }

            Map<String, Object> stats = new HashMap<>();
            stats.put("merchants", successCount);
            stats.put("failed", failedCount);
            stats.put("total", credentials.size());

            String message = String.format("Sync completed: %d/%d credentials synced for %s",
                successCount, credentials.size(), code);
            log.info("[syncNetwork] {}", message);

            return success(SyncResult.success(message, stats));
        } catch (Exception e) {
            log.error("[syncNetwork] Sync failed for network: {}", code, e);
            return success(SyncResult.error("Sync failed: " + e.getMessage()));
        }
    }

    @PostMapping("/sync-coupons")
    @Operation(summary = "同步优惠券和Deal数据")
    @Parameter(name = "code", description = "联盟编码", required = true, example = "admitad")
    @PreAuthorize("@ss.hasPermission('affiliate:network:update')")
    public CommonResult<SyncResult> syncCoupons(@RequestParam("code") String code) {
        log.info("[syncCoupons] Starting coupon sync for network: {}", code);

        try {
            List<NetworkCredentialDO> credentials = credentialMapper.selectEnabledByNetworkCode(code);
            if (credentials.isEmpty()) {
                return success(SyncResult.error("No enabled credentials found for network: " + code));
            }

            int successCount = 0;
            int failedCount = 0;
            for (NetworkCredentialDO credential : credentials) {
                try {
                    if ("admitad".equalsIgnoreCase(code)) {
                        admitadSyncService.syncCoupons(credential);
                        successCount++;
                    }
                } catch (Exception e) {
                    log.error("[syncCoupons] Failed to sync coupons for credential id={}: {}",
                        credential.getId(), e.getMessage());
                    failedCount++;
                }
            }

            Map<String, Object> stats = new HashMap<>();
            stats.put("coupons", successCount);
            stats.put("failed", failedCount);
            stats.put("total", credentials.size());

            String message = String.format("Coupon sync completed: %d/%d credentials synced for %s",
                successCount, credentials.size(), code);
            log.info("[syncCoupons] {}", message);

            return success(SyncResult.success(message, stats));
        } catch (Exception e) {
            log.error("[syncCoupons] Coupon sync failed for network: {}", code, e);
            return success(SyncResult.error("Coupon sync failed: " + e.getMessage()));
        }
    }

    @PostMapping("/sync-all")
    @Operation(summary = "全量同步（商家、Offer、优惠券、Deal）")
    @Parameter(name = "code", description = "联盟编码", required = true, example = "admitad")
    @PreAuthorize("@ss.hasPermission('affiliate:network:update')")
    public CommonResult<SyncResult> syncAll(@RequestParam("code") String code) {
        log.info("[syncAll] Starting full sync for network: {}", code);

        try {
            List<NetworkCredentialDO> credentials = credentialMapper.selectEnabledByNetworkCode(code);
            if (credentials.isEmpty()) {
                return success(SyncResult.error("No enabled credentials found for network: " + code));
            }

            int merchantSuccess = 0;
            int couponSuccess = 0;
            int failedCount = 0;

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
                    failedCount++;
                }
            }

            Map<String, Object> stats = new HashMap<>();
            stats.put("merchants", merchantSuccess);
            stats.put("coupons", couponSuccess);
            stats.put("failed", failedCount);
            stats.put("total", credentials.size());

            String message = String.format("Full sync completed: merchants %d/%d, coupons %d/%d for %s",
                merchantSuccess, credentials.size(), couponSuccess, credentials.size(), code);
            log.info("[syncAll] {}", message);

            return success(SyncResult.success(message, stats));
        } catch (Exception e) {
            log.error("[syncAll] Full sync failed for network: {}", code, e);
            return success(SyncResult.error("Full sync failed: " + e.getMessage()));
        }
    }

    @PostMapping("/sync-deals")
    @Operation(summary = "同步 Deal 数据")
    @Parameter(name = "networkId", description = "联盟网络ID", required = false)
    @Parameter(name = "code", description = "联盟编码", required = false)
    @PreAuthorize("@ss.hasPermission('affiliate:network:update')")
    public CommonResult<SyncResult> syncDeals(
            @RequestParam(required = false) String networkId,
            @RequestParam(required = false) String code) {
        log.info("[syncDeals] Request received - networkId: {}, code: {}", networkId, code);

        // 参数校验：至少一个不为空
        if (StrUtil.isAllEmpty(networkId, code)) {
            return success(SyncResult.error("At least one of networkId or code is required"));
        }

        String finalCode = code;

        // networkId 优先：解析出 code
        if (StrUtil.isNotEmpty(networkId)) {
            try {
                Long id = Long.parseLong(networkId);
                AffiliateNetworkDO network = networkService.getNetwork(id);
                if (network == null) {
                    log.warn("[syncDeals] Network not found - networkId: {}", networkId);
                    return success(SyncResult.error("Network not found: " + networkId));
                }
                finalCode = network.getCode();
            } catch (NumberFormatException e) {
                log.warn("[syncDeals] Invalid networkId format: {}", networkId);
                return success(SyncResult.error("Invalid networkId format"));
            }
        }

        // 调用同步逻辑
        SyncResult result = admitadSyncService.syncDeals(finalCode);
        return success(result);
    }

    @PostMapping("/sync-coupons-only")
    @Operation(summary = "同步 Coupon 数据")
    @Parameter(name = "networkId", description = "联盟网络ID", required = false)
    @Parameter(name = "code", description = "联盟编码", required = false)
    @PreAuthorize("@ss.hasPermission('affiliate:network:update')")
    public CommonResult<SyncResult> syncCouponsOnly(
            @RequestParam(required = false) String networkId,
            @RequestParam(required = false) String code) {
        log.info("[syncCouponsOnly] Request received - networkId: {}, code: {}", networkId, code);

        // 参数校验：至少一个不为空
        if (StrUtil.isAllEmpty(networkId, code)) {
            return success(SyncResult.error("At least one of networkId or code is required"));
        }

        String finalCode = code;

        // networkId 优先：解析出 code
        if (StrUtil.isNotEmpty(networkId)) {
            try {
                Long id = Long.parseLong(networkId);
                AffiliateNetworkDO network = networkService.getNetwork(id);
                if (network == null) {
                    log.warn("[syncCouponsOnly] Network not found - networkId: {}", networkId);
                    return success(SyncResult.error("Network not found: " + networkId));
                }
                finalCode = network.getCode();
            } catch (NumberFormatException e) {
                log.warn("[syncCouponsOnly] Invalid networkId format: {}", networkId);
                return success(SyncResult.error("Invalid networkId format"));
            }
        }

        // 调用同步逻辑
        SyncResult result = admitadSyncService.syncCouponsOnly(finalCode);
        return success(result);
    }

    /**
     * 统一同步结果响应 DTO
     */
    public static class SyncResult {
        private boolean success;
        private String message;
        private Map<String, Object> data;

        public static SyncResult success(String message, Map<String, Object> data) {
            SyncResult result = new SyncResult();
            result.success = true;
            result.message = message;
            result.data = data;
            return result;
        }

        public static SyncResult error(String message) {
            SyncResult result = new SyncResult();
            result.success = false;
            result.message = message;
            result.data = null;
            return result;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public Map<String, Object> getData() {
            return data;
        }
    }
}
