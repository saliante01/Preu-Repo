package com.backend.backendpreu.evaluations.model;

import com.backend.backendpreu.users.model.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * Represents the grade obtained by a student in a specific evaluation.
 * <p>
 * Each student can have only one grade per evaluation.
 */
@Entity
@Table(
        name = "grades",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"evaluation_id", "user_id"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grade {

    /**
     * Unique identifier of the grade record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Evaluation associated with this grade.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evaluation_id", nullable = false)
    private Evaluation evaluation;

    /**
     * Student who received the grade.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Numeric grade obtained by the student.
     * The grading scale is defined by business rules.
     */
    @Column(nullable = false)
    private Double grade;

    /**
     * Optional feedback provided by the professor.
     */
    @Column(length = 500)
    private String feedback;
}
