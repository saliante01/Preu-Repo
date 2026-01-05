package com.backend.backendpreu.notifications.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Defines the logical target of a notification.
 * <p>
 * A notification can target either a single user
 * or an entire academic period.
 */
@Entity
@Table(name = "notification_targets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationTarget {

    /**
     * Unique identifier of the notification target.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Associated notification.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;

    /**
     * Type of target (USER or ACADEMIC_PERIOD).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private NotificationTargetType targetType;

    /**
     * Identifier of the target entity.
     * Represents either a User ID or an AcademicPeriod ID,
     * depending on the targetType.
     */
    @Column(name = "target_id", nullable = false)
    private Long targetId;
}
