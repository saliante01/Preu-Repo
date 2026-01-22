package com.backend.backendpreu.attendance.model;

import com.backend.backendpreu.meetings.model.Meeting;
import com.backend.backendpreu.users.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * Represents the attendance record of a user for a specific academic meeting.
 *
 * <p>This entity tracks whether a {@link User} was present or absent
 * in a given {@link Meeting}.</p>
 *
 * <p>Only one attendance record per user and meeting is allowed.</p>
 */
@Entity
@Table(
        name = "attendance",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"meeting_id", "user_id"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Academic meeting associated with this attendance record.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;

    /**
     * User whose attendance is being recorded.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Indicates whether the user was present or absent.
     */
    @Column(nullable = false)
    private Boolean present;

    /**
     * Timestamp when the attendance was recorded.
     */
    @Column(name = "recorded_at", nullable = false, updatable = false)
    private OffsetDateTime recordedAt;

    @PrePersist
    protected void onCreate() {
        recordedAt = OffsetDateTime.now();
    }
}
