package com.university.placementsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request DTO for updating an existing student profile.
 *
 * <p>Used when a student modifies their academic details or skills.</p>
 */
@Data
public class StudentUpdateRequest {

    // Updated university name
    @NotBlank(message = "University is required")
    private String university;

    // Updated degree program
    @NotBlank(message = "Degree is required")
    private String degree;

    // Updated graduation year
    @NotNull(message = "Graduation year is required")
    private Integer graduationYear;

    // Updated list of skills
    private String skills;
}
