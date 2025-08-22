package com.university.placementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for returning job application information to administrators.
 *
 * <p>Encapsulates key details about a student's application,
 * including the student's identity, job information, and current application status.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDTO {

    // Unique ID of the application
    private Long id;

    // Full name of the student who applied
    private String studentName;

    // Email of the student
    private String studentEmail;

    // Title of the job applied for
    private String jobTitle;

    // Name of the organization offering the job
    private String organizationName;

    // Current status of the application (e.g., PENDING, ACCEPTED, REJECTED)
    private String status;
}
