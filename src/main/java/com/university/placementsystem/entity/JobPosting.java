package com.university.placementsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Represents a job posting created by an organization.
 */
@Entity
@Table(name = "job_postings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPosting {

    /** Primary key, auto-generated */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Job title */
    @Column(nullable = false, length = 200)
    private String title;

    /** Job description */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    /** Comma-separated list of required skills */
    @Column(name = "skills_required", length = 500)
    private String skillsRequired;

    /** Eligibility criteria for applicants */
    @Column(name = "eligibility_criteria", length = 500)
    private String eligibilityCriteria;

    /** Timestamp of creation, auto-set */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Many-to-One relationship to the organization that posted the job */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    /** Pre-persist hook to set createdAt */
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
