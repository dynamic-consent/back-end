package com.example.dynamicconsent.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_preferences")
public class UserPreferences extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private String theme = "light"; // light, dark

    @Column
    private Boolean emailNotifications = true;

    @Column
    private Boolean pushNotifications = true;

    @Column
    private Boolean smsNotifications = false;

    @Column
    private Double riskThreshold = 0.7; // Risk threshold

    @Column
    private String language = "ko"; // Language setting

    @Column
    private Boolean autoSync = true; // Auto synchronization

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public Boolean getEmailNotifications() {
        return emailNotifications;
    }

    public void setEmailNotifications(Boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
    }

    public Boolean getPushNotifications() {
        return pushNotifications;
    }

    public void setPushNotifications(Boolean pushNotifications) {
        this.pushNotifications = pushNotifications;
    }

    public Boolean getSmsNotifications() {
        return smsNotifications;
    }

    public void setSmsNotifications(Boolean smsNotifications) {
        this.smsNotifications = smsNotifications;
    }

    public Double getRiskThreshold() {
        return riskThreshold;
    }

    public void setRiskThreshold(Double riskThreshold) {
        this.riskThreshold = riskThreshold;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Boolean getAutoSync() {
        return autoSync;
    }

    public void setAutoSync(Boolean autoSync) {
        this.autoSync = autoSync;
    }
}
