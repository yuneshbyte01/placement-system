package com.university.placementsystem.controller;

import com.university.placementsystem.dto.*;
import com.university.placementsystem.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for organization-related endpoints.
 *
 * <p>Handles HTTP requests related to organization profiles and job postings.
 * Delegates business logic to {@link OrganizationService}.</p>
 *
 * <p>Endpoints require authentication and are restricted to users with
 * the ORGANIZATION role.</p>
 */
@RestController
@RequestMapping("/api/organization")
@RequiredArgsConstructor
public class OrganizationController {

    // Dependencies
    private final OrganizationService organizationService;

    // ---- Messages / constants ----
    private static final String MSG_ORG_ENDPOINT_OK   = "ORGANIZATION endpoint accessed successfully";
    private static final String MSG_PROFILE_CREATED    = "Organization profile created successfully";
    private static final String MSG_PROFILE_UPDATED    = "Profile updated successfully";
    private static final String MSG_PROFILE_NOT_FOUND  = "Organization profile not found";
    private static final String MSG_JOB_CREATED        = "Job posting created successfully";
    private static final String MSG_INTERNAL           = "Internal server error";

    // ------------------- Test Endpoint -------------------

    /**
     * Test endpoint to verify the organization API is accessible.
     *
     * @param authentication Authentication object injected by Spring Security
     * @return JSON with a message, email, and role of the logged-in user
     */
    @GetMapping("/test")
    public ResponseEntity<?> testOrganization(Authentication authentication) {
        try {
            UserDTO user = getUser(authentication);
            return ResponseEntity.ok(Map.of(
                    "message", MSG_ORG_ENDPOINT_OK,
                    "email", user.getEmail(),
                    "role", user.getRole().name()
            ));
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MSG_INTERNAL);
        }
    }

    // ------------------- Profile Endpoints -------------------

    /**
     * Create a new organization profile.
     *
     * @param authentication Authentication object injected by Spring Security
     * @param request        DTO containing profile creation data
     * @return HTTP 201 with a success message, or 400 if the profile exists
     */
    @PostMapping("/profile")
    public ResponseEntity<?> createProfile(Authentication authentication,
                                           @RequestBody OrganizationCreateRequest request) {
        try {
            UserDTO user = getUser(authentication);
            organizationService.createProfile(user, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", MSG_PROFILE_CREATED));
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MSG_INTERNAL);
        }
    }

    /**
     * Retrieve the organization profile of the logged-in user.
     *
     * @param authentication Authentication object injected by Spring Security
     * @return OrganizationDTO with profile data or 404 if not found
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        try {
            UserDTO user = getUser(authentication);
            return organizationService.getProfile(user)
                    .map(org -> ResponseEntity.ok(new OrganizationDTO(
                            org.getCompanyName(),
                            org.getIndustry(),
                            org.getLocation(),
                            org.getDescription(),
                            org.isApproved(),
                            org.getUser().getEmail()
                    )))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body((OrganizationDTO) Map.of("message", MSG_PROFILE_NOT_FOUND)));
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MSG_INTERNAL);
        }
    }

    /**
     * Update the organization profile of the logged-in user.
     *
     * @param authentication Authentication object injected by Spring Security
     * @param request        DTO containing updated profile fields
     * @return HTTP 200 on success, 404 if profile not found
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(Authentication authentication,
                                           @RequestBody OrganizationUpdateRequest request) {
        try {
            UserDTO user = getUser(authentication);
            organizationService.updateProfile(user, request);
            return ResponseEntity.ok(Map.of("message", MSG_PROFILE_UPDATED));
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MSG_INTERNAL);
        }
    }

    // ------------------- Job Posting Endpoints -------------------

    /**
     * Create a new job posting for the logged-in organization.
     *
     * @param authentication Authentication object injected by Spring Security
     * @param request        DTO containing job posting data
     * @return HTTP 201 with a success message; 403 if not approved
     */
    @PostMapping("/jobs")
    public ResponseEntity<?> createJobPosting(Authentication authentication,
                                              @RequestBody JobPostingCreateRequest request) {
        try {
            UserDTO user = getUser(authentication);
            organizationService.createJobPosting(user, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", MSG_JOB_CREATED));
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MSG_INTERNAL);
        }
    }

    /**
     * List all job postings of the logged-in organization.
     *
     * @param authentication Authentication object injected by Spring Security
     * @return List of {@link JobPostingDTO}
     */
    @GetMapping("/jobs")
    public ResponseEntity<?> listJobPostings(Authentication authentication) {
        try {
            UserDTO user = getUser(authentication);
            List<JobPostingDTO> jobs = organizationService.listJobPostings(user);
            return ResponseEntity.ok(jobs);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MSG_INTERNAL);
        }
    }

    // ------------------- Private Helper Methods -------------------

    // Extracts UserDTO from the Authentication object
    private UserDTO getUser(Authentication authentication) {
        return (UserDTO) authentication.getPrincipal();
    }
}
