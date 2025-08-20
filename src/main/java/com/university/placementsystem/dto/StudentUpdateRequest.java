package com.university.placementsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO for updating a student profile.
 */
@Data
public class StudentUpdateRequest {

    @NotBlank(message = "University cannot be blank")
    private String university;

    @NotBlank(message = "Degree cannot be blank")
    private String degree;

    @NotNull(message = "Graduation year cannot be null")
    private Integer graduationYear;

    @NotBlank(message = "Skills cannot be blank")
    private String skills;
}
