package com.backend.backendpreu.notifications.model;

import com.backend.backendpreu.users.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * Represents the effective delivery of a notification to a specific user.
 * <p>
 * This entity tracks whether the notification was read
 * and whether it was sent by email.
 */
@Entity
@Table(
        name = "notification_deliveries",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"notification_id", "user_id"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDelivery {

    /**
     * Unique identifier of the delivery record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Notification being delivered.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;

    /**
     * User receiving the notification.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Timestamp indicating when the notification was read.
     * Null if the notification has not been read yet.
     */
    @Column(name = "read_at")
    private OffsetDateTime readAt;

    /**
     * Indicates whether the notification was sent via email.
     */
    @Column(name = "email_sent", nullable = false)
    private Boolean emailSent;
}
