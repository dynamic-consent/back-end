package com.example.dynamicconsent.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.example.dynamicconsent.domain.model.enums.ConsentScope;
import com.example.dynamicconsent.domain.model.enums.ConsentStatus;
import com.example.dynamicconsent.domain.model.enums.DataSensitivity;
import com.example.dynamicconsent.domain.model.enums.NotificationType;

/**
 * Common DTO collection for Dynamic Consent API.
 * NOTE:
 * - Public names, field counts, and structures are preserved intentionally.
 * - Only readability and consistency improvements applied.
 */
public class CommonDTOs {

    // =========================
    // ===== Authentication =====
    // =========================

    public record SmsStartRequest(@NotBlank String phoneNumber) {}
    public record SmsVerifyRequest(@NotBlank String phoneNumber, @NotBlank String code) {}
    public record TokenRefreshRequest(@NotBlank String refreshToken) {}

    public record AuthStartResponse(@NotBlank String txId, @NotNull Instant expireAt) {}
    public record AuthChallengeResponse(@NotBlank String challenge, @NotNull Instant expireAt) {}

    // expiresAt 유지 (4 필드)
    public record AuthCompleteResponse(
            @NotBlank String accessToken,
            @NotBlank String refreshToken,
            @NotBlank String userId,
            @NotNull  Instant expiresAt
    ) {}

    // expiresAt 유지 (4 필드)
    public record AuthVerifyResponse(
            @NotBlank String accessToken,
            @NotBlank String refreshToken,
            @NotBlank String userId,
            @NotNull  Instant expiresAt
    ) {}

    // Service에서 쓰는 형태 유지
    public record AuthMeResponse(
            @NotBlank String userId,
            String displayName,
            Instant lastLoginAt,
            Boolean authenticated
    ) {}

    public record TokenRefreshResponse(
            @NotBlank String accessToken,
            @NotBlank String refreshToken,
            @NotNull  Instant expireAt
    ) {}

    // ===================
    // ===== Consent =====
    // ===================

    public record ConsentCreateRequest(
            String organizationId,
            String sensitivity,
            List<String> scopes,
            Instant validFrom,
            Instant validUntil,
            String purpose
    ) {}

    public record ConsentPartialUpdateRequest(
            List<String> scopes,
            Instant validUntil,
            String purpose
    ) {}

    public record ConsentRevokeRequest(@NotBlank String consentId, String reason) {}

    public record ConsentDetailResponse(
            Long consentId,
            String orgId,
            String orgName,
            ConsentStatus status,        // enum 그대로
            DataSensitivity sensitivity, // enum 그대로
            Set<ConsentScope> scopes,    // Set<ConsentScope> 그대로
            Instant validFrom,
            Instant validUntil,
            Instant lastUsedAt,
            Integer sharedOrganizationCount,
            String purpose,
            String revokeReason,
            Instant createdAt,
            Instant updatedAt
    ) {}

    public record ConsentEventResponse(
            String eventId,
            String consentId,
            String type,
            Instant occurredAt,
            String detail
    ) {}

    public record ConsentUpdateRequest(
            String status,      // ACTIVE, REVOKED 등
            List<String> scopes,
            Instant validUntil,
            String purpose
    ) {}

    public record ConsentValidationResponse(
            boolean valid,
            String message,
            String errorCode
    ) {}

    // ================
    // ===== User =====
    // ================

    public record UserProfileResponse(
            String userId,
            String displayName,
            String email,
            String phoneNumber,
            Instant birthDate,             // Instant 유지
            Instant lastConsentChangeAt,
            Instant lastLoginAt,
            Instant createdAt,
            UserPreferencesResponse preferences
    ) {}

    public record UserPreferencesResponse(
            String theme,               // e.g., "light" / "dark"
            Boolean emailNotifications, // true/false
            Boolean pushNotifications,  // true/false
            Boolean smsNotifications,   // true/false
            Double riskThreshold,       // e.g., 0.7
            String language,            // "ko" / "en"
            Boolean autoSync            // true/false
    ) {}

    public record UserUpdateRequest(
            String displayName,
            String email,
            String phoneNumber,
            Instant birthDate           // Instant 유지
    ) {}

    public record UserPreferencesUpdateRequest(
            String theme,
            Boolean emailNotifications,
            Boolean pushNotifications,
            Boolean smsNotifications,
            Double riskThreshold,
            String language,
            Boolean autoSync
    ) {}

