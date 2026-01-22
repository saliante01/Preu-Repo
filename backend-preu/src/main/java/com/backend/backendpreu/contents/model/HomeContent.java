package com.backend.backendpreu.contents.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * Represents content displayed on the institutional home page.
 * <p>
 * This content is visible to all users and may include
 * announcements, informational text, or external links.
 */
@Entity
@Table(name = "home_contents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeContent {

    /**
     * Unique identifier of the home content.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Title of the content.
     */
    @Column(nullable = false)
    private String title;

    /**
     * Main text displayed on the home page.
     */
    @Column(name = "content_text", columnDefinition = "TEXT", nullable = false)
    private String contentText;

    /**
     * Optional external link associated with the content.
     */
    private String link;

    /**
     * Indicates whether the content is visible on the home page.
     */
    @Column(nullable = false)
    private boolean visible;

    /**
     * Timestamp indicating when the content was created.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    /**
     * Timestamp indicating the last update of the content.
     */
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
