package com.example.dynamicconsent.service;

import com.example.dynamicconsent.api.dto.CommonDTOs;
import com.example.dynamicconsent.domain.model.ConsentEvent;
import com.example.dynamicconsent.domain.model.enums.ConsentEventType;
import com.example.dynamicconsent.domain.repository.ConsentEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional(readOnly = true)
public class ConsentEventService {

    @Autowired
    private ConsentEventRepository consentEventRepository;

    public Page<CommonDTOs.ConsentEventResponse> getConsentEvents(String userId, Instant from, Instant to, 
                                                                 ConsentEventType type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        
        Page<ConsentEvent> events = consentEventRepository.findByUserIdAndFilters(userId, from, to, type, pageable);
        
        return events.map(this::toConsentEventResponse);
    }

    public Page<CommonDTOs.ConsentEventResponse> getConsentEventsByConsentId(Long consentId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        
        Page<ConsentEvent> events = consentEventRepository.findByConsentId(consentId, pageable);
        
        return events.map(this::toConsentEventResponse);
    }

    private CommonDTOs.ConsentEventResponse toConsentEventResponse(ConsentEvent event) {
        return new CommonDTOs.ConsentEventResponse(
                String.valueOf(event.getId()),                                   // eventId
                event.getConsent() != null ? String.valueOf(event.getConsent().getId()) : "", // consentId
                event.getType().name(),                                           // type
                event.getCreatedAt(),                                             // occurredAt
                event.getDetails() != null ? event.getDetails() : ""              // detail
        );
    }

}
