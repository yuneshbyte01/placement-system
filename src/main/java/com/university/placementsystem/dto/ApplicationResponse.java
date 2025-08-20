package com.university.placementsystem.dto;

import com.university.placementsystem.entity.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ApplicationResponse {

    private Long jobId;
    private String jobTitle;
    private String organizationName;
    private ApplicationStatus status;
    private LocalDateTime appliedDate;
}
