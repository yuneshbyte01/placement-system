package com.university.placementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Lightweight DTO for returning job postings in API responses.
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
}
