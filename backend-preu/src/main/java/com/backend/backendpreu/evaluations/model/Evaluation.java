package com.backend.backendpreu.evaluations.model;

import com.backend.backendpreu.academicPeriod.model.AcademicPeriod;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Represents an academic evaluation within a specific academic period.
 * <p>
 * Evaluations may correspond to exams, quizzes, forms, or mini-tests
 * and contribute to the final assessment of a course.
 */
@Entity
@Table(name = "evaluations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evaluation {

    /**
     * Unique identifier of the evaluation.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Academic period to which this evaluation belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "academic_period_id", nullable = false)
    private AcademicPeriod academicPeriod;

    /**
     * Title or name of the evaluation.
     */
    @Column(nullable = false, length = 150)
    private String title;

    /**
     * Percentage weight of the evaluation.
     * Example: 20 represents 20% of the final grade.
     */
    @Column(nullable = false)
    private Integer weight;

    /**
     * Deadline for completing the evaluation.
     */
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;
}
