package com.example.dynamicconsent.api.controller;

import com.example.dynamicconsent.api.dto.CommonDTOs;
import com.example.dynamicconsent.service.HomeService;
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
import java.util.List;

@RestController
@RequestMapping("/api/v1/home")
@Tag(name = "Home", description = "Home related API")
public class HomeController {

    @Autowired
    private HomeService homeService;

    @GetMapping("/summary")
    @Operation(summary = "Home Summary", description = "Include risk summary (score, grade), notice preview, recent consent changes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Authentication Failed")
    })
    public ResponseEntity<CommonDTOs.HomeSummaryResponse> getHomeSummary(Authentication authentication) {
        String userId = authentication.getName();
        CommonDTOs.HomeSummaryResponse summary = homeService.getHomeSummary(userId);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/timeline")
    @Operation(summary = "Home Timeline", description = "Support cursor-based pagination (after, limit)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Authentication Failed")
    })
    public ResponseEntity<List<CommonDTOs.TimelineEventResponse>> getTimeline(
            @Parameter(description = "Cursor timestamp (ISO8601)", example = "2024-01-01T00:00:00Z")
            @RequestParam(required = false) Instant after,
            @Parameter(description = "Number of items to return", example = "20")
            @RequestParam(defaultValue = "20") int limit,
            Authentication authentication) {
        String userId = authentication.getName();
        String afterStr = after != null ? after.toString() : null;
        List<CommonDTOs.TimelineEventResponse> timeline = homeService.getTimeline(userId, afterStr, limit);
        return ResponseEntity.ok(timeline);
    }
}