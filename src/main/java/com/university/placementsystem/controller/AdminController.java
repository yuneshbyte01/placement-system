package com.university.placementsystem.controller;

import com.university.placementsystem.dto.ApplicationDTO;
import com.university.placementsystem.dto.JobPostingDTO;
import com.university.placementsystem.dto.OrganizationDTO;
import com.university.placementsystem.entity.User;
import com.university.placementsystem.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

/**
 * Admin REST controller for managing users, organizations, job postings, and applications.
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>List/activate/deactivate users</li>
 *   <li>List/approve/reject organizations</li>
 *   <li>Monitor job postings and applications</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    // Dependencies
    private final AdminService adminService;

    // ---- Messages / constants ----
    private static final String MSG_TEST_OK            = "Admin module active";
    private static final String MSG_STATUS_SUCCESS     = "success";
    private static final String MSG_USER_DEACTIVATED   = "User deactivated";
    private static final String MSG_USER_ACTIVATED     = "User activated";
    private static final String MSG_ORG_APPROVED       = "Organization approved successfully";
    private static final String MSG_ORG_REJECTED       = "Organization rejected and removed";
    private static final String MSG_INTERNAL           = "Internal server error";

    /**
     * Health check endpoint for the admin module.
     *
     * @return simple JSON payload indicating the module is active
     */
    @GetMapping("/test")
    public ResponseEntity<?> testAdmin() {
        try {
            return ResponseEntity.ok(Map.of(
                    "message", MSG_TEST_OK,
                    "status", MSG_STATUS_SUCCESS
            ));
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MSG_INTERNAL);
        }
    }

    /**
     * Returns all users.
     *
     * @return list of {@link User}
     */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = adminService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MSG_INTERNAL);
        }
    }

    /**
     * Deactivates a user by id.
     *
     * @param id user id
     * @return confirmation message and user id
     */
    @PutMapping("/users/{id}/deactivate")
    public ResponseEntity<?> deactivateUser(@PathVariable Long id) {
        try {
            adminService.deactivateUser(id);
            return ResponseEntity.ok(Map.of("message", MSG_USER_DEACTIVATED, "userId", id));
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MSG_INTERNAL);
        }
    }

    /**
     * Activates a user by id.
     *
     * @param id user id
     * @return confirmation message and user id
     */
    @PutMapping("/users/{id}/activate")
    public ResponseEntity<?> activateUser(@PathVariable Long id) {
        try {
            adminService.activateUser(id);
            return ResponseEntity.ok(Map.of("message", MSG_USER_ACTIVATED, "userId", id));
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MSG_INTERNAL);
        }
    }

    /**
     * Returns all organizations.
     *
     * @return list of {@link OrganizationDTO}
     */
    @GetMapping("/organizations")
    public ResponseEntity<?> getAllOrganizations() {
        try {
            List<OrganizationDTO> orgs = adminService.getAllOrganizations();
            return ResponseEntity.ok(orgs);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MSG_INTERNAL);
        }
    }

    /**
     * Returns organizations that are pending approval.
     *
     * @return list of {@link OrganizationDTO}
     */
    @GetMapping("/organizations/pending")
    public ResponseEntity<?> getPendingOrganizations() {
        try {
            List<OrganizationDTO> orgs = adminService.getPendingOrganizations();
            return ResponseEntity.ok(orgs);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MSG_INTERNAL);
        }
    }

    /**
     * Approves an organization.
     *
     * @param id organization id
     * @return confirmation message
     */
    @PutMapping("/organizations/{id}/approve")
    public ResponseEntity<?> approveOrganization(@PathVariable Long id) {
        try {
            adminService.approveOrganization(id);
            return ResponseEntity.ok(Map.of("message", MSG_ORG_APPROVED));
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MSG_INTERNAL);
        }
    }

    /**
     * Rejects an organization (sets approved=false or removes if implemented).
     *
     * @param id organization id
     * @return confirmation message
     */
    @PutMapping("/organizations/{id}/reject")
    public ResponseEntity<?> rejectOrganization(@PathVariable Long id) {
        try {
            adminService.rejectOrganization(id);
            return ResponseEntity.ok(Map.of("message", MSG_ORG_REJECTED));
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MSG_INTERNAL);
        }
    }

    /**
     * Returns all job postings.
     *
     * @return list of {@link JobPostingDTO}
     */
    @GetMapping("/jobs")
    public ResponseEntity<?> getAllJobPostings() {
        try {
            List<JobPostingDTO> jobs = adminService.getAllJobPostings();
            return ResponseEntity.ok(jobs);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MSG_INTERNAL);
        }
    }

    /**
     * Returns all applications.
     *
     * @return list of {@link ApplicationDTO}
     */
    @GetMapping("/applications")
    public ResponseEntity<?> getAllApplications() {
        try {
            List<ApplicationDTO> apps = adminService.getAllApplications();
            return ResponseEntity.ok(apps);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MSG_INTERNAL);
        }
    }

    /**
     * Returns a single application by id.
     *
     * @param id application id
     * @return {@link ApplicationDTO}
     */
    @GetMapping("/applications/{id}")
    public ResponseEntity<?> getApplicationById(@PathVariable Long id) {
        try {
            ApplicationDTO dto = adminService.getApplicationById(id);
            return ResponseEntity.ok(dto);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MSG_INTERNAL);
        }
    }
}
