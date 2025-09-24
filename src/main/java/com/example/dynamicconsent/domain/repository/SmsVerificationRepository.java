package com.example.dynamicconsent.domain.repository;

import com.example.dynamicconsent.domain.model.SmsVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface SmsVerificationRepository extends JpaRepository<SmsVerification, Long> {

    Optional<SmsVerification> findByPhoneNumberAndIsVerifiedFalseOrderByCreatedAtDesc(String phoneNumber);

    @Modifying
    @Query("UPDATE SmsVerification s SET s.isVerified = true, s.userId = :userId " +
           "WHERE s.phoneNumber = :phoneNumber AND s.verificationCode = :code " +
           "AND s.expiresAt > :now AND s.attemptCount < 5")
    int verifySmsCode(@Param("phoneNumber") String phoneNumber,
                      @Param("code") String code,
                      @Param("userId") String userId,
                      @Param("now") Instant now);

    @Modifying
    @Query("UPDATE SmsVerification s SET s.attemptCount = s.attemptCount + 1 " +
           "WHERE s.phoneNumber = :phoneNumber AND s.verificationCode = :code")
    int incrementAttemptCount(@Param("phoneNumber") String phoneNumber,
                             @Param("code") String code);

    @Modifying
    @Query("DELETE FROM SmsVerification s WHERE s.expiresAt < :now")
    int deleteExpiredVerifications(@Param("now") Instant now);
}
