package com.university.placementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO for returning job postings along with the organization’s name.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobPostingDTO {
    private Long id;
    private String title;
    private String description;
    private String skillsRequired;
    private String eligibilityCriteria;
    private LocalDateTime createdAt;
    private String organizationName;
}
