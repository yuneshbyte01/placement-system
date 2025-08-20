package com.university.placementsystem.controller;

import com.university.placementsystem.dto.StudentCreateRequest;
import com.university.placementsystem.dto.StudentDTO;
import com.university.placementsystem.dto.StudentUpdateRequest;
import com.university.placementsystem.dto.UserDTO;
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
 * Controller for managing student profiles and resume uploads.
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

    // ------------------- Profile Endpoints -------------------

    @GetMapping("/test")
    public ResponseEntity<?> testStudentEndpoint(Authentication authentication) {
        UserDTO user = (UserDTO) authentication.getPrincipal();
        return ResponseEntity.ok(Map.of(
                "message", "STUDENT endpoint accessed successfully",
                "email", user.getEmail(),
                "role", user.getRole().name()
        ));
    }

    @PostMapping("/profile")
    public ResponseEntity<?> createProfile(Authentication authentication,
                                           @Valid @RequestBody StudentCreateRequest request) {
        UserDTO user = (UserDTO) authentication.getPrincipal();

        if (studentRepository.findByUserEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Profile already exists"));
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

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        return findStudentByAuth(authentication)
                .map(student -> new StudentDTO(
                        student.getUniversity(),
                        student.getDegree(),
                        student.getGraduationYear(),
                        student.getSkills(),
                        student.getResumePath(),
                        student.getUser().getEmail()
                ))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body((StudentDTO) Map.of("message", "Student profile not found")));
    }


    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(Authentication authentication,
                                           @Valid @RequestBody StudentUpdateRequest request) {
        Optional<Student> studentOpt = findStudentByAuth(authentication);

        if (studentOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Student profile not found"));
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

    @PostMapping("/upload-resume")
    public ResponseEntity<?> uploadResume(Authentication authentication,
                                          @RequestParam("file") MultipartFile file) {
        UserDTO user = (UserDTO) authentication.getPrincipal();

        // Ensure only a STUDENT role can upload
        if (user.getRole() != UserRole.STUDENT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Access denied: STUDENT role required"));
        }

        // --- File validations ---
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "File is empty"));
        }
        if (!PDF_CONTENT_TYPE.equalsIgnoreCase(file.getContentType())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Only PDF files are allowed"));
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            return ResponseEntity.badRequest().body(Map.of("message", "File exceeds 5MB limit"));
        }

        try {
            // --- Resolve absolute upload directory ---
            Path dirPath = Path.of(uploadDir).toAbsolutePath();
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            // --- Save file safely ---
            String fileName = sanitizeFileName(user.getEmail()) + "_" + UUID.randomUUID() + ".pdf";
            Path destination = dirPath.resolve(fileName);

            Files.copy(file.getInputStream(), destination, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            // --- Update student profile in DB ---
            Optional<Student> studentOpt = findStudentByAuth(authentication);
            if (studentOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Student profile not found"));
            }

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

    // ------------------- Private Helpers -------------------

    /**
     * Finds the logged-in student's profile based on authentication.
     */
    private Optional<Student> findStudentByAuth(Authentication authentication) {
        UserDTO user = (UserDTO) authentication.getPrincipal();
        return studentRepository.findByUserEmail(user.getEmail());
    }

    /**
     * Sanitizes email for safe file naming.
     */
    private String sanitizeFileName(String email) {
        return email.replaceAll("[^a-zA-Z0-9]", "_");
    }
}
