package com.example.dynamicconsent.api.controller;

import com.example.dynamicconsent.api.dto.CommonDTOs;
import com.example.dynamicconsent.service.UserService;
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
@Tag(name = "User", description = "User related API")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    @Operation(summary = "User Profile", description = "Add preferences, lastLoginAt fields")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Authentication Failed")
    })
    public ResponseEntity<CommonDTOs.UserProfileResponse> getUserProfile(Authentication authentication) {
        String userId = authentication.getName();
        CommonDTOs.UserProfileResponse profile = userService.getUserProfile(userId);
        return ResponseEntity.ok(profile);
    }

    @PatchMapping("/me")
    @Operation(summary = "Update User Profile", description = "Update name, birthdate, email, phone number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Authentication Failed")
    })
    public ResponseEntity<CommonDTOs.UserProfileResponse> updateUserProfile(
            @Parameter(description = "User update request")
            @Valid @RequestBody CommonDTOs.UserUpdateRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        CommonDTOs.UserProfileResponse profile = userService.updateUserProfile(userId, request);
        return ResponseEntity.ok(profile);
    }

    @PatchMapping("/me/preferences")
    @Operation(summary = "Update User Preferences", description = "Change environment settings (notifications, theme, risk threshold)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Authentication Failed")
    })
    public ResponseEntity<CommonDTOs.UserPreferencesResponse> updateUserPreferences(
            @Parameter(description = "User preferences update request")
            @Valid @RequestBody CommonDTOs.UserPreferencesUpdateRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        CommonDTOs.UserPreferencesResponse preferences = userService.updateUserPreferences(userId, request);
        return ResponseEntity.ok(preferences);
    }
}