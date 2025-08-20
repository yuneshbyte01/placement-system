package com.university.placementsystem.controller;

import com.university.placementsystem.dto.UserDTO;
import com.university.placementsystem.dto.StudentApplicationDTO;
import com.university.placementsystem.entity.*;
import com.university.placementsystem.repository.ApplicationRepository;
import com.university.placementsystem.repository.JobPostingRepository;
import com.university.placementsystem.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for students to apply for jobs and view their applications.
 */
@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentApplicationController {

    private final ApplicationRepository applicationRepository;
    private final StudentRepository studentRepository;
    private final JobPostingRepository jobPostingRepository;

    /**
     * Simple test endpoint to verify the controller is working.
     */
    @GetMapping("/test-application")
    public ResponseEntity<Map<String, String>> testEndpoint() {
        return ResponseEntity.ok(Map.of(
                "message", "StudentApplicationController is working!",
                "status", "success"
        ));
    }

    // ------------------- Apply to Job -------------------

    @PostMapping("/apply/{jobId}")
    public ResponseEntity<?> applyForJob(Authentication authentication,
                                         @PathVariable Long jobId) {
        checkStudentRole(authentication);
        Student student = getStudentOrThrow(authentication);

        JobPosting job = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job posting not found"));

        if (applicationRepository.existsByStudent_IdAndJobPosting_Id(student.getId(), job.getId())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Already applied for this job"));
        }

        Application application = Application.builder()
                .student(student)
                .jobPosting(job)
                .status(ApplicationStatus.APPLIED)
                .build();

        applicationRepository.save(application);

        return ResponseEntity.ok(Map.of(
                "message", "Applied successfully",
                "applicationId", application.getId()
        ));
    }


    // ------------------- List Student Applications -------------------

    /**
     * List all applications of the logged-in student.
     */
    @GetMapping("/applications")
    public ResponseEntity<?> listApplications(Authentication authentication) {
        checkStudentRole(authentication);
        Student student = getStudentOrThrow(authentication);

        List<StudentApplicationDTO> applications = applicationRepository
                .findByStudent_Id(student.getId())
                .stream()
                .map(app -> new StudentApplicationDTO(
                        app.getId(),
                        app.getJobPosting().getTitle(),
                        app.getJobPosting().getDescription(),
                        app.getJobPosting().getSkillsRequired(),
                        app.getJobPosting().getEligibilityCriteria(),
                        app.getStatus(),
                        app.getAppliedAt()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(applications);
    }

    // ------------------- Private Helper Methods -------------------

    /** Retrieves the UserDTO from the authentication object */
    private UserDTO getUser(Authentication authentication) {
        return (UserDTO) authentication.getPrincipal();
    }

    /** Retrieves the Student entity for the logged-in user */
    private Student getStudentOrThrow(Authentication authentication) {
        return studentRepository.findByUserEmail(getUser(authentication).getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student profile not found"));
    }

    /**
     * Ensures the logged-in user has a STUDENT role, throws 403 if not.
     */
    private void checkStudentRole(Authentication authentication) {
        if (getUser(authentication).getRole() != UserRole.STUDENT) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: STUDENT role required");
        }
    }
}
