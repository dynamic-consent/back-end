package com.example.dynamicconsent.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "organizations")
public class Organization extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String orgId;

    @Column(nullable = false)
    private String name;

    @Column
    private String category;

    @Column(length = 1000)
    private String description;

    @Column
    private String website;

    @Column
    private String contactEmail;

    @Column
    private String contactPhone;

    @Column
    private Boolean ismsCertified = false; // ISMS-P certification status

    @Column
    private String policyUrl; // Privacy policy URL

    @Column
    private java.time.Instant policyLastUpdated; // Policy last updated date

    @Column
    private Boolean isActive = true;

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public Boolean getIsmsCertified() {
        return ismsCertified;
    }

    public void setIsmsCertified(Boolean ismsCertified) {
        this.ismsCertified = ismsCertified;
    }

    public String getPolicyUrl() {
        return policyUrl;
    }

    public void setPolicyUrl(String policyUrl) {
        this.policyUrl = policyUrl;
    }

    public java.time.Instant getPolicyLastUpdated() {
        return policyLastUpdated;
    }

    public void setPolicyLastUpdated(java.time.Instant policyLastUpdated) {
        this.policyLastUpdated = policyLastUpdated;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
