package com.university.placementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Organization profile.
 * Lightweight version for API responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationDTO {
    private String companyName;
    private String industry;
    private String location;
    private String description;
    private boolean approved;
    private String email;
}
