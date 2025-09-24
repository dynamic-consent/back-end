package com.example.dynamicconsent.domain.model;

import com.example.dynamicconsent.domain.model.enums.NoticeCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "notices")
public class Notice extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column
    private NoticeCategory category;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column
    private Integer priority = 0; // Display priority (higher number = higher priority)

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public NoticeCategory getCategory() {
        return category;
    }

    public void setCategory(NoticeCategory category) {
        this.category = category;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
