package com.example.dynamicconsent.domain.model;

import com.example.dynamicconsent.domain.model.enums.ConsentEventType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "consent_events")
public class ConsentEvent extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "consent_id")
    private Consent consent;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConsentEventType type;

    @Column(length = 1000)
    private String details;

    @Column(length = 2000)
    private String metadata; // JSON format additional information

    @Column(length = 2000)
    private String scopeDiff; // Scope change details

    @Column
    private String organizationName;

    public Consent getConsent() {
        return consent;
    }

    public void setConsent(Consent consent) {
        this.consent = consent;
    }

    public ConsentEventType getType() {
        return type;
    }

    public void setType(ConsentEventType type) {
        this.type = type;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getScopeDiff() {
        return scopeDiff;
    }

    public void setScopeDiff(String scopeDiff) {
        this.scopeDiff = scopeDiff;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
}
