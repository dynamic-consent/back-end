package com.example.dynamicconsent.api.controller;

import com.example.dynamicconsent.api.dto.CommonDTOs;
import com.example.dynamicconsent.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Notice", description = "Notice related API")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @GetMapping("/notices:preview")
    @Operation(summary = "Notice Preview", description = "Latest notice 3 items preview")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success")
    })
    public ResponseEntity<List<CommonDTOs.NoticePreviewResponse>> getNoticePreviews(
            @Parameter(description = "Preview count", example = "3")
            @RequestParam(defaultValue = "3") int limit) {
        List<CommonDTOs.NoticePreviewResponse> previews = noticeService.getNoticePreviews(limit);
        return ResponseEntity.ok(previews);
    }

    @GetMapping("/notices")
    @Operation(summary = "Notice List", description = "Notice list with pagination and sorting")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success")
    })
    public ResponseEntity<Page<CommonDTOs.NoticeResponse>> getNotices(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort criteria (e.g., createdAt,desc)", example = "createdAt,desc")
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        Page<CommonDTOs.NoticeResponse> notices = noticeService.getNotices(page, size, sort);
        return ResponseEntity.ok(notices);
    }

    @GetMapping("/notices/{noticeId}")
    @Operation(summary = "Notice Detail", description = "Specific notice detail content")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Notice not found")
    })
    public ResponseEntity<CommonDTOs.NoticeResponse> getNoticeDetail(
            @Parameter(description = "Notice ID", example = "1")
            @PathVariable Long noticeId) {
        return noticeService.getNoticeById(noticeId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}