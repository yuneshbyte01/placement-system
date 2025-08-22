package com.university.placementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for representing an organization's profile in API responses.
 *
 * <p>This is a lightweight representation of the organization entity,
 * typically used when sending data to the client.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationDTO {

    // Name of the company
    private String companyName;

    // Industry the organization belongs to
    private String industry;

    // Location of the organization
    private String location;

    // Short description about the organization
    private String description;

    // Whether admin approves the organization
    private boolean approved;

    // Contact email associated with the organization
    private String email;
}
