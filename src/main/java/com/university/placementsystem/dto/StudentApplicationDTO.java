package com.university.placementsystem.dto;

import com.university.placementsystem.entity.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for returning a student's job application details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentApplicationDTO {

    private Long applicationId;          // ID of the application
    private String jobTitle;             // Title of the job
    private String jobDescription;       // Description of the job
    private String skillsRequired;       // Skills required for the job
    private String eligibilityCriteria;  // Eligibility criteria of the job
    private ApplicationStatus status;    // Current status of the application
    private LocalDateTime appliedAt;     // Timestamp when the application was submitted
}
