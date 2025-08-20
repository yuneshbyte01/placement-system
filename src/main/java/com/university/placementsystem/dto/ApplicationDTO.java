package com.university.placementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for returning job application info to admin.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDTO {
    private Long id;
    private String studentName;
    private String studentEmail;
    private String jobTitle;
    private String organizationName;
    private String status; // PENDING, ACCEPTED, REJECTED etc.
}
