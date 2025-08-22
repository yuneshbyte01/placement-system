package com.university.placementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO for returning job postings along with organization details.
 *
 * <p>Used when listing jobs to students, admins, or organizations,
 * providing essential information about the job posting.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobPostingDTO {

    // Unique ID of the job posting
    private Long id;

    // Title of the job
    private String title;

    // Description of the job role and responsibilities
    private String description;

    // Skills required for this job
    private String skillsRequired;

    // Eligibility criteria for applicants
    private String eligibilityCriteria;

    // Date and time when the job posting was created
    private LocalDateTime createdAt;

    // Name of the organization offering this job
    private String organizationName;
}
