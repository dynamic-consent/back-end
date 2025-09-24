package com.example.dynamicconsent.api.controller;

import com.example.dynamicconsent.api.dto.CommonDTOs;
import com.example.dynamicconsent.service.SyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/sync")
@Tag(name = "Synchronization", description = "Mobile optimized synchronization API")
public class SyncController {

    @Autowired
    private SyncService syncService;

    @GetMapping
    @Operation(summary = "Synchronization", description = "Package home summary, notices, notifications, event deltas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Authentication Failed")
    })
    public ResponseEntity<CommonDTOs.SyncResponse> getSyncData(
            @Parameter(description = "Last sync timestamp (ISO8601)", example = "2024-01-01T00:00:00Z")
            @RequestParam Instant since,
            @Parameter(description = "Limit number of items to fetch", example = "50")
            @RequestParam(defaultValue = "50") int limit,
            Authentication authentication) {

        String userId = authentication.getName();
        CommonDTOs.SyncResponse syncResponse = syncService.getSyncData(userId, since, limit);
        return ResponseEntity.ok(syncResponse);
    }
}