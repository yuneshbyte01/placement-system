package com.university.placementsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Represents a student's application to a specific job posting.
 */
@Entity
@Table(name = "applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {

    /** Primary key, auto-generated */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Student who applied */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    /** Job posting applied to */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "job_posting_id", nullable = false)
    private JobPosting jobPosting;

    /** Status of the application */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    /** Timestamp when the application was created */
    @Column(name = "applied_at", nullable = false, updatable = false)
    private LocalDateTime appliedAt;

    /**
     * Automatically sets appliedAt and default status before persisting.
     */
    @PrePersist
    protected void onCreate() {
        this.appliedAt = LocalDateTime.now();
        if (status == null) {
            this.status = ApplicationStatus.APPLIED;
        }
    }
}
