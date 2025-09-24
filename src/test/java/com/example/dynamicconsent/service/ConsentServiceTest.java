package com.example.dynamicconsent.service;

import com.example.dynamicconsent.api.dto.CommonDTOs;
import com.example.dynamicconsent.domain.model.*;
import com.example.dynamicconsent.domain.model.enums.*;
import com.example.dynamicconsent.domain.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsentServiceTest {

    @Mock
    private ConsentRepository consentRepository;
    
    @Mock
    private ConsentEventRepository consentEventRepository;
    
    @Mock
    private OrganizationRepository organizationRepository;
    
    @Mock
    private UserService userService;

    @InjectMocks
    private ConsentService consentService;

    private User testUser;
    private Organization testOrganization;
    private Consent testConsent;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId("test-user");
        testUser.setDisplayName("Test User");

        testOrganization = new Organization();
        testOrganization.setOrgId("TEST_ORG");
        testOrganization.setName("Test Organization");

        testConsent = new Consent();
        testConsent.setUser(testUser);
        testConsent.setOrganization(testOrganization);
        testConsent.setStatus(ConsentStatus.ACTIVE);
        testConsent.setSensitivity(DataSensitivity.MEDIUM);
        testConsent.setScopes(Set.of(ConsentScope.PROFILE));
    }

    @Test
    void getConsentDetail_ShouldReturnConsentDetail_WhenConsentExists() {
        // Given
        when(consentRepository.findById(1L)).thenReturn(Optional.of(testConsent));

        // When
        CommonDTOs.ConsentDetailResponse result = consentService.getConsentDetail(1L, "test-user");

        // Then
        assertNotNull(result);
        assertEquals("TEST_ORG", result.organizationId());
        assertEquals(ConsentStatus.ACTIVE, result.status());
    }

    @Test
    void getConsentDetail_ShouldThrowException_WhenConsentNotFound() {
        // Given
        when(consentRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            consentService.getConsentDetail(1L, "test-user"));
    }

    @Test
    void getConsentDetail_ShouldThrowException_WhenUserNotAuthorized() {
        // Given
        when(consentRepository.findById(1L)).thenReturn(Optional.of(testConsent));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            consentService.getConsentDetail(1L, "other-user"));
    }

    @Test
    void createConsent_ShouldCreateConsent_WhenValidRequest() {
        // Given
        CommonDTOs.ConsentCreateRequest request = new CommonDTOs.ConsentCreateRequest(
            "TEST_ORG",
            DataSensitivity.MEDIUM,
            Set.of(ConsentScope.PROFILE),
            Instant.now(),
            Instant.now().plusSeconds(86400),
            "Test purpose"
        );

        when(userService.getOrCreateUser("test-user")).thenReturn(testUser);
        when(organizationRepository.findByOrgId("TEST_ORG")).thenReturn(Optional.of(testOrganization));
        when(consentRepository.save(any(Consent.class))).thenReturn(testConsent);

        // When
        CommonDTOs.ConsentDetailResponse result = consentService.createConsent(request, "test-user");

        // Then
        assertNotNull(result);
        verify(consentRepository).save(any(Consent.class));
        verify(consentEventRepository).save(any(ConsentEvent.class));
        verify(userService).updateLastConsentChange("test-user");
    }

    @Test
    void revokeConsent_ShouldRevokeConsent_WhenValidRequest() {
        // Given
        when(consentRepository.findById(1L)).thenReturn(Optional.of(testConsent));
        when(consentRepository.save(any(Consent.class))).thenReturn(testConsent);

        // When
        consentService.revokeConsent(1L, "test-user");

        // Then
        verify(consentRepository).save(any(Consent.class));
        verify(consentEventRepository).save(any(ConsentEvent.class));
        verify(userService).updateLastConsentChange("test-user");
    }
}
