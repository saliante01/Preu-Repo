package com.backend.backendpreu.contents.model;

import com.backend.backendpreu.academicPeriod.model.AcademicPeriod;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * Represents academic content associated with an academic period.
 * <p>
 * Content may include documents, videos, links, or tasks
 * defined by the professor.
 */
@Entity
@Table(name = "contents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Content {

    /**
     * Unique identifier of the content.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Academic period to which the content belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "academic_period_id", nullable = false)
    private AcademicPeriod academicPeriod;

    /**
     * Title of the content.
     */
    @Column(nullable = false, length = 150)
    private String title;

    /**
     * Type of content.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentType type;

    /**
     * URL or external reference to the content.
     */
    @Column(nullable = false, length = 500)
    private String url;

    /**
     * Timestamp indicating when the content was created.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }
}
