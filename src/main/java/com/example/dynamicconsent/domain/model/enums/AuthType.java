package com.example.dynamicconsent.domain.model.enums;

public enum AuthType {
    SIMPLE_CERT("Simple Certificate"),
    PUBLIC_CERT("Public Certificate"),
    SMS("SMS"),
    PASSWORD("Password");

    private final String description;

    AuthType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}