package com.university.placementsystem.controller;

import com.university.placementsystem.dto.StudentCreateRequest;
import com.university.placementsystem.dto.StudentDTO;
import com.university.placementsystem.dto.StudentUpdateRequest;
import com.university.placementsystem.dto.UserDTO;
import com.university.placementsystem.entity.UserRole;
import com.university.placementsystem.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/**
 * REST controller for managing student profiles and resume uploads.
 *
 * <p>Provides endpoints to create, read, and update student profiles,
 * as well as uploading PDF resumes. Authentication is required, and
 * only users with the STUDENT role can access these endpoints.</p>
 */
@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    // Dependencies
    private final StudentService studentService;

    // ---- Messages / constants ----
    private static final String MSG_STUDENT_ENDPOINT_OK = "STUDENT endpoint accessed successfully";
    private static final String MSG_PROFILE_CREATED     = "Profile created successfully";
    private static final String MSG_PROFILE_UPDATED     = "Profile updated successfully";
    private static final String MSG_ACCESS_DENIED       = "Access denied: STUDENT role required";
    private static final String MSG_RESUME_UPLOADED     = "Resume uploaded successfully";
    private static final String MSG_INTERNAL            = "Internal server error";

    // ------------------- Test Endpoint -------------------

    /**
     * Simple test endpoint to verify the student API is accessible.
     *
     * @param authentication Authentication object injected by Spring Security
     * @return JSON with a message, email, and role of the logged-in user
     */
    @GetMapping("/test")
    public ResponseEntity<?> testStudentEndpoint(Authentication authentication) {
        try {
            UserDTO user = (UserDTO) authentication.getPrincipal();
            return ResponseEntity.ok(Map.of(
                    "message", MSG_STUDENT_ENDPOINT_OK,
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
     * Creates a new student profile for the logged-in user.
     *
     * @param authentication Authentication object injected by Spring Security
     * @param request        DTO containing profile data
     * @return HTTP 201 on success
     */
    @PostMapping("/profile")
    public ResponseEntity<?> createProfile(Authentication authentication,
                                           @Valid @RequestBody StudentCreateRequest request) {
        try {
            UserDTO user = (UserDTO) authentication.getPrincipal();
            studentService.createProfile(user.getEmail(), request, user.getId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", MSG_PROFILE_CREATED));
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MSG_INTERNAL);
        }
    }

    /**
     * Retrieves the student profile for the logged-in user.
     *
     * @param authentication Authentication object injected by Spring Security
     * @return StudentDTO with profile details
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        try {
            UserDTO user = (UserDTO) authentication.getPrincipal();
            StudentDTO dto = studentService.getProfile(user.getEmail());
            return ResponseEntity.ok(dto);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MSG_INTERNAL);
        }
    }

    /**
     * Updates the profile of the logged-in student.
     *
     * @param authentication Authentication object injected by Spring Security
     * @param request        DTO containing updated profile fields
     * @return HTTP 200 on success
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(Authentication authentication,
                                           @Valid @RequestBody StudentUpdateRequest request) {
        try {
            UserDTO user = (UserDTO) authentication.getPrincipal();
            studentService.updateProfile(user.getEmail(), request);
            return ResponseEntity.ok(Map.of("message", MSG_PROFILE_UPDATED));
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MSG_INTERNAL);
        }
    }

    // ------------------- Resume Upload Endpoint -------------------

    /**
     * Uploads a PDF resume for the logged-in student.
     *
     * <p>Validates file type and size and ensures only users with a STUDENT
     * role can upload.</p>
     *
     * @param authentication Authentication object injected by Spring Security
     * @param file           multipart PDF file
     * @return HTTP 200 with a resume path on success
     */
    @PostMapping("/upload-resume")
    public ResponseEntity<?> uploadResume(Authentication authentication,
                                          @RequestParam("file") MultipartFile file) {
        try {
            UserDTO user = (UserDTO) authentication.getPrincipal();

            // Role check
            if (user.getRole() != UserRole.STUDENT) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", MSG_ACCESS_DENIED));
            }

            String savedPath = studentService.uploadResume(user.getEmail(), file);
            return ResponseEntity.ok(Map.of(
                    "message", MSG_RESUME_UPLOADED,
                    "resumePath", savedPath
            ));
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MSG_INTERNAL);
        }
    }
}
