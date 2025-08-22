package com.university.placementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Response DTO representing student profile details.
 *
 * <p>Used when retrieving a student profile.</p>
 */
@Data
@AllArgsConstructor
public class StudentDTO {

    // University name
    private String university;

    // Degree program
    private String degree;

    // Graduation year
    private Integer graduationYear;

    // List of skills
    private String skills;

    // Path to uploaded resume (if available)
    private String resumePath;

    // Email of the student (taken from a linked user account)
    private String email;
}
