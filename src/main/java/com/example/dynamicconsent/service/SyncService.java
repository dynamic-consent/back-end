package com.example.dynamicconsent.service;

import com.example.dynamicconsent.api.dto.CommonDTOs;
import com.example.dynamicconsent.domain.model.Notice;
import com.example.dynamicconsent.domain.model.Notification;
import com.example.dynamicconsent.domain.model.User;
import com.example.dynamicconsent.domain.repository.ConsentEventRepository;
import com.example.dynamicconsent.domain.repository.NoticeRepository;
import com.example.dynamicconsent.domain.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class SyncService {

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ConsentEventRepository consentEventRepository;

    @Autowired
    private HomeService homeService;

    @Autowired
    private ConsentEventService consentEventService;

    public CommonDTOs.SyncResponse getSyncData(String userId, Instant since, int limit) {
        User user = new User();
        user.setUserId(userId);

        // 1) 홈 요약
        CommonDTOs.HomeSummaryResponse homeSummary = homeService.getHomeSummary(userId);

        // 2) 공지 미리보기 (최신 3건)
        Pageable noticePageable = PageRequest.of(0, 3,
                Sort.by("priority").descending().and(Sort.by("createdAt").descending()));
        List<Notice> notices = noticeRepository.findActiveNoticesOrderByPriority(noticePageable);
        List<CommonDTOs.NoticePreviewResponse> noticePreviews = notices.stream()
                .map(n -> new CommonDTOs.NoticePreviewResponse(
                        n.getId(),
                        n.getTitle(),
                        n.getCategory().name(),
                        n.getCreatedAt()
                ))
                .toList();

        // 3) 알림 (since 이후) — PageMeta 포함해서 묶기
        Pageable notificationPageable = PageRequest.of(0, limit, Sort.by("createdAt").descending());
        Page<Notification> notificationPage = notificationRepository.findByUserAndSince(user, since, notificationPageable);

        List<CommonDTOs.NotificationResponse> notificationItems = notificationPage.getContent().stream()
                .map(n -> new CommonDTOs.NotificationResponse(
                        n.getId(),
                        n.getType(),       // DTO가 String으로 받도록 설계됨
                        n.getTitle(),
                        n.getMessage(),
                        n.getIsRead(),
                        n.getActionUrl(),
                        n.getCreatedAt()
                ))
                .toList();

        CommonDTOs.PageMeta meta = new CommonDTOs.PageMeta(
                notificationPage.getNumber(),
                notificationPage.getSize(),
                notificationPage.getTotalElements(),
                notificationPage.getTotalPages(),
                notificationPage.hasNext()
        );

        CommonDTOs.NotificationListResponse notifications =
                new CommonDTOs.NotificationListResponse(
                        notificationItems,
                        meta,
                        "/api/v1/notifications:read-all"
                );

        // 4) 동의 이벤트 (since 이후)
        List<CommonDTOs.ConsentEventResponse> events =
                consentEventService.getConsentEvents(userId, since, null, null, 0, limit).getContent();

        // ConsentEventResponse -> TimelineEventResponse 매핑
        List<CommonDTOs.TimelineEventResponse> timeline =
                events.stream()
                        .map(e -> new CommonDTOs.TimelineEventResponse(
                                toLongOrZero(e.eventId()),   // id(Long)
                                e.occurredAt(),              // occurredAt
                                e.type(),                    // type
                                e.detail(),                  // detail
                                "",                          // organizationName (정보 없음)
                                e.consentId()                // consentId
                        ))
                        .toList();

        // 5) SyncResponse
        return new CommonDTOs.SyncResponse(
                homeSummary,
                noticePreviews,
                notifications,
                timeline,
                since
        );
    }

    /** 문자열을 Long으로 변환. null/빈값/파싱실패 시 0L 반환 */
    private static Long toLongOrZero(String s) {
        if (s == null || s.isBlank()) return 0L;
        try {
            return Long.parseLong(s.trim());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
