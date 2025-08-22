package com.university.placementsystem.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for updating an organization's profile.
 *
 * <p>Used when an organization edits its company details such as
 * name, industry, location, or description.</p>
 */
@Data
public class OrganizationUpdateRequest {

    // Name of the company
    @NotBlank(message = "Company name cannot be blank")
    private String companyName;

    // Industry the organization belongs to
    @NotBlank(message = "Industry cannot be blank")
    private String industry;

    // Location of the organization
    @NotBlank(message = "Location cannot be blank")
    private String location;

    // Short description about the organization (optional)
    private String description;
}
