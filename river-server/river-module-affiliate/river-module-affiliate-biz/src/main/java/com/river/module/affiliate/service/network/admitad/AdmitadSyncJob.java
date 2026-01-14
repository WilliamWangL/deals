package com.river.module.affiliate.service.network.admitad;

import com.river.framework.quartz.core.handler.JobHandler;
import com.river.framework.tenant.core.job.TenantJob;
import com.river.module.affiliate.dal.dataobject.NetworkCredentialDO;
import com.river.module.affiliate.dal.mysql.NetworkCredentialMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.List;

@Slf4j
@Component("admitadSyncJob")
public class AdmitadSyncJob implements JobHandler {

    private static final String NETWORK_CODE = "admitad";

    @Resource
    private NetworkCredentialMapper credentialMapper;

    @Resource
    private AdmitadSyncService admitadSyncService;

    @Override
    @TenantJob
    public String execute(String param) throws Exception {
        log.info("[AdmitadSyncJob] Starting Admitad sync job");

        List<NetworkCredentialDO> credentials = credentialMapper.selectEnabledByNetworkCode(NETWORK_CODE);
        
        if (credentials.isEmpty()) {
            log.info("[AdmitadSyncJob] No enabled Admitad credentials found");
            return "No credentials";
        }

        int successCount = 0;
        for (NetworkCredentialDO credential : credentials) {
            try {
                log.info("[AdmitadSyncJob] Syncing for network credential id={}", credential.getId());
                admitadSyncService.syncCampaigns(credential);
                successCount++;
            } catch (Exception e) {
                log.error("[AdmitadSyncJob] Failed to sync for credential id={}: {}", 
                    credential.getId(), e.getMessage());
            }
        }

        String result = String.format("Synced %d/%d credentials", successCount, credentials.size());
        log.info("[AdmitadSyncJob] {}", result);
        return result;
    }

}
