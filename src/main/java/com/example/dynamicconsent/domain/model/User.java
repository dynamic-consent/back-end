package com.example.dynamicconsent.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String userId; // X-UserId header value

    @Column(nullable = false)
    private String displayName;

    @Column
    private String email;

    @Column
    private String phoneNumber;

    @Column
    private Instant birthDate;

    @Column
    private Instant lastConsentChangeAt;

    @Column
    private Instant lastLoginAt;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Instant getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Instant birthDate) {
        this.birthDate = birthDate;
    }

    public Instant getLastConsentChangeAt() {
        return lastConsentChangeAt;
    }

    public void setLastConsentChangeAt(Instant lastConsentChangeAt) {
        this.lastConsentChangeAt = lastConsentChangeAt;
    }

    public Instant getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Instant lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }
}
