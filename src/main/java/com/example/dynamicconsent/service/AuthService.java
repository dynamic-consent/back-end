package com.example.dynamicconsent.service;

import com.example.dynamicconsent.api.dto.CommonDTOs;
import com.example.dynamicconsent.domain.model.AuthSession;
import com.example.dynamicconsent.domain.model.SmsVerification;
import com.example.dynamicconsent.domain.model.User;
import com.example.dynamicconsent.domain.repository.AuthSessionRepository;
import com.example.dynamicconsent.domain.repository.SmsVerificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class AuthService {

    @Autowired
    private AuthSessionRepository authSessionRepository;
    
    @Autowired
    private SmsVerificationRepository smsVerificationRepository;
    
    @Autowired
    private UserService userService;

    // Simple Certificate Authentication Start
    public CommonDTOs.AuthStartResponse startSimpleCertAuth(String userId) {
        String txId = UUID.randomUUID().toString();
        Instant expireAt = Instant.now().plusSeconds(300); // 5 minutes
        
        return new CommonDTOs.AuthStartResponse(txId, expireAt);
    }

    // Simple Certificate Authentication Complete
    @Transactional
    public CommonDTOs.AuthCompleteResponse completeSimpleCertAuth(String txId, String userId) {
        // In practice, simple certificate verification logic is needed
        User user = userService.getOrCreateUser(userId);
        userService.updateLastLogin(userId);
        
        String accessToken = generateAccessToken();
        String refreshToken = generateRefreshToken();
        Instant expiresAt = Instant.now().plusSeconds(3600); // 1 hour
        
        // Session creation
        AuthSession session = new AuthSession();
        session.setUser(user);
        session.setAccessToken(accessToken);
        session.setRefreshToken(refreshToken);
        session.setExpiresAt(expiresAt);
        session.setLastUsedAt(Instant.now());
        session.setIsActive(true);
        authSessionRepository.save(session);
        
        return new CommonDTOs.AuthCompleteResponse(accessToken, refreshToken, userId, expiresAt);
    }

    // Public Certificate Challenge
    public CommonDTOs.AuthChallengeResponse publicCertChallenge(String userId) {
        String challenge = UUID.randomUUID().toString();
        Instant expireAt = Instant.now().plusSeconds(300); // 5 minutes
        
        return new CommonDTOs.AuthChallengeResponse(challenge, expireAt);
    }

    // Public Certificate Verification
    @Transactional
    public CommonDTOs.AuthVerifyResponse publicCertVerify(String challenge, String signedData, String userId) {
        // In practice, public certificate verification logic is needed
        User user = userService.getOrCreateUser(userId);
        userService.updateLastLogin(userId);
        
        String accessToken = generateAccessToken();
        String refreshToken = generateRefreshToken();
        Instant expiresAt = Instant.now().plusSeconds(3600); // 1 hour
        
        // Session creation
        AuthSession session = new AuthSession();
        session.setUser(user);
        session.setAccessToken(accessToken);
        session.setRefreshToken(refreshToken);
        session.setExpiresAt(expiresAt);
        session.setLastUsedAt(Instant.now());
        session.setIsActive(true);
        authSessionRepository.save(session);
        
        return new CommonDTOs.AuthVerifyResponse(accessToken, refreshToken, userId, expiresAt);
    }

    // SMS Authentication Start
    @Transactional
    public void startSmsVerification(String phoneNumber) {
        // Delete existing unverified SMS verifications
        smsVerificationRepository.deleteExpiredVerifications(Instant.now());
        
        String code = generateSmsCode();
        Instant expireAt = Instant.now().plusSeconds(180); // 3 minutes
        
        SmsVerification verification = new SmsVerification();
        verification.setPhoneNumber(phoneNumber);
        verification.setVerificationCode(code);
        verification.setExpiresAt(expireAt);
        verification.setAttemptCount(0);
        verification.setIsVerified(false);
        
        smsVerificationRepository.save(verification);
        
        // In practice, SMS sending logic is needed
        System.out.println("SMS Code for " + phoneNumber + ": " + code);
    }

    // SMS Authentication Verification
    @Transactional
    public CommonDTOs.AuthCompleteResponse verifySms(String phoneNumber, String code) {
        Optional<SmsVerification> verificationOpt = smsVerificationRepository
                .findByPhoneNumberAndIsVerifiedFalseOrderByCreatedAtDesc(phoneNumber);
        
        if (verificationOpt.isEmpty()) {
            throw new IllegalArgumentException("SMS verification not found");
        }
        
        SmsVerification verification = verificationOpt.get();
        
        if (verification.getAttemptCount() >= 5) {
            throw new IllegalArgumentException("Too many attempts");
        }
        
        if (verification.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("SMS code expired");
        }
        
        if (!verification.getVerificationCode().equals(code)) {
            smsVerificationRepository.incrementAttemptCount(phoneNumber, code);
            throw new IllegalArgumentException("Invalid SMS code");
        }
        
        // Authentication successful
        String userId = phoneNumber; // In practice, lookup user by phone number
        User user = userService.getOrCreateUser(userId);
        userService.updateLastLogin(userId);
        
        String accessToken = generateAccessToken();
        String refreshToken = generateRefreshToken();
        Instant expiresAt = Instant.now().plusSeconds(3600); // 1 hour
        
        // Session creation
        AuthSession session = new AuthSession();
        session.setUser(user);
        session.setAccessToken(accessToken);
        session.setRefreshToken(refreshToken);
        session.setExpiresAt(expiresAt);
        session.setLastUsedAt(Instant.now());
        session.setIsActive(true);
        authSessionRepository.save(session);
        
        // SMS verification completion processing
        smsVerificationRepository.verifySmsCode(phoneNumber, code, userId, Instant.now());
        
        return new CommonDTOs.AuthCompleteResponse(accessToken, refreshToken, userId, expiresAt);
    }

    // SMS Resend
    @Transactional
    public void resendSmsVerification(String phoneNumber) {
        startSmsVerification(phoneNumber);
    }

    // Token Refresh
    @Transactional
    public CommonDTOs.TokenRefreshResponse refreshToken(String refreshToken) {
        Optional<AuthSession> sessionOpt = authSessionRepository.findByRefreshTokenAndIsActiveTrue(refreshToken);
        
        if (sessionOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        
        AuthSession session = sessionOpt.get();
        
        if (session.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Refresh token expired");
        }
        
        // New token generation
        String newAccessToken = generateAccessToken();
        String newRefreshToken = generateRefreshToken();
        Instant expiresAt = Instant.now().plusSeconds(3600); // 1 hour
        
        session.setAccessToken(newAccessToken);
        session.setRefreshToken(newRefreshToken);
        session.setExpiresAt(expiresAt);
        session.setLastUsedAt(Instant.now());
        authSessionRepository.save(session);
        
        return new CommonDTOs.TokenRefreshResponse(newAccessToken, newRefreshToken, expiresAt);
    }

    // Logout
    @Transactional
    public void logout(String userId) {
        User user = userService.getOrCreateUser(userId);
        authSessionRepository.deactivateAllByUser(user);
    }

    // Current Session Check
    public CommonDTOs.AuthMeResponse getAuthMe(String userId) {
        User user = userService.getOrCreateUser(userId);
        
        return new CommonDTOs.AuthMeResponse(
            user.getUserId(),
            user.getDisplayName(),
            user.getLastLoginAt(),
            true
        );
    }

    private String generateAccessToken() {
        return "access_" + UUID.randomUUID().toString().replace("-", "");
    }

    private String generateRefreshToken() {
        return "refresh_" + UUID.randomUUID().toString().replace("-", "");
    }

    private String generateSmsCode() {
        return String.format("%06d", (int) (Math.random() * 1000000));
    }
}