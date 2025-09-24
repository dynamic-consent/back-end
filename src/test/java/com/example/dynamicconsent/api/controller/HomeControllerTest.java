package com.example.dynamicconsent.api.controller;

import com.example.dynamicconsent.api.dto.CommonDTOs;
import com.example.dynamicconsent.service.HomeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HomeService homeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "test-user")
    void getSummary_ShouldReturnHomeSummary() throws Exception {
        // Given
        CommonDTOs.HomeSummaryResponse summary = new CommonDTOs.HomeSummaryResponse(
            5, 3, 2, 0.6, "MEDIUM", 1
        );
        when(homeService.getHomeSummary("test-user")).thenReturn(summary);

        // When & Then
        mockMvc.perform(get("/api/v1/home/summary")
                .header("X-UserId", "test-user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalConsents").value(5))
                .andExpect(jsonPath("$.activeConsents").value(3))
                .andExpect(jsonPath("$.riskLevel").value("MEDIUM"));
    }

    @Test
    @WithMockUser(username = "test-user")
    void getTimeline_ShouldReturnTimeline() throws Exception {
        // Given
        CommonDTOs.TimelineEventResponse event = new CommonDTOs.TimelineEventResponse(
            1L, Instant.now(), "CREATED", "Test event", "Test Org", "1"
        );
        when(homeService.getTimeline(anyString(), any(), any())).thenReturn(List.of(event));

        // When & Then
        mockMvc.perform(get("/api/v1/home/timeline")
                .header("X-UserId", "test-user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("CREATED"))
                .andExpect(jsonPath("$[0].details").value("Test event"));
    }

    @Test
    @WithMockUser(username = "test-user")
    void getUpcoming_ShouldReturnUpcomingConsents() throws Exception {
        // Given
        CommonDTOs.UpcomingConsentResponse upcoming = new CommonDTOs.UpcomingConsentResponse(
            1L, "Test Org", "TEST_ORG", Instant.now().plusSeconds(86400), 30, 
            com.example.dynamicconsent.domain.model.enums.ConsentStatus.ACTIVE
        );
        when(homeService.getUpcomingConsents(anyString(), any(Integer.class))).thenReturn(List.of(upcoming));

        // When & Then
        mockMvc.perform(get("/api/v1/home/upcoming?days=30")
                .header("X-UserId", "test-user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].organizationName").value("Test Org"))
                .andExpect(jsonPath("$[0].daysUntilExpiry").value(30));
    }
}
