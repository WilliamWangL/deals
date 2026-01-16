package com.river.module.campaign.service;

import com.river.framework.test.core.ut.BaseMockitoUnitTest;
import com.river.module.campaign.dal.dataobject.CampaignDO;
import com.river.module.campaign.dal.mysql.CampaignMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * {@link CampaignServiceImpl} 的单元测试
 */
class CampaignServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private CampaignServiceImpl campaignService;

    @Mock
    private CampaignMapper campaignMapper;

    @Mock
    private TrafficSourceService trafficSourceService;

    @Test
    void testCreateCampaign_Success() {
        // Given
        CampaignDO campaign = new CampaignDO();
        campaign.setName("Test Campaign");
        campaign.setTrafficSourceId(1L);
        doNothing().when(trafficSourceService).validateTrafficSourceExists(eq(1L));
        when(campaignMapper.insert(any(CampaignDO.class))).thenReturn(1);

        // When
        Long result = campaignService.createCampaign(campaign);

        // Then
        verify(trafficSourceService).validateTrafficSourceExists(eq(1L));
        verify(campaignMapper).insert(any(CampaignDO.class));
    }

    @Test
    void testGetCampaign_Success() {
        // Given
        Long campaignId = 1L;
        CampaignDO expectedCampaign = new CampaignDO();
        expectedCampaign.setId(campaignId);
        expectedCampaign.setName("Test Campaign");
        when(campaignMapper.selectById(eq(campaignId))).thenReturn(expectedCampaign);

        // When
        CampaignDO result = campaignService.getCampaign(campaignId);

        // Then
        assertNotNull(result);
        assertEquals(campaignId, result.getId());
        assertEquals("Test Campaign", result.getName());
        verify(campaignMapper).selectById(eq(campaignId));
    }

    @Test
    void testGetCampaign_NotFound() {
        // Given
        Long campaignId = 999L;
        when(campaignMapper.selectById(eq(campaignId))).thenReturn(null);

        // When
        CampaignDO result = campaignService.getCampaign(campaignId);

        // Then
        assertNull(result);
        verify(campaignMapper).selectById(eq(campaignId));
    }

    @Test
    void testDeleteCampaign_Success() {
        // Given
        Long campaignId = 1L;
        CampaignDO existingCampaign = new CampaignDO();
        existingCampaign.setId(campaignId);
        when(campaignMapper.selectById(eq(campaignId))).thenReturn(existingCampaign);
        when(campaignMapper.deleteById(eq(campaignId))).thenReturn(1);

        // When
        campaignService.deleteCampaign(campaignId);

        // Then
        verify(campaignMapper).selectById(eq(campaignId));
        verify(campaignMapper).deleteById(eq(campaignId));
    }

}
