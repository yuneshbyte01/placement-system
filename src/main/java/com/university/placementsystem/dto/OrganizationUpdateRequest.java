package com.university.placementsystem.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for updating an organization profile.
 */
@Data
public class OrganizationUpdateRequest {

    @NotBlank(message = "Company name cannot be blank")
    private String companyName;

    @NotBlank(message = "Industry cannot be blank")
    private String industry;

    @NotBlank(message = "Location cannot be blank")
    private String location;

    private String description; // optional
}
