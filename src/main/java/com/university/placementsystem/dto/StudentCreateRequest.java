package com.university.placementsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request DTO for creating a new student profile.
 *
 * <p>Used when a student first sets up their profile.</p>
 */
@Data
public class StudentCreateRequest {

    // Name of the university the student is enrolled in
    @NotBlank(message = "University is required")
    private String university;

    // Degree program of the student (e.g., B.Sc. CS, MBA)
    @NotBlank(message = "Degree is required")
    private String degree;

    // Graduation year of the student
    @NotNull(message = "Graduation year is required")
    private Integer graduationYear;

    // List of skills (e.g., Java, Spring Boot, SQL)
    private String skills;
}
