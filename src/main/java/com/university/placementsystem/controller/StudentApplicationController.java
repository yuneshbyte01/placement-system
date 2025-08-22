package com.university.placementsystem.controller;

import com.university.placementsystem.dto.ApplicationResponse;
import com.university.placementsystem.dto.UserDTO;
import com.university.placementsystem.entity.UserRole;
import com.university.placementsystem.service.StudentApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

/**
 * Controller for students to apply for jobs and view their applications.
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Expose endpoints to apply for jobs</li>
 *   <li>Retrieve logged-in student's applications</li>
 * </ul>
 *
 * <p>Delegates business logic to {@link StudentApplicationService}.</p>
 */
@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentApplicationController {

    // Dependencies
    private final StudentApplicationService studentApplicationService;

    // ---- Messages / constants ----
    private static final String MSG_TEST_OK          = "StudentApplicationController is working!";
    private static final String MSG_STATUS_SUCCESS   = "success";
    private static final String MSG_APPLIED_OK       = "Applied successfully";
    private static final String MSG_ACCESS_DENIED    = "Access denied: STUDENT role required";
    private static final String MSG_INTERNAL         = "Internal server error";

    /**
     * Simple test endpoint to verify the controller is working.
     *
     * @return test success message
     */
    @GetMapping("/test-application")
    public ResponseEntity<?> testEndpoint() {
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

    // ------------------- Apply to Job -------------------

    /**
     * Apply the logged-in student to a job.
     *
     * @param authentication Authentication object
     * @param jobId          ID of the job posting
     * @return application ID and success message
     */
    @PostMapping("/apply/{jobId}")
    public ResponseEntity<?> applyForJob(Authentication authentication,
                                         @PathVariable Long jobId) {
        try {
            UserDTO user = (UserDTO) authentication.getPrincipal();
            checkStudentRole(user);

            Long applicationId = studentApplicationService.applyForJob(user.getEmail(), jobId);

            return ResponseEntity.ok(Map.of(
                    "message", MSG_APPLIED_OK,
                    "applicationId", applicationId
            ));
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MSG_INTERNAL);
        }
    }

    // ------------------- List Student Applications -------------------

    /**
     * List all applications of the logged-in student.
     *
     * @param authentication Authentication object
     * @return list of application responses
     */
    @GetMapping("/applications")
    public ResponseEntity<?> listApplications(Authentication authentication) {
        try {
            UserDTO user = (UserDTO) authentication.getPrincipal();
            checkStudentRole(user);

            List<ApplicationResponse> applications = studentApplicationService.listApplications(user.getEmail());
            return ResponseEntity.ok(applications);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MSG_INTERNAL);
        }
    }

    // ------------------- Private Helpers -------------------

    /**
     * Ensures the logged-in user has a STUDENT role.
     *
     * @param user the logged-in user
     */
    private void checkStudentRole(UserDTO user) {
        if (user.getRole() != UserRole.STUDENT) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, MSG_ACCESS_DENIED);
        }
    }
}