    // =================
    // ===== Home ======
    // =================

    public record HomeSummaryResponse(
            int totalConsents,
            int activeConsents,
            int expiringConsents,
            int unreadNotifications
    ) {}

    public record TimelineEventResponse(
            Long eventId,
            Instant occurredAt,
            String type,
            String detail,
            String orgName,
            String consentId
    ) {}

    public record UpcomingConsentResponse(
            Long consentId,
            String orgName,
            String orgId,
            Instant validUntil,
            Integer daysUntilExpiry,
            ConsentStatus status
    ) {}

    // ===================
    // ===== Notice ======
    // ===================

    public record NoticePreviewResponse(
            Long id,
            String title,
            String category,  // 추가
            Instant createdAt
    ) {}

    public record NoticeResponse(
            Long id,
            String title,
            String content,
            String category,  // 추가
            Integer priority, // 추가
            Instant createdAt,
            Instant updatedAt
    ) {}

    // ==========================
    // ===== Notification =======
    // ==========================

    public record NotificationResponse(
            Long id,
            NotificationType type, // String → NotificationType 유지
            String title,
            String message,
            boolean isRead,
            String actionUrl,
            Instant createdAt
    ) {}

    public record PageMeta(
            int page,
            int size,
            long totalElements,
            int totalPages,
            boolean hasNext
    ) {}

    public record NotificationListResponse(
            List<NotificationResponse> items,
            PageMeta meta,
            String bulkMarkUrl
    ) {}

    // ===================
    // ===== Device ======
    // ===================

    public record DeviceRegisterRequest(
            String deviceId,
            String pushToken,
            String platform,   // ANDROID / IOS
            String appVersion  // 앱 버전 추가
    ) {}

    public record DeviceResponse(
            Long id,
            String deviceId,
            String platform,
            String appVersion, // 앱 버전 추가
            Boolean isActive,
            Instant registeredAt
    ) {}

    // ============================
    // ===== Organization(s) ======
    // ============================

    public record OrganizationResponse(
            String orgId,
            String name,
            String category,
            String description,
            int totalConsents,
            int activeConsents
    ) {}

    public record OrganizationDetailResponse(
            String orgId,
            String name,
            String category,
            String description,
            String website,
            String contactEmail,
            String contactPhone,
            Boolean ismsCertified,
            String policyUrl,
            Instant policyLastUpdated,
            int totalConsents,
            int activeConsents
    ) {}

    public record OrganizationConsentResponse(
            Long consentId,
            ConsentStatus status,
            DataSensitivity sensitivity,
            Set<ConsentScope> scopes,
            Instant validFrom,
            Instant validUntil,
            Instant lastUsedAt,
            Integer sharedOrganizationCount,
            String purpose
    ) {}

    // ==========================
    // ===== Share (Graph) ======
    // ==========================

    public record ShareEdgeResponse(
            String fromOrgId,
            String toOrgId,
            String fromOrgName,
            String toOrgName,
            Long weight,
            String dataTypes
    ) {}

    public record ShareGraphResponse(
            List<ShareNodeResponse> nodes,
            List<ShareEdgeResponse> edges,
            Instant lastUpdated
    ) {}

    public record ShareNode(
            String orgId,
            String name,
            String category
    ) {}

    public record ShareNodeResponse(
            String orgId,
            String name,
            String category,
            Boolean ismsCertified
    ) {}

    // ==============================
    // ===== Synchronization ========
    // ==============================

    public record SyncResponse(
            HomeSummaryResponse homeSummary,                     // 홈 요약
            List<NoticePreviewResponse> notices,                 // 공지 미리보기
            NotificationListResponse notifications,              // 알림 리스트
            List<TimelineEventResponse> timelineEvents,          // 타임라인 이벤트들
            Instant lastSync                                      // 마지막 동기화 시간
    ) {}

    // =========================
    // ===== Error Handling ====
    // =========================

    public record ErrorResponse(
            String timestamp,            // 오류 발생 시각
            int status,                  // HTTP 상태 코드
            String code,                 // 에러 코드 (예: INVALID_ARGUMENT, AUTHENTICATION_FAILED)
            String message,              // 에러 메시지
            Map<String, Object> details, // 추가 세부 정보 (필드 오류 등)
            String requestId             // 요청 추적용 ID
    ) {}
}
