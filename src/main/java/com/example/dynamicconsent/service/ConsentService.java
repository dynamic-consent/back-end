package com.example.dynamicconsent.service;

import com.example.dynamicconsent.api.dto.CommonDTOs;
import com.example.dynamicconsent.domain.model.Consent;
import com.example.dynamicconsent.domain.model.ConsentEvent;
import com.example.dynamicconsent.domain.model.Organization;
import com.example.dynamicconsent.domain.model.User;
import com.example.dynamicconsent.domain.model.enums.ConsentEventType;
import com.example.dynamicconsent.domain.model.enums.ConsentStatus;
import com.example.dynamicconsent.domain.model.enums.ConsentScope;
import com.example.dynamicconsent.domain.model.enums.DataSensitivity;
import com.example.dynamicconsent.domain.repository.ConsentEventRepository;
import com.example.dynamicconsent.domain.repository.ConsentRepository;
import com.example.dynamicconsent.domain.repository.OrganizationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ConsentService {

    private final ConsentRepository consentRepository;
    private final ConsentEventRepository consentEventRepository;
    private final OrganizationRepository organizationRepository;
    private final UserService userService;

    public ConsentService(ConsentRepository consentRepository,
                          ConsentEventRepository consentEventRepository,
                          OrganizationRepository organizationRepository,
                          UserService userService) {
        this.consentRepository = consentRepository;
        this.consentEventRepository = consentEventRepository;
        this.organizationRepository = organizationRepository;
        this.userService = userService;
    }

    public CommonDTOs.ConsentDetailResponse getConsentDetail(Long consentId, String userId) {
        Consent consent = consentRepository.findById(consentId)
                .orElseThrow(() -> new IllegalArgumentException("Consent not found"));

        if (!consent.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Access denied");
        }

        return toConsentDetailResponse(consent);
    }

    public CommonDTOs.ConsentDetailResponse createConsent(CommonDTOs.ConsentCreateRequest request, String userId) {
        User user = userService.getOrCreateUser(userId);

        Organization organization = organizationRepository.findByOrgId(request.organizationId())
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));

        // String → Enum 매핑
        DataSensitivity sensitivity = request.sensitivity() == null
                ? null
                : DataSensitivity.valueOf(request.sensitivity().toUpperCase());

        Set<ConsentScope> scopeSet = request.scopes() == null
                ? Set.of()
                : request.scopes()
                .stream()
                .map(s -> ConsentScope.valueOf(s.toUpperCase()))
                .collect(Collectors.toSet());

        Consent consent = new Consent();
        consent.setUser(user);
        consent.setOrganization(organization);
        consent.setStatus(ConsentStatus.ACTIVE);
        consent.setSensitivity(sensitivity);
        consent.setScopes(scopeSet);
        consent.setValidFrom(Optional.ofNullable(request.validFrom()).orElse(Instant.now()));
        consent.setValidUntil(request.validUntil());
        consent.setPurpose(request.purpose());
        consent.setSharedOrganizationCount(0);

        consent = consentRepository.save(consent);

        recordEvent(consent, ConsentEventType.CREATED, "Consent created", null);
        userService.updateLastConsentChange(userId);

        return toConsentDetailResponse(consent);
    }

    public CommonDTOs.ConsentDetailResponse updateConsent(Long consentId, CommonDTOs.ConsentUpdateRequest request, String userId) {
        Consent consent = consentRepository.findById(consentId)
                .orElseThrow(() -> new IllegalArgumentException("Consent not found"));

        if (!consent.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Access denied");
        }

        if (request.status() != null && !request.status().isBlank()) {
            consent.setStatus(ConsentStatus.valueOf(request.status().toUpperCase()));
        }
        if (request.scopes() != null && !request.scopes().isEmpty()) {
            Set<ConsentScope> scopeSet = request.scopes()
                    .stream()
                    .map(s -> ConsentScope.valueOf(s.toUpperCase()))
                    .collect(Collectors.toSet());
            consent.setScopes(scopeSet);
        }
        if (request.validUntil() != null) {
            consent.setValidUntil(request.validUntil());
        }
        if (request.purpose() != null) {
            consent.setPurpose(request.purpose());
        }

        consent = consentRepository.save(consent);
        recordEvent(consent, ConsentEventType.UPDATED, "Consent updated", null);
        userService.updateLastConsentChange(userId);

        return toConsentDetailResponse(consent);
    }

    public void revokeConsent(Long consentId, String userId) {
        Consent consent = consentRepository.findById(consentId)
                .orElseThrow(() -> new IllegalArgumentException("Consent not found"));

        if (!consent.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Access denied");
        }

        consent.setStatus(ConsentStatus.REVOKED);
        consentRepository.save(consent);
        recordEvent(consent, ConsentEventType.REVOKED, "Consent revoked", null);
        userService.updateLastConsentChange(userId);
    }

    public List<CommonDTOs.ConsentEventResponse> getConsentEvents(Long consentId, String userId) {
        Consent consent = consentRepository.findById(consentId)
                .orElseThrow(() -> new IllegalArgumentException("Consent not found"));

        if (!consent.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Access denied");
        }

        return consentEventRepository.findByConsentOrderByCreatedAtDesc(consent)
                .stream()
                .map(this::toConsentEventResponse)
                .toList();
    }

    public List<Consent> getUserConsents(String userId) {
        return consentRepository.findByUserId(userId);
    }

    public Page<Consent> getUserConsents(String userId, Pageable pageable) {
        return consentRepository.findByUserId(userId, pageable);
    }

    public List<Consent> getExpiringConsents(String userId, int days) {
        Instant from = Instant.now();
        Instant to = from.plusSeconds(days * 24L * 60 * 60);
        return consentRepository.findExpiringConsents(userId, from, to);
    }

    public Page<CommonDTOs.ConsentDetailResponse> getConsents(String userId, ConsentStatus status, String orgId, int page, int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<Consent> consents = consentRepository.findByUserIdAndFilters(userId, status, orgId, pageable);
        return consents.map(this::toConsentDetailResponse);
    }

    public CommonDTOs.ConsentValidationResponse validateConsent(CommonDTOs.ConsentCreateRequest request) {
        boolean isValid = request.scopes() != null && !request.scopes().isEmpty()
                && request.validUntil() != null
                && request.validUntil().isAfter(Instant.now());

        return new CommonDTOs.ConsentValidationResponse(
                isValid,
                isValid ? "Valid consent request" : "Invalid consent request",
                null
        );
    }

    private CommonDTOs.ConsentDetailResponse toConsentDetailResponse(Consent consent) {
        return new CommonDTOs.ConsentDetailResponse(
                consent.getId(),
                consent.getOrganization().getOrgId(),
                consent.getOrganization().getName(),
                consent.getStatus(),
                consent.getSensitivity(),
                consent.getScopes(),
                consent.getValidFrom(),
                consent.getValidUntil(),
                consent.getLastUsedAt(),
                consent.getSharedOrganizationCount(),
                consent.getPurpose(),
                consent.getRevokeReason(),
                consent.getCreatedAt(),
                consent.getUpdatedAt()
        );
    }

    private CommonDTOs.ConsentEventResponse toConsentEventResponse(ConsentEvent event) {
        return new CommonDTOs.ConsentEventResponse(
                String.valueOf(event.getId()),                            // eventId
                event.getConsent() != null ?                              // consentId
                        String.valueOf(event.getConsent().getId()) : "",
                event.getType().name(),                                   // type
                event.getCreatedAt(),                                     // occurredAt
                event.getDetails() != null ? event.getDetails() : ""      // detail
        );
    }

    private void recordEvent(Consent consent, ConsentEventType type, String details, String metadata) {
        ConsentEvent event = new ConsentEvent();
        event.setConsent(consent);
        event.setUser(consent.getUser());
        event.setType(type);
        event.setDetails(details);
        event.setMetadata(metadata);
        event.setOrganizationName(consent.getOrganization().getName());
        consentEventRepository.save(event);
    }
}
