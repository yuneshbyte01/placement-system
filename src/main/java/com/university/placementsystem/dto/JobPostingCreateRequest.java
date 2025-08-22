package com.university.placementsystem.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for creating a new job posting.
 *
 * <p>Contains the details required when an organization posts a job.</p>
 */
@Data
public class JobPostingCreateRequest {

    // Title of the job posting
    @NotBlank(message = "Job title cannot be blank")
    private String title;

    // Detailed description of the job
    @NotBlank(message = "Job description cannot be blank")
    private String description;

    // Skills required for the job
    @NotBlank(message = "Skills required cannot be blank")
    private String skillsRequired;

    // Eligibility criteria (optional field)
    private String eligibilityCriteria;
}
