package com.backend.backendpreu.payments.models;

import com.backend.backendpreu.academicPeriod.model.AcademicPeriod;
import com.backend.backendpreu.users.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Represents a payment record associated with a student
 * and a specific academic period (course execution).
 *
 * A payment is NOT global to the user, but linked to
 * the course period the student is enrolled in.
 */
@Entity
@Table(
        name = "payment_records",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "academic_period_id"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Student associated with the payment.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Academic period (course + start/end dates)
     * for which the payment applies.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "academic_period_id", nullable = false)
    private AcademicPeriod academicPeriod;

    /**
     * Informational payment status.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    /**
     * Payment due date.
     */
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    /**
     * Date when the payment was completed (if applicable).
     */
    @Column(name = "paid_at")
    private LocalDate paidAt;

    /**
     * Record creation timestamp.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }
}
