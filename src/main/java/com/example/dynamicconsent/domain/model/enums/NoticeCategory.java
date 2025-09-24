package com.example.dynamicconsent.domain.model.enums;

public enum NoticeCategory {
    GENERAL("General"),
    SECURITY("Security"),
    POLICY("Policy"),
    MAINTENANCE("Maintenance"),
    UPDATE("Update"),
    URGENT("Urgent");

    private final String description;

    NoticeCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}