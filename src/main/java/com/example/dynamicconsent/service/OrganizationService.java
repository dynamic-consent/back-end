package com.example.dynamicconsent.service;

import com.example.dynamicconsent.api.dto.CommonDTOs;
import com.example.dynamicconsent.domain.model.Consent;
import com.example.dynamicconsent.domain.model.Organization;
import com.example.dynamicconsent.domain.model.User;
import com.example.dynamicconsent.domain.model.enums.ConsentStatus;
import com.example.dynamicconsent.domain.repository.OrganizationRepository;
import com.example.dynamicconsent.domain.repository.ConsentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class OrganizationService {

    @Autowired
    private OrganizationRepository organizationRepository;
    
    @Autowired
    private ConsentRepository consentRepository;

    public Page<CommonDTOs.OrganizationResponse> getOrganizations(String category, String query, int page, int size, String sort) {
        Sort sortObj = parseSort(sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);
        
        Page<Organization> organizations = organizationRepository.findActiveOrganizations(category, query, pageable);
        
        return organizations.map(this::toOrganizationResponse);
    }

    public Optional<CommonDTOs.OrganizationDetailResponse> getOrganizationDetail(String orgId, String userId) {
        return organizationRepository.findByOrgId(orgId)
                .map(org -> {
                    // Query user's consent information for this organization
                    List<Consent> userConsents = consentRepository.findByUserIdAndOrgId(userId, orgId, null, Pageable.unpaged()).getContent();
                    
                    int totalConsents = userConsents.size();
                    int activeConsents = (int) userConsents.stream()
                            .filter(c -> c.getStatus() == ConsentStatus.ACTIVE)
                            .count();
                    
                    return new CommonDTOs.OrganizationDetailResponse(
                            org.getOrgId(),
                            org.getName(),
                            org.getCategory(),
                            org.getDescription(),
                            org.getWebsite(),
                            org.getContactEmail(),
                            org.getContactPhone(),
                            org.getIsmsCertified(),
                            org.getPolicyUrl(),
                            org.getPolicyLastUpdated(),
                            totalConsents,
                            activeConsents
                    );
                });
    }

    public Page<CommonDTOs.OrganizationConsentResponse> getOrganizationConsents(String orgId, String userId, ConsentStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Consent> consents = consentRepository.findByUserIdAndOrgId(userId, orgId, status, pageable);
        
        return consents.map(this::toOrganizationConsentResponse);
    }

    public List<CommonDTOs.ShareEdgeResponse> getOrganizationShares(String orgId, String userId) {
        // Existing implementation - should use DataShare entity in practice
        List<Consent> userConsents = consentRepository.findByUserIdAndOrgId(userId, orgId, null, Pageable.unpaged()).getContent();
        
        return userConsents.stream()
                .filter(c -> c.getSharedOrganizationCount() != null && c.getSharedOrganizationCount() > 0)
                .flatMap(consent -> {
                    String dataTypes = consent.getScopes().stream()
                            .map(Enum::name)
                            .collect(Collectors.joining(","));
                    
                    return java.util.stream.IntStream.range(0, consent.getSharedOrganizationCount())
                            .mapToObj(i -> new CommonDTOs.ShareEdgeResponse(
                                    consent.getOrganization().getOrgId(),
                                    "SHARED_ORG_" + i,
                                    consent.getOrganization().getName(),
                                    "Shared Organization " + i,
                                    1L,
                                    dataTypes
                            ));
                })
                .collect(Collectors.toList());
    }

    private CommonDTOs.OrganizationResponse toOrganizationResponse(Organization org) {
        return new CommonDTOs.OrganizationResponse(
                org.getOrgId(),
                org.getName(),
                org.getCategory(),
                org.getDescription(),
                0, // totalConsents - calculated separately
                0  // activeConsents - calculated separately
        );
    }

    private CommonDTOs.OrganizationConsentResponse toOrganizationConsentResponse(Consent consent) {
        return new CommonDTOs.OrganizationConsentResponse(
                consent.getId(),
                consent.getStatus(),
                consent.getSensitivity(),
                consent.getScopes(),
                consent.getValidFrom(),
                consent.getValidUntil(),
                consent.getLastUsedAt(),
                consent.getSharedOrganizationCount(),
                consent.getPurpose()
        );
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isEmpty()) {
            return Sort.by("name").ascending();
        }
        
        String[] parts = sort.split(",");
        String property = parts[0];
        Sort.Direction direction = parts.length > 1 && "desc".equalsIgnoreCase(parts[1]) 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC;
        
        return Sort.by(direction, property);
    }
}