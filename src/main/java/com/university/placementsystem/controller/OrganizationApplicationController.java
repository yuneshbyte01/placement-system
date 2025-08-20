package com.university.placementsystem.controller;

import com.university.placementsystem.dto.UserDTO;
import com.university.placementsystem.entity.*;
import com.university.placementsystem.repository.ApplicationRepository;
import com.university.placementsystem.repository.JobPostingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/**
 * Controller for organizations to manage job applications.
 * Task 24: Shortlist / Select / Reject applicants
 */
@RestController
@RequestMapping("/api/organization")
@RequiredArgsConstructor
public class OrganizationApplicationController {

    private final ApplicationRepository applicationRepository;
    private final JobPostingRepository jobPostingRepository;

    /**
     * Simple test endpoint to verify the controller is working.
     */
    @GetMapping("/test-application")
    public ResponseEntity<Map<String, String>> testEndpoint() {
        return ResponseEntity.ok(Map.of(
                "message", "StudentApplicationController is working!",
                "status", "success"
        ));
    }

    // ------------------- Shortlist Application -------------------

    @PutMapping("/jobs/{jobId}/applications/{applicationId}/shortlist")
    public ResponseEntity<?> shortlistApplicant(Authentication authentication,
                                                @PathVariable Long jobId,
                                                @PathVariable Long applicationId) {
        return updateApplicationStatus(authentication, jobId, applicationId, ApplicationStatus.SHORTLISTED);
    }

    // ------------------- Select Application -------------------

    @PutMapping("/jobs/{jobId}/applications/{applicationId}/select")
    public ResponseEntity<?> selectApplicant(Authentication authentication,
                                             @PathVariable Long jobId,
                                             @PathVariable Long applicationId) {
        return updateApplicationStatus(authentication, jobId, applicationId, ApplicationStatus.SELECTED);
    }

    // ------------------- Reject Application -------------------

    @PutMapping("/jobs/{jobId}/applications/{applicationId}/reject")
    public ResponseEntity<?> rejectApplicant(Authentication authentication,
                                             @PathVariable Long jobId,
                                             @PathVariable Long applicationId) {
        return updateApplicationStatus(authentication, jobId, applicationId, ApplicationStatus.REJECTED);
    }

    // ------------------- Private Helper Method -------------------

    /**
     * Updates application status with all validation checks.
     */
    private ResponseEntity<?> updateApplicationStatus(Authentication authentication,
                                                      Long jobId,
                                                      Long applicationId,
                                                      ApplicationStatus newStatus) {

        // Check an organization role
        UserDTO orgUser = (UserDTO) authentication.getPrincipal();
        if (orgUser.getRole() != UserRole.ORGANIZATION) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Access denied: ORG role required"));
        }

        // Fetch job and verify ownership
        JobPosting job = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job posting not found"));

        // Correct ownership check via organization profile → user
        if (!job.getOrganization().getUser().getId().equals(orgUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "You do not own this job posting"));
        }

        // Fetch application
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));

        // Verify the application belongs to the job
        if (!application.getJobPosting().getId().equals(jobId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Application does not belong to this job"));
        }

        // Prevent status override if already finalized
        if (application.getStatus() == ApplicationStatus.SELECTED ||
                application.getStatus() == ApplicationStatus.REJECTED) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Cannot update application already finalized"));
        }

        // Update status
        application.setStatus(newStatus);
        applicationRepository.save(application);

        return ResponseEntity.ok(Map.of(
                "message", "Application status updated",
                "applicationId", application.getId(),
                "newStatus", newStatus.toString()
        ));
    }
}
