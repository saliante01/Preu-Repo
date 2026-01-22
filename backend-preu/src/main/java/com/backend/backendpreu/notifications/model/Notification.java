package com.backend.backendpreu.notifications.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * Represents the base message of a system notification.
 * <p>
 * A notification defines the content and type of the message,
 * but does not define its recipients directly.
 * Recipients are resolved through NotificationTarget and
 * NotificationDelivery entities.
 */
@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    /**
     * Unique identifier of the notification.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Type of notification.
     * Defines the semantic meaning of the message.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    /**
     * Notification message content.
     */
    @Column(nullable = false, length = 500)
    private String content;

    /**
     * Timestamp indicating when the notification was created.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    /**
     * Automatically sets the creation timestamp
     * when the entity is first persisted.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }
}
