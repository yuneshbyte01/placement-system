package com.university.placementsystem.dto;

import com.university.placementsystem.entity.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Response DTO representing a student's job application details.
 *
 * <p>Returned when listing all job applications of a student.</p>
 */
@Data
@AllArgsConstructor
public class ApplicationResponse {

    // ID of the applied job posting
    private Long jobId;

    // Title of the job
    private String jobTitle;

    // Organization offering the job
    private String organizationName;

    // Current status of the application (e.g., APPLIED, REVIEWED)
    private ApplicationStatus status;

    // Date and time when the application was submitted
    private LocalDateTime appliedAt;
}
