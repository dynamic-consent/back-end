package com.example.dynamicconsent.service;

import com.example.dynamicconsent.api.dto.CommonDTOs;
import com.example.dynamicconsent.domain.model.Consent;
import com.example.dynamicconsent.domain.model.ConsentEvent;
import com.example.dynamicconsent.domain.model.User;
import com.example.dynamicconsent.domain.model.enums.ConsentStatus;
import com.example.dynamicconsent.domain.repository.ConsentEventRepository;
import com.example.dynamicconsent.domain.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class HomeService {

    @Autowired
    private ConsentService consentService;
    
    @Autowired
    private ConsentEventRepository consentEventRepository;
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private UserService userService;

    public CommonDTOs.HomeSummaryResponse getHomeSummary(String userId) {
        List<Consent> consents = consentService.getUserConsents(userId);
        User user = userService.getOrCreateUser(userId);
        
        int totalConsents = consents.size();
        int activeConsents = (int) consents.stream().filter(c -> c.getStatus() == ConsentStatus.ACTIVE).count();
        int expiringConsents = consentService.getExpiringConsents(userId, 30).size();
        
        int unreadNotifications = (int) notificationRepository.countUnreadByUser(user);
        
        return new CommonDTOs.HomeSummaryResponse(
            totalConsents,
            activeConsents,
            expiringConsents,
            unreadNotifications
        );
    }

    public List<CommonDTOs.TimelineEventResponse> getTimeline(String userId, String after, int limit) {
        // Cursor-based pagination implementation
        Instant from = after != null ? Instant.parse(after) : Instant.now().minus(30, ChronoUnit.DAYS);
        
        Pageable pageable = PageRequest.of(0, limit);
        var events = consentEventRepository.findByUserIdAndFilters(userId, from, null, null, pageable);
        
        return events.getContent().stream()
            .map(this::toTimelineEventResponse)
            .toList();
    }

    public List<CommonDTOs.UpcomingConsentResponse> getUpcomingConsents(String userId, int days) {
        List<Consent> expiringConsents = consentService.getExpiringConsents(userId, days);
        
        return expiringConsents.stream()
            .map(consent -> {
                long daysUntilExpiry = ChronoUnit.DAYS.between(Instant.now(), consent.getValidUntil());
                return new CommonDTOs.UpcomingConsentResponse(
                    consent.getId(),
                    consent.getOrganization().getName(),
                    consent.getOrganization().getOrgId(),
                    consent.getValidUntil(),
                    (int) daysUntilExpiry,
                    consent.getStatus()
                );
            })
            .toList();
    }

    private CommonDTOs.TimelineEventResponse toTimelineEventResponse(ConsentEvent event) {
        return new CommonDTOs.TimelineEventResponse(
            event.getId(),
            event.getCreatedAt(),
            event.getType().name(),
            event.getDetails(),
            event.getConsent().getOrganization().getName(),
            event.getConsent().getId().toString()
        );
    }

}


