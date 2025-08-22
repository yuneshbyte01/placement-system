package com.university.placementsystem.controller;

import com.university.placementsystem.dto.UserDTO;
import com.university.placementsystem.entity.ApplicationStatus;
import com.university.placementsystem.service.OrganizationApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/**
 * REST controller for organizations to manage application statuses
 * (shortlist, select, reject) for their job postings.
 *
 * <p>Endpoints require authentication and organization ownership
 * of the target job posting.</p>
 */
@RestController
@RequestMapping("/api/organization")
@RequiredArgsConstructor
public class OrganizationApplicationController {

    // Business service for status updates
    private final OrganizationApplicationService applicationService;

    // ---- Messages / constants ----
    private static final String MSG_TEST_OK   = "StudentApplicationController is working!"; // keep existing text
    private static final String MSG_STATUS_OK = "success";
    private static final String MSG_INTERNAL  = "Internal server error";

    /**
     * Simple test endpoint to verify the controller is reachable.
     *
     * @return 200 with a small JSON payload indicating success
     */
    @GetMapping("/test-application")
    public ResponseEntity<?> testEndpoint() {
        try {
            return ResponseEntity.ok(Map.of(
                    "message", MSG_TEST_OK,
                    "status", MSG_STATUS_OK
            ));
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MSG_INTERNAL);
        }
    }

    /**
     * Shortlists an application for a specific job posting owned by the
     * authenticated organization.
     *
     * @param authentication Spring Security authentication (principal is {@link UserDTO})
     * @param jobId          ID of the job posting
     * @param applicationId  ID of the application to update
     * @return 200 with a confirmation map; error status if validation fails
     */
    @PutMapping("/jobs/{jobId}/applications/{applicationId}/shortlist")
    public ResponseEntity<?> shortlistApplicant(Authentication authentication,
                                                @PathVariable Long jobId,
                                                @PathVariable Long applicationId) {
        try {
            UserDTO orgUser = (UserDTO) authentication.getPrincipal();
            return ResponseEntity.ok(
                    applicationService.updateApplicationStatus(orgUser, jobId, applicationId, ApplicationStatus.SHORTLISTED)
            );
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MSG_INTERNAL);
        }
    }

    /**
     * Marks an application as selected for a specific job posting owned by the
     * authenticated organization.
     *
     * @param authentication Spring Security authentication (principal is {@link UserDTO})
     * @param jobId          ID of the job posting
     * @param applicationId  ID of the application to update
     * @return 200 with a confirmation map; error status if validation fails
     */
    @PutMapping("/jobs/{jobId}/applications/{applicationId}/select")
    public ResponseEntity<?> selectApplicant(Authentication authentication,
                                             @PathVariable Long jobId,
                                             @PathVariable Long applicationId) {
        try {
            UserDTO orgUser = (UserDTO) authentication.getPrincipal();
            return ResponseEntity.ok(
                    applicationService.updateApplicationStatus(orgUser, jobId, applicationId, ApplicationStatus.SELECTED)
            );
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MSG_INTERNAL);
        }
    }

    /**
     * Rejects an application for a specific job posting owned by the
     * authenticated organization.
     *
     * @param authentication Spring Security authentication (principal is {@link UserDTO})
     * @param jobId          ID of the job posting
     * @param applicationId  ID of the application to update
     * @return 200 with a confirmation map; error status if validation fails
     */
    @PutMapping("/jobs/{jobId}/applications/{applicationId}/reject")
    public ResponseEntity<?> rejectApplicant(Authentication authentication,
                                             @PathVariable Long jobId,
                                             @PathVariable Long applicationId) {
        try {
            UserDTO orgUser = (UserDTO) authentication.getPrincipal();
            return ResponseEntity.ok(
                    applicationService.updateApplicationStatus(orgUser, jobId, applicationId, ApplicationStatus.REJECTED)
            );
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MSG_INTERNAL);
        }
    }
}
