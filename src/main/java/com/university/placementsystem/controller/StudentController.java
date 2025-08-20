package com.university.placementsystem.controller;

import com.university.placementsystem.dto.*;
import com.university.placementsystem.entity.Student;
import com.university.placementsystem.entity.User;
import com.university.placementsystem.entity.UserRole;
import com.university.placementsystem.repository.StudentRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * REST controller for managing student profiles and resume uploads.
 * <p>
 * Provides endpoints to create, read, and update student profiles,
 * as well as uploading PDF resumes.
 * Authentication is required, and role-based access control ensures
 * only users with the STUDENT role can access these endpoints.
 */
@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentRepository studentRepository;

    @Value("${student.upload-dir:uploads/resumes}")
    private String uploadDir;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB
    private static final String PDF_CONTENT_TYPE = "application/pdf";

    // ------------------- Test Endpoint -------------------

    /**
     * Simple test endpoint to verify the student API is accessible.
     *
     * @param authentication Authentication object injected by Spring Security
     * @return JSON with a message, email, and role of a logged-in user
     */
    @GetMapping("/test")
    public ResponseEntity<?> testStudentEndpoint(Authentication authentication) {
        UserDTO user = getUser(authentication);
        return ResponseEntity.ok(Map.of(
                "message", "STUDENT endpoint accessed successfully",
                "email", user.getEmail(),
                "role", user.getRole().name()
        ));
    }

    // ------------------- Profile Endpoints -------------------

    /**
     * Creates a new student profile for the logged-in user.
     *
     * @param authentication Authentication object injected by Spring Security
     * @param request        DTO containing profile data
     * @return HTTP 201 on success, 400 if the profile already exists
     */
    @PostMapping("/profile")
    public ResponseEntity<?> createProfile(Authentication authentication,
                                           @Valid @RequestBody StudentCreateRequest request) {
        UserDTO user = getUser(authentication);

        if (studentRepository.findByUserEmail(user.getEmail()).isPresent()) {
            return badRequest("Profile already exists");
        }

        Student student = Student.builder()
                .user(new User(user.getId()))
                .university(request.getUniversity())
                .degree(request.getDegree())
                .graduationYear(request.getGraduationYear())
                .skills(request.getSkills())
                .build();

        studentRepository.save(student);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Profile created successfully"));
    }

    /**
     * Retrieves the student profile for the logged-in user.
     *
     * @param authentication Authentication object injected by Spring Security
     * @return StudentDTO with profile details or 404 if not found
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        return getStudent(authentication)
                .map(student -> new StudentDTO(
                        student.getUniversity(),
                        student.getDegree(),
                        student.getGraduationYear(),
                        student.getSkills(),
                        student.getResumePath(),
                        student.getUser().getEmail()
                ))
                .map(ResponseEntity::ok)
                .orElseGet(this::notFound);
    }

    /**
     * Updates the profile of the logged-in student.
     *
     * @param authentication Authentication object injected by Spring Security
     * @param request        DTO containing updated profile fields
     * @return HTTP 200 on success, 404 if profile not found
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(Authentication authentication,
                                           @Valid @RequestBody StudentUpdateRequest request) {
        Optional<Student> studentOpt = getStudent(authentication);

        if (studentOpt.isEmpty()) {
            return notFound();
        }

        Student student = studentOpt.get();
        student.setUniversity(request.getUniversity());
        student.setDegree(request.getDegree());
        student.setGraduationYear(request.getGraduationYear());
        student.setSkills(request.getSkills());

        studentRepository.save(student);

        return ResponseEntity.ok(Map.of("message", "Profile updated successfully"));
    }

    // ------------------- Resume Upload Endpoint -------------------

    /**
     * Uploads a PDF resume for the logged-in student.
     * <p>
     * Validates file type, size, and ensures only users with a STUDENT role
     * can upload. Saves the file to a configurable directory and updates
     * the student's profile.
     *
     * @param authentication Authentication object injected by Spring Security
     * @param file           Multipart PDF file
     * @return HTTP 200 with a resume path on success, 400/403/404 on errors
     */
    @PostMapping("/upload-resume")
    public ResponseEntity<?> uploadResume(Authentication authentication,
                                          @RequestParam("file") MultipartFile file) {
        UserDTO user = getUser(authentication);

        if (!hasStudentRole(authentication)) return forbidden();

        // --- File validations ---
        if (file.isEmpty()) return badRequest("File is empty");
        if (!PDF_CONTENT_TYPE.equalsIgnoreCase(file.getContentType())) return badRequest("Only PDF files are allowed");
        if (file.getSize() > MAX_FILE_SIZE) return badRequest("File exceeds 5MB limit");

        try {
            Path dirPath = Path.of(uploadDir).toAbsolutePath();
            if (!Files.exists(dirPath)) Files.createDirectories(dirPath);

            String fileName = sanitizeFileName(user.getEmail()) + "_" + UUID.randomUUID() + ".pdf";
            Path destination = dirPath.resolve(fileName);

            Files.copy(file.getInputStream(), destination, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            Optional<Student> studentOpt = getStudent(authentication);
            if (studentOpt.isEmpty()) return notFound();

            Student student = studentOpt.get();
            student.setResumePath(destination.toString());
            studentRepository.save(student);

            return ResponseEntity.ok(Map.of(
                    "message", "Resume uploaded successfully",
                    "resumePath", destination.toString()
            ));

        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to store file: " + ex.getMessage()));
        }
    }

    // ------------------- Private Helper Methods -------------------

    /** Get a logged-in user from authentication */
    private UserDTO getUser(Authentication authentication) {
        return (UserDTO) authentication.getPrincipal();
    }

    /** Get student entity for logged-in user */
    private Optional<Student> getStudent(Authentication authentication) {
        return studentRepository.findByUserEmail(getUser(authentication).getEmail());
    }

    /** Check if user has STUDENT role */
    private boolean hasStudentRole(Authentication authentication) {
        return getUser(authentication).getRole() == UserRole.STUDENT;
    }

    /** Return 403 Forbidden response */
    private ResponseEntity<Map<String, String>> forbidden() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", "Access denied: STUDENT role required"));
    }

    /** Return 400 Bad Request response with a message */
    private ResponseEntity<Map<String, String>> badRequest(String message) {
        return ResponseEntity.badRequest().body(Map.of("message", message));
    }

    /** Return 404 Not Found response for student profile */
    private ResponseEntity<StudentDTO> notFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body((StudentDTO) Map.of("message", "STUDENT profile not found"));
    }

    /** Sanitize email for safe file name */
    private String sanitizeFileName(String email) {
        return email.replaceAll("[^a-zA-Z0-9]", "_");
    }

}
