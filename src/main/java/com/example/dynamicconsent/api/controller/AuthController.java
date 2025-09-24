package com.example.dynamicconsent.api.controller;

import com.example.dynamicconsent.api.dto.CommonDTOs;
import com.example.dynamicconsent.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication API (SMS, Simple/Public Certificate, Token)
 * - 이름/필드/구조 변경 없이 가독성만 개선
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Login/Authentication related API")
public class AuthController {

    @Autowired
    private AuthService authService;

    // ==============================
    // ===== Simple Certificate =====
    // ==============================

    @PostMapping("/cert/simple/start")
    @Operation(summary = "Start Simple Certificate Authentication", description = "Returns {txId, expireAt}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    public ResponseEntity<CommonDTOs.AuthStartResponse> startSimpleCertAuth(
            @Parameter(description = "User ID (X-UserId header)", example = "user123")
            @RequestHeader("X-UserId") String userId
    ) {
        return ResponseEntity.ok(authService.startSimpleCertAuth(userId));
    }

    @PostMapping("/cert/simple/complete")
    @Operation(summary = "Complete Simple Certificate Authentication", description = "Login completed, returns token and userId")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Authentication Failed")
    })
    public ResponseEntity<CommonDTOs.AuthCompleteResponse> completeSimpleCertAuth(
            @Parameter(description = "Transaction ID", example = "uuid-1234")
            @RequestParam String txId,
            @Parameter(description = "User ID (X-UserId header)", example = "user123")
            @RequestHeader("X-UserId") String userId
    ) {
        return ResponseEntity.ok(authService.completeSimpleCertAuth(txId, userId));
    }

    // =============================
    // ===== Public Certificate =====
    // =============================

    @PostMapping("/cert/public/challenge")
    @Operation(summary = "Public Certificate Challenge", description = "Returns {challenge, expireAt}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    public ResponseEntity<CommonDTOs.AuthChallengeResponse> publicCertChallenge(
            @Parameter(description = "User ID (X-UserId header)", example = "user123")
            @RequestHeader("X-UserId") String userId
    ) {
        return ResponseEntity.ok(authService.publicCertChallenge(userId));
    }

    @PostMapping("/cert/public/verify")
    @Operation(summary = "Public Certificate Verification", description = "Returns token and userId")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Verification Failed")
    })
    public ResponseEntity<CommonDTOs.AuthVerifyResponse> publicCertVerify(
            @Parameter(description = "Challenge string", example = "random-challenge-string")
            @RequestParam String challenge,
            @Parameter(description = "Signed data", example = "signed-data-from-client")
            @RequestParam String signedData,
            @Parameter(description = "User ID (X-UserId header)", example = "user123")
            @RequestHeader("X-UserId") String userId
    ) {
        return ResponseEntity.ok(authService.publicCertVerify(challenge, signedData, userId));
    }

    // =======================
    // ===== SMS Login =======
    // =======================

    @PostMapping("/sms/start")
    @Operation(summary = "Start SMS Authentication", description = "Send verification code")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    public ResponseEntity<Void> startSmsVerification(
            @Parameter(description = "Phone number", example = "01012345678")
            @Valid @RequestBody CommonDTOs.SmsStartRequest request
    ) {
        authService.startSmsVerification(request.phoneNumber());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/sms/verify")
    @Operation(summary = "Verify SMS Authentication", description = "Verify code and login")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Verification Failed")
    })
    public ResponseEntity<CommonDTOs.AuthCompleteResponse> verifySms(
            @Parameter(
                    description = "Phone number and verification code",
                    example = "{ \"phoneNumber\": \"01012345678\", \"code\": \"123456\" }"
            )
            @Valid @RequestBody CommonDTOs.SmsVerifyRequest request
    ) {
        return ResponseEntity.ok(authService.verifySms(request.phoneNumber(), request.code()));
    }

    @PostMapping("/sms/resend")
    @Operation(summary = "Resend SMS Verification Code", description = "Resend verification code")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "No ongoing verification")
    })
    public ResponseEntity<Void> resendSmsVerification(
            @Parameter(description = "Phone number", example = "{ \"phoneNumber\": \"01012345678\" }")
            @Valid @RequestBody CommonDTOs.SmsStartRequest request
    ) {
        authService.resendSmsVerification(request.phoneNumber());
        return ResponseEntity.noContent().build();
    }

    // ==========================
    // ===== Session/Token ======
    // ==========================

    @PostMapping("/token/refresh")
    @Operation(summary = "Refresh Access Token", description = "Refresh Access Token using Refresh Token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired Refresh Token")
    })
    public ResponseEntity<CommonDTOs.TokenRefreshResponse> refreshToken(
            @Parameter(description = "Refresh Token", example = "{ \"refreshToken\": \"eyJ...\" }")
            @Valid @RequestBody CommonDTOs.TokenRefreshRequest request
    ) {
        return ResponseEntity.ok(authService.refreshToken(request.refreshToken()));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Invalidate Refresh Token")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Invalid or already logged out Refresh Token")
    })
    public ResponseEntity<Void> logout(
            @Parameter(description = "Refresh Token", example = "{ \"refreshToken\": \"eyJ...\" }")
            @Valid @RequestBody CommonDTOs.TokenRefreshRequest request
    ) {
        authService.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @Operation(summary = "Check Current Session", description = "Check current logged in user information")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Authentication Failed")
    })
    public ResponseEntity<CommonDTOs.AuthMeResponse> getAuthMe(Authentication authentication) {
        String userId = authentication.getName();
        return ResponseEntity.ok(authService.getAuthMe(userId));
    }
}
