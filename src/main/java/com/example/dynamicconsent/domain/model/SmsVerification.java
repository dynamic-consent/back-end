package com.example.dynamicconsent.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "sms_verifications")
public class SmsVerification extends BaseEntity {

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String verificationCode;

    @Column(nullable = false)
    private java.time.Instant expiresAt;

    @Column(nullable = false)
    private Integer attemptCount = 0;

    @Column(nullable = false)
    private Boolean isVerified = false;

    @Column
    private String userId; // Authentication successful user ID

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public java.time.Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(java.time.Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Integer getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(Integer attemptCount) {
        this.attemptCount = attemptCount;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
