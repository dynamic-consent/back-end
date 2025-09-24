package com.example.dynamicconsent.domain.model;

import com.example.dynamicconsent.domain.model.enums.ConsentStatus;
import com.example.dynamicconsent.domain.model.enums.DataSensitivity;
import com.example.dynamicconsent.domain.model.enums.ConsentScope;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.Set;

@Entity
@Table(name = "consents")
public class Consent extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConsentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DataSensitivity sensitivity;

    @ElementCollection(targetClass = ConsentScope.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "consent_scopes", joinColumns = @JoinColumn(name = "consent_id"))
    @Column(name = "scope")
    private Set<ConsentScope> scopes;

    @Column(nullable = false)
    private Instant validFrom;

    @Column
    private Instant validUntil;

    @Column
    private Instant lastUsedAt;

    @Column
    private Integer sharedOrganizationCount = 0;

    @Column(length = 1000)
    private String purpose;

    @Column
    private String revokeReason; // Revocation reason

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public ConsentStatus getStatus() {
        return status;
    }

    public void setStatus(ConsentStatus status) {
        this.status = status;
    }

    public DataSensitivity getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(DataSensitivity sensitivity) {
        this.sensitivity = sensitivity;
    }

    public Set<ConsentScope> getScopes() {
        return scopes;
    }

    public void setScopes(Set<ConsentScope> scopes) {
        this.scopes = scopes;
    }

    public Instant getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Instant validFrom) {
        this.validFrom = validFrom;
    }

    public Instant getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Instant validUntil) {
        this.validUntil = validUntil;
    }

    public Instant getLastUsedAt() {
        return lastUsedAt;
    }

    public void setLastUsedAt(Instant lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }

    public Integer getSharedOrganizationCount() {
        return sharedOrganizationCount;
    }

    public void setSharedOrganizationCount(Integer sharedOrganizationCount) {
        this.sharedOrganizationCount = sharedOrganizationCount;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getRevokeReason() {
        return revokeReason;
    }

    public void setRevokeReason(String revokeReason) {
        this.revokeReason = revokeReason;
    }
}
