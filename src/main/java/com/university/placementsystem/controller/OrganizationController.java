package com.university.placementsystem.controller;

import com.university.placementsystem.dto.*;
import com.university.placementsystem.entity.JobPosting;
import com.university.placementsystem.entity.Organization;
import com.university.placementsystem.entity.UserRole;
import com.university.placementsystem.repository.JobPostingRepository;
import com.university.placementsystem.repository.OrganizationRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST Controller for managing organization profiles and job postings.
 */
@RestController
@RequestMapping("/api/organization")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationRepository organizationRepository;
    private final JobPostingRepository jobPostingRepository;

    // ------------------- Profile Endpoints -------------------

    @GetMapping("/test")
    public ResponseEntity<?> testOrganizationEndpoint(Authentication authentication) {
        return ResponseEntity.ok(Map.of(
                "message", "ORGANIZATION endpoint accessed successfully",
                "email", getUserDTO(authentication).getEmail(),
                "role", getUserDTO(authentication).getRole().name()
        ));
    }

    @PostMapping("/profile")
    public ResponseEntity<?> createProfile(Authentication authentication,
                                           @Valid @RequestBody OrganizationCreateRequest request) {
        UserDTO user = getUserDTO(authentication);

        if (organizationRepository.findByUserEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Profile already exists"));
        }

        Organization organization = Organization.builder()
                .user(new com.university.placementsystem.entity.User(user.getId()))
                .companyName(request.getCompanyName())
                .industry(request.getIndustry())
                .location(request.getLocation())
                .description(request.getDescription())
                .approved(false)
                .build();

        organizationRepository.save(organization);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Organization profile created successfully"));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        Optional<Organization> orgOpt = getOrganization(authentication);
        return orgOpt
                .map(org -> new OrganizationDTO(
                        org.getCompanyName(),
                        org.getIndustry(),
                        org.getLocation(),
                        org.getDescription(),
                        org.isApproved(),
                        org.getUser().getEmail()
                ))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body((OrganizationDTO) Map.of("message", "Organization profile not found")));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(Authentication authentication,
                                           @Valid @RequestBody OrganizationUpdateRequest request) {
        Optional<Organization> orgOpt = getOrganization(authentication);

        if (orgOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Organization profile not found"));
        }

        Organization organization = orgOpt.get();
        organization.setCompanyName(request.getCompanyName());
        organization.setIndustry(request.getIndustry());
        organization.setLocation(request.getLocation());
        organization.setDescription(request.getDescription());

        organizationRepository.save(organization);

        return ResponseEntity.ok(Map.of("message", "Profile updated successfully"));
    }

    // ------------------- Job Posting Endpoints -------------------

    @PostMapping("/jobs")
    public ResponseEntity<?> createJobPosting(Authentication authentication,
                                              @Valid @RequestBody JobPostingCreateRequest request) {
        if (hasOrgRole(authentication)) {
            return forbiddenResponse();
        }

        Optional<Organization> orgOpt = getOrganization(authentication);
        if (orgOpt.isEmpty()) return notFoundResponse();

        Organization org = orgOpt.get();
        if (!org.isApproved()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Organization not approved by Admin. Cannot post jobs."));
        }

        JobPosting job = JobPosting.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .skillsRequired(request.getSkillsRequired())
                .eligibilityCriteria(request.getEligibilityCriteria())
                .organization(org)
                .build();

        jobPostingRepository.save(job);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Job posting created successfully", "jobId", job.getId()));
    }

    @GetMapping("/jobs")
    public ResponseEntity<?> listJobPostings(Authentication authentication) {
        if (hasOrgRole(authentication)) {
            return forbiddenResponse();
        }

        Optional<Organization> orgOpt = getOrganization(authentication);
        if (orgOpt.isEmpty()) return notFoundResponse();

        List<JobPostingDTO> jobs = jobPostingRepository.findByOrganizationId(orgOpt.get().getId())
                .stream()
                .map(job -> new JobPostingDTO(
                        job.getId(),
                        job.getTitle(),
                        job.getDescription(),
                        job.getSkillsRequired(),
                        job.getEligibilityCriteria(),
                        job.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(jobs);
    }

    // ------------------- Private Helper Methods -------------------

    /**
     * Extract UserDTO from an Authentication object.
     */
    private UserDTO getUserDTO(Authentication authentication) {
        return (UserDTO) authentication.getPrincipal();
    }

    /**
     * Fetch the organization profile linked to the logged-in user.
     */
    private Optional<Organization> getOrganization(Authentication authentication) {
        UserDTO user = getUserDTO(authentication);
        return organizationRepository.findByUserId(user.getId());
    }

    /**
     * Check if the logged-in user has an ORGANIZATION role.
     */
    private boolean hasOrgRole(Authentication authentication) {
        return getUserDTO(authentication).getRole() != UserRole.ORGANIZATION;
    }

    /**
     * Return standard 403 Forbidden response.
     */
    private ResponseEntity<Map<String, String>> forbiddenResponse() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", "Access denied: ORGANIZATION role required"));
    }

    /**
     * Return the standard 404 Not Found response with a custom message.
     */
    private ResponseEntity<Map<String, String>> notFoundResponse() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "ORGANIZATION profile not found"));
    }
}
