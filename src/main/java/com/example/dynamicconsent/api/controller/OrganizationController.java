package com.example.dynamicconsent.api.controller;

import com.example.dynamicconsent.api.dto.CommonDTOs;
import com.example.dynamicconsent.domain.model.enums.ConsentStatus;
import com.example.dynamicconsent.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orgs")
@Tag(name = "Organization", description = "Organization related API")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @GetMapping
    @Operation(summary = "Organization List/Search", description = "Category/query-based organization list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    public ResponseEntity<Page<CommonDTOs.OrganizationResponse>> getOrganizations(
            @Parameter(description = "Category", example = "FINANCE")
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String q,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort criteria (e.g., name,asc)", example = "name,asc")
            @RequestParam(defaultValue = "name,asc") String sort) {
        Page<CommonDTOs.OrganizationResponse> organizations = organizationService.getOrganizations(category, q, page, size, sort);
        return ResponseEntity.ok(organizations);
    }

    @GetMapping("/{orgId}")
    @Operation(summary = "Organization Detail", description = "Add ISMS-P status, policy URL, policy last updated date fields")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Organization not found")
    })
    public ResponseEntity<CommonDTOs.OrganizationDetailResponse> getOrganizationDetail(
            @Parameter(description = "Organization ID", example = "ORG001")
            @PathVariable String orgId,
            Authentication authentication) {
        String userId = authentication.getName();
        return organizationService.getOrganizationDetail(orgId, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{orgId}/consents")
    @Operation(summary = "Organization Consents", description = "Support status=ACTIVE|REVOKED|ALL&page&size filtering")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Organization not found")
    })
    public ResponseEntity<Page<CommonDTOs.OrganizationConsentResponse>> getOrganizationConsents(
            @Parameter(description = "Organization ID", example = "ORG001")
            @PathVariable String orgId,
            @Parameter(description = "Consent status", example = "ACTIVE")
            @RequestParam(required = false) ConsentStatus status,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        String userId = authentication.getName();
        Page<CommonDTOs.OrganizationConsentResponse> consents = organizationService.getOrganizationConsents(orgId, userId, status, page, size);
        return ResponseEntity.ok(consents);
    }

    @GetMapping("/{orgId}/shares")
    @Operation(summary = "Organization Shares", description = "Maintain, but graph format provided by /shares/graph")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Organization not found")
    })
    public ResponseEntity<List<CommonDTOs.ShareEdgeResponse>> getOrganizationShares(
            @Parameter(description = "Organization ID", example = "ORG001")
            @PathVariable String orgId,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        String userId = authentication.getName();
        List<CommonDTOs.ShareEdgeResponse> shares = organizationService.getOrganizationShares(orgId, userId);
        return ResponseEntity.ok(shares);
    }
}