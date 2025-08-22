package com.university.placementsystem.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for creating an organization profile.
 *
 * <p>Contains the required details that an organization
 * must provide during profile creation.</p>
 */
@Data
public class OrganizationCreateRequest {

    // Name of the company (required)
    @NotBlank(message = "Company name cannot be blank")
    private String companyName;

    // Industry type of the organization (required)
    @NotBlank(message = "Industry cannot be blank")
    private String industry;

    // Location of the organization (required)
    @NotBlank(message = "Location cannot be blank")
    private String location;

    // Description of the organization (optional)
    private String description;
}
