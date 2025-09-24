package com.example.dynamicconsent.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "data_shares")
public class DataShare extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "from_org_id")
    private Organization fromOrganization;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "to_org_id")
    private Organization toOrganization;

    @Column
    private Long volume; // Data volume

    @Column
    private String dataTypes; // Data types

    @Column
    private Boolean isActive = true;

    public Organization getFromOrganization() {
        return fromOrganization;
    }

    public void setFromOrganization(Organization fromOrganization) {
        this.fromOrganization = fromOrganization;
    }

    public Organization getToOrganization() {
        return toOrganization;
    }

    public void setToOrganization(Organization toOrganization) {
        this.toOrganization = toOrganization;
    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }

    public String getDataTypes() {
        return dataTypes;
    }

    public void setDataTypes(String dataTypes) {
        this.dataTypes = dataTypes;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
