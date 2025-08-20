package com.university.placementsystem.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for creating a job posting.
 */
@Data
public class JobPostingCreateRequest {

    @NotBlank(message = "Job title cannot be blank")
    private String title;

    @NotBlank(message = "Job description cannot be blank")
    private String description;

    @NotBlank(message = "Skills required cannot be blank")
    private String skillsRequired;

    private String eligibilityCriteria;
}
