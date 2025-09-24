package com.example.dynamicconsent.service;

import com.example.dynamicconsent.api.dto.CommonDTOs;
import com.example.dynamicconsent.domain.model.Notice;
import com.example.dynamicconsent.domain.repository.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class NoticeService {

    @Autowired
    private NoticeRepository noticeRepository;

    public List<CommonDTOs.NoticePreviewResponse> getNoticePreviews(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("priority").descending().and(Sort.by("createdAt").descending()));
        List<Notice> notices = noticeRepository.findActiveNoticesOrderByPriority(pageable);
        
        return notices.stream()
                .map(notice -> new CommonDTOs.NoticePreviewResponse(
                        notice.getId(),
                        notice.getTitle(),
                        notice.getCategory().name(),
                        notice.getCreatedAt()
                ))
                .toList();
    }

    public Page<CommonDTOs.NoticeResponse> getNotices(int page, int size, String sort) {
        Sort sortObj = parseSort(sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);
        
        Page<Notice> notices = noticeRepository.findActiveNotices(pageable);
        
        return notices.map(notice -> new CommonDTOs.NoticeResponse(
                notice.getId(),
                notice.getTitle(),
                notice.getContent(),
                notice.getCategory().name(),
                notice.getPriority(),
                notice.getCreatedAt(),
                notice.getUpdatedAt()
        ));
    }

    public Optional<CommonDTOs.NoticeResponse> getNoticeById(Long noticeId) {
        return noticeRepository.findById(noticeId)
                .filter(Notice::getIsActive)
                .map(notice -> new CommonDTOs.NoticeResponse(
                        notice.getId(),
                        notice.getTitle(),
                        notice.getContent(),
                        notice.getCategory().name(),
                        notice.getPriority(),
                        notice.getCreatedAt(),
                        notice.getUpdatedAt()
                ));
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isEmpty()) {
            return Sort.by("createdAt").descending();
        }
        
        String[] parts = sort.split(",");
        String property = parts[0];
        Sort.Direction direction = parts.length > 1 && "desc".equalsIgnoreCase(parts[1]) 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC;
        
        return Sort.by(direction, property);
    }
}
