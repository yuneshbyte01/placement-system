package com.university.placementsystem.controller;

import com.university.placementsystem.entity.Student;
import com.university.placementsystem.entity.User;
import com.university.placementsystem.repository.StudentRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for Student profile management.
 * Secured endpoints for the STUDENT role only.
 */
@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {

    private final StudentRepository studentRepository;

    /**
     * Get the profile of the currently logged-in student.
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        return studentRepository.findByUserId(user.getId())
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Student profile not found for user: " + user.getEmail());
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
    }

    /**
     * Update student profile fields.
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(Authentication authentication,
                                           @Valid @RequestBody Student updatedStudent) {
        User user = (User) authentication.getPrincipal();

        return studentRepository.findByUserId(user.getId())
                .map(student -> {
                    // Update allowed fields
                    student.setUniversity(updatedStudent.getUniversity());
                    student.setDegree(updatedStudent.getDegree());
                    student.setGraduationYear(updatedStudent.getGraduationYear());
                    student.setSkills(updatedStudent.getSkills());
                    student.setResumePath(updatedStudent.getResumePath());

                    studentRepository.save(student);

                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Student profile updated successfully!");
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Cannot update: Student profile not found for user: " + user.getEmail());
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
    }

    /**
     * Upload resume for the student.
     */
    @PostMapping("/upload-resume")
    public ResponseEntity<?> uploadResume(Authentication authentication,
                                          @RequestParam("file") MultipartFile file) {
        User user = (User) authentication.getPrincipal();

        // ✅ Validate file
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "File is empty!"));
        }
        if (!file.getContentType().equalsIgnoreCase("application/pdf")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Only PDF files are allowed!"));
        }
        if (file.getSize() > 5 * 1024 * 1024) { // 5MB limit
            return ResponseEntity.badRequest().body(Map.of("message", "File size exceeds 5MB!"));
        }

        // ✅ Save file locally
        String uploadDir = "uploads/resumes/";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = "resume_" + user.getId() + ".pdf"; // unique per student
        File destination = new File(uploadDir + fileName);

        try {
            file.transferTo(destination);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to save file!"));
        }

        // ✅ Save path in DB
        Optional<Student> optionalStudent = studentRepository.findByUserId(user.getId());
        if (optionalStudent.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Student profile not found for user: " + user.getEmail()));
        }

        Student student = optionalStudent.get();
        student.setResumePath(destination.getAbsolutePath());
        studentRepository.save(student);

        return ResponseEntity.ok(Map.of(
                "message", "Resume uploaded successfully!",
                "resumePath", destination.getAbsolutePath()
        ));
    }
}
