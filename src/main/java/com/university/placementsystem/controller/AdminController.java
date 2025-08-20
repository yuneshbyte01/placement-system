package com.university.placementsystem.controller;

import com.university.placementsystem.dto.ApplicationDTO;
import com.university.placementsystem.dto.JobPostingDTO;
import com.university.placementsystem.dto.OrganizationDTO;
import com.university.placementsystem.entity.Organization;
import com.university.placementsystem.entity.User;
import com.university.placementsystem.repository.ApplicationRepository;
import com.university.placementsystem.repository.JobPostingRepository;
import com.university.placementsystem.repository.OrganizationRepository;
import com.university.placementsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final JobPostingRepository jobPostingRepository;
    private final ApplicationRepository applicationRepository;

    /** --- Test endpoint --- */
    @GetMapping("/test")
    public ResponseEntity<?> testAdmin() {
        return ResponseEntity.ok(Map.of(
                "message", "Admin module active",
                "status", "success"
        ));
    }

    /** --- List all users --- */
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    /** --- Deactivate a user --- */
    @PutMapping("/users/{id}/deactivate")
    public ResponseEntity<?> deactivateUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setActive(false);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "message", "User deactivated",
                "userId", user.getId()
        ));
    }

    /** --- Activate a user --- */
    @PutMapping("/users/{id}/activate")
    public ResponseEntity<?> activateUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setActive(true);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "message", "User activated",
                "userId", user.getId()
        ));
    }

    /** --- List all organizations --- */
    @GetMapping("/organizations")
    public ResponseEntity<List<OrganizationDTO>> getAllOrganizations() {
        List<OrganizationDTO> orgs = organizationRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orgs);
    }

    /** --- List of pending organizations --- */
    @GetMapping("/organizations/pending")
    public ResponseEntity<List<OrganizationDTO>> getPendingOrganizations() {
        List<OrganizationDTO> pendingOrgs = organizationRepository.findByApprovedFalse()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(pendingOrgs);
    }

    /** --- Approve an organization --- */
    @PutMapping("/organizations/{id}/approve")
    public ResponseEntity<?> approveOrganization(@PathVariable Long id) {
        return organizationRepository.findById(id)
                .map(org -> {
                    org.setApproved(true);
                    organizationRepository.save(org);
                    return ResponseEntity.ok(Map.of("message", "Organization approved successfully"));
                })
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Organization not found")));
    }

    /** --- Reject an organization --- */
    @PutMapping("/organizations/{id}/reject")
    public ResponseEntity<?> rejectOrganization(@PathVariable Long id) {
        return organizationRepository.findById(id)
                .map(org -> {
                    org.setApproved(false);
                    organizationRepository.save(org);
                    return ResponseEntity.ok(Map.of("message", "Organization rejected and removed"));
                })
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Organization not found")));
    }

    /** --- Monitor all job postings --- */
    @GetMapping("/jobs")
    public ResponseEntity<List<JobPostingDTO>> getAllJobPostings() {
        List<JobPostingDTO> jobs = jobPostingRepository.findAll()
                .stream()
                .map(job -> new JobPostingDTO(
                        job.getId(),
                        job.getTitle(),
                        job.getDescription(),
                        job.getSkillsRequired(),
                        job.getEligibilityCriteria(),
                        job.getCreatedAt(),
                        job.getOrganization().getCompanyName()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(jobs);
    }

    /** --- List all applications --- */
    @GetMapping("/applications")
    public ResponseEntity<List<ApplicationDTO>> getAllApplications() {
        List<ApplicationDTO> applications = applicationRepository.findAll()
                .stream()
                .map(app -> new ApplicationDTO(
                        app.getId(),
                        app.getStudent().getUser().getUsername(),
                        app.getStudent().getUser().getEmail(),
                        app.getJobPosting().getTitle(),
                        app.getJobPosting().getOrganization().getCompanyName(),
                        app.getStatus().name()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(applications);
    }

    /** --- Get a single application by ID --- */
    @GetMapping("/applications/{id}")
    public ResponseEntity<?> getApplicationById(@PathVariable Long id) {
        return applicationRepository.findById(id)
                .map(app -> new ApplicationDTO(
                        app.getId(),
                        app.getStudent().getUser().getUsername(),
                        app.getStudent().getUser().getEmail(),
                        app.getJobPosting().getTitle(),
                        app.getJobPosting().getOrganization().getCompanyName(),
                        app.getStatus().name()
                ))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404)
                        .body((ApplicationDTO) Map.of("message", "Application not found")));
    }

    // ------------------- Private Helper -------------------

    /** Convert Organization entity to DTO */
    private OrganizationDTO mapToDTO(Organization org) {
        return new OrganizationDTO(
                org.getCompanyName(),
                org.getIndustry(),
                org.getLocation(),
                org.getDescription(),
                org.isApproved(),
                org.getUser().getEmail()
        );
    }
}
