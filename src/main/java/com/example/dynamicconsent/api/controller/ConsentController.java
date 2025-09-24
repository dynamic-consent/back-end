package com.example.dynamicconsent.api.controller;

import com.example.dynamicconsent.api.dto.CommonDTOs;
import com.example.dynamicconsent.domain.model.enums.ConsentEventType;
import com.example.dynamicconsent.domain.model.enums.ConsentStatus;
import com.example.dynamicconsent.service.ConsentEventService;
import com.example.dynamicconsent.service.ConsentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Consent API
 * - 이름/필드/구조 변경 없이 가독성만 개선
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Consent", description = "Consent related API")
public class ConsentController {

    @Autowired
    private ConsentService consentService;

    @Autowired
    private ConsentEventService consentEventService;

    // ===========================
    // ===== Event - Global ======
    // ===========================

    @GetMapping("/consents/events")
    @Operation(summary = "Consent Event List", description = "Filtered change event list by period/type")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Authentication Failed")
    })
    public ResponseEntity<Page<CommonDTOs.ConsentEventResponse>> getConsentEvents(
            @Parameter(description = "Start date (ISO8601)", example = "2024-01-01T00:00:00Z")
            @RequestParam(required = false) Instant from,
            @Parameter(description = "End date (ISO8601)", example = "2024-12-31T23:59:59Z")
            @RequestParam(required = false) Instant to,
            @Parameter(description = "Event type", example = "CREATED")
            @RequestParam(required = false) ConsentEventType type,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication
    ) {
        String userId = authentication.getName();
        return ResponseEntity.ok(
                consentEventService.getConsentEvents(userId, from, to, type, page, size)
        );
    }

    // =========================
    // ===== Consent List ======
    // =========================

    @GetMapping("/consents")
    @Operation(summary = "Consent List", description = "Consent list with status, orgId filters")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Authentication Failed")
    })
    public ResponseEntity<Page<CommonDTOs.ConsentDetailResponse>> getConsents(
            @Parameter(description = "Consent status", example = "ACTIVE")
            @RequestParam(required = false) ConsentStatus status,
            @Parameter(description = "Organization ID", example = "ORG001")
            @RequestParam(required = false) String orgId,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication
    ) {
        String userId = authentication.getName();
        return ResponseEntity.ok(
                consentService.getConsents(userId, status, orgId, page, size)
        );
    }

    // ===========================
    // ===== Consent Detail ======
    // ===========================

    @GetMapping("/consents/{consentId}")
    @Operation(summary = "Consent Detail", description = "Consent detail information")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Consent not found"),
            @ApiResponse(responseCode = "401", description = "Authentication Failed")
    })
    public ResponseEntity<CommonDTOs.ConsentDetailResponse> getConsentDetail(
            @Parameter(description = "Consent ID", example = "1")
            @PathVariable Long consentId,
            Authentication authentication
    ) {
        String userId = authentication.getName();
        return ResponseEntity.ok(
                consentService.getConsentDetail(consentId, userId)
        );
    }

    // ===========================
    // ===== Create Consent ======
    // ===========================

    @PostMapping("/consents")
    @Operation(summary = "Create Consent", description = "Support validateOnly=true mode without creation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Authentication Failed")
    })
    public ResponseEntity<?> createConsent(
            @Parameter(description = "Validation only mode", example = "false")
            @RequestParam(defaultValue = "false") boolean validateOnly,
            @Parameter(description = "Consent creation request")
            @Valid @RequestBody CommonDTOs.ConsentCreateRequest request,
            Authentication authentication
    ) {
        String userId = authentication.getName();

        if (validateOnly) {
            // Return validation result without creating consent
            CommonDTOs.ConsentValidationResponse validation = consentService.validateConsent(request);
            return ResponseEntity.ok(validation);
        }

        // Create consent
        CommonDTOs.ConsentDetailResponse consent = consentService.createConsent(request, userId);
        return ResponseEntity.ok(consent);
    }

    // ==========================
    // ===== Update Consent =====
    // ==========================

    @PatchMapping("/consents/{consentId}")
    @Operation(summary = "Update Consent", description = "Partial update with op: ADD|REMOVE|REPLACE")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Consent not found"),
            @ApiResponse(responseCode = "401", description = "Authentication Failed")
    })
    public ResponseEntity<CommonDTOs.ConsentDetailResponse> updateConsent(
            @Parameter(description = "Consent ID", example = "1")
            @PathVariable Long consentId,
            @Parameter(description = "Partial update request")
            @Valid @RequestBody CommonDTOs.ConsentPartialUpdateRequest request,
            Authentication authentication
    ) {
        String userId = authentication.getName();

        CommonDTOs.ConsentUpdateRequest updateRequest = new CommonDTOs.ConsentUpdateRequest(
                null,                 // status - will be set based on op
                request.scopes(),
                request.validUntil(),
                request.purpose()
        );

        return ResponseEntity.ok(
                consentService.updateConsent(consentId, updateRequest, userId)
        );
    }

    // ==========================
    // ===== Revoke Consent =====
    // ==========================

    @PostMapping("/consents/{consentId}:revoke")
    @Operation(summary = "Revoke Consent", description = "Idempotent, allow revokeReason field")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Consent not found"),
            @ApiResponse(responseCode = "401", description = "Authentication Failed")
    })
    public ResponseEntity<Void> revokeConsent(
            @Parameter(description = "Consent ID", example = "1")
            @PathVariable Long consentId,
            @Parameter(description = "Revoke request")
            @Valid @RequestBody CommonDTOs.ConsentRevokeRequest request,
            Authentication authentication
    ) {
        String userId = authentication.getName();
        consentService.revokeConsent(consentId, userId);
        return ResponseEntity.noContent().build();
    }

    // ============================
    // ===== Event - By ID ========
    // ============================

    @GetMapping("/consents/{consentId}/events")
    @Operation(summary = "Consent Event History", description = "Include scope diff")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Consent not found"),
            @ApiResponse(responseCode = "401", description = "Authentication Failed")
    })
    public ResponseEntity<Page<CommonDTOs.ConsentEventResponse>> getConsentEvents(
            @Parameter(description = "Consent ID", example = "1")
            @PathVariable Long consentId,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication
    ) {
        // authentication은 보안 필터링 용도로 유지되지만 이 메서드에서는 userId가 직접 쓰이지 않음
        return ResponseEntity.ok(
                consentEventService.getConsentEventsByConsentId(consentId, page, size)
        );
    }
}
