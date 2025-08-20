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

import java.util.HashMap;
import java.util.Map;

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
}
