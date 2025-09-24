package com.example.dynamicconsent.service;

import com.example.dynamicconsent.api.dto.CommonDTOs;
import com.example.dynamicconsent.domain.model.Notification;
import com.example.dynamicconsent.domain.model.User;
import com.example.dynamicconsent.domain.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional(readOnly = true)
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private UserService userService;

    public CommonDTOs.NotificationListResponse getNotifications(String userId, int page, int size, String sort) {
        User user = userService.getOrCreateUser(userId);
        
        Sort sortObj = parseSort(sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);
        
        Page<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        
        Page<CommonDTOs.NotificationResponse> notificationResponses = notifications.map(this::toNotificationResponse);
        
        CommonDTOs.PageMeta meta = new CommonDTOs.PageMeta(
            notifications.getNumber(),
            notifications.getSize(),
            notifications.getTotalElements(),
            notifications.getTotalPages(),
            notifications.hasNext()
        );
        
        return new CommonDTOs.NotificationListResponse(
            notificationResponses.getContent(),
            meta,
            "/api/v1/notifications:read-all"
        );
    }

    @Transactional
    public void markAsRead(Long notificationId, String userId) {
        User user = userService.getOrCreateUser(userId);
        
        // Idempotency guarantee - already read notifications are processed as success
        int updated = notificationRepository.markAsReadByIdAndUser(notificationId, user);
        // Result regardless of success processing (idempotency)
    }

    @Transactional
    public void markAllAsRead(String userId) {
        User user = userService.getOrCreateUser(userId);
        
        // Idempotency guarantee - all notifications already read are processed as success
        notificationRepository.markAllAsReadByUser(user);
    }

    public void createNotification(String userId, String type, String title, String message, String actionUrl) {
        User user = userService.getOrCreateUser(userId);
        
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(com.example.dynamicconsent.domain.model.enums.NotificationType.valueOf(type));
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setActionUrl(actionUrl);
        notification.setIsRead(false);
        
        notificationRepository.save(notification);
    }

    public long getUnreadCount(String userId) {
        User user = userService.getOrCreateUser(userId);
        return notificationRepository.countUnreadByUser(user);
    }

    private CommonDTOs.NotificationResponse toNotificationResponse(Notification notification) {
        return new CommonDTOs.NotificationResponse(
            notification.getId(),
            notification.getType(),
            notification.getTitle(),
            notification.getMessage(),
            notification.getIsRead(),
            notification.getActionUrl(),
            notification.getCreatedAt()
        );
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


