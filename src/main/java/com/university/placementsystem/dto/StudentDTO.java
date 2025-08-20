package com.university.placementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Student profile.
 * Lightweight version for API responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {
    private String university;
    private String degree;
    private Integer graduationYear;
    private String skills;
    private String resumePath;
    private String email;
}
