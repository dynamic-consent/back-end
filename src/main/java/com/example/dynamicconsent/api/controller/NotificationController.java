package com.example.dynamicconsent.api.controller;

import com.example.dynamicconsent.api.dto.CommonDTOs;
import com.example.dynamicconsent.domain.model.enums.ConsentStatus;
import com.example.dynamicconsent.service.DeviceService;
import com.example.dynamicconsent.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Notification", description = "Notification related API")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private DeviceService deviceService;

    @GetMapping("/notifications")
    @Operation(summary = "Notification List", description = "Support sorting (sort=createdAt,desc), include bulkMarkUrl in response")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Authentication Failed")
    })
    public ResponseEntity<CommonDTOs.NotificationListResponse> getNotifications(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort criteria (e.g., createdAt,desc)", example = "createdAt,desc")
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            Authentication authentication) {
        String userId = authentication.getName();
        CommonDTOs.NotificationListResponse notifications = notificationService.getNotifications(userId, page, size, sort);
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/notifications/{id}:read")
    @Operation(summary = "Mark Notification as Read", description = "Idempotent")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Authentication Failed"),
            @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    public ResponseEntity<Void> markAsRead(
            @Parameter(description = "Notification ID", example = "1")
            @PathVariable Long id,
            Authentication authentication) {
        String userId = authentication.getName();
        notificationService.markAsRead(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/notifications:read-all")
    @Operation(summary = "Mark All Notifications as Read", description = "Bulk read processing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Authentication Failed")
    })
    public ResponseEntity<Void> markAllAsRead(Authentication authentication) {
        String userId = authentication.getName();
        notificationService.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/devices")
    @Operation(summary = "Register Push Token", description = "Register push token for notifications")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Authentication Failed")
    })
    public ResponseEntity<CommonDTOs.DeviceResponse> registerDevice(
            @Parameter(description = "Device registration request")
            @Valid @RequestBody CommonDTOs.DeviceRegisterRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        CommonDTOs.DeviceResponse device = deviceService.registerDevice(userId, request);
        return ResponseEntity.ok(device);
    }
}