package com.university.placementsystem.service;

import com.university.placementsystem.dto.ApplicationResponse;
import com.university.placementsystem.entity.*;
import com.university.placementsystem.repository.ApplicationRepository;
import com.university.placementsystem.repository.JobPostingRepository;
import com.university.placementsystem.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for handling student job applications.
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Apply for jobs</li>
 *   <li>List all applications of a student</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class StudentApplicationService {

    // Repositories
    private final ApplicationRepository applicationRepository;
    private final StudentRepository studentRepository;
    private final JobPostingRepository jobPostingRepository;

    // ---- Constants ----
    private static final String MSG_STUDENT_NOT_FOUND = "Student profile not found";
    private static final String MSG_JOB_NOT_FOUND = "Job posting not found";
    private static final String MSG_ALREADY_APPLIED = "Already applied for this job";

    /**
     * Applies a student to a job posting.
     *
     * @param email student's email
     * @param jobId ID of the job posting
     * @return ID of the created application
     * @throws ResponseStatusException if a student / job isn't found or already applied
     */
    public Long applyForJob(String email, Long jobId) {
        // Fetch a student profile or fail with 404
        Student student = studentRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MSG_STUDENT_NOT_FOUND));

        // Fetch job posting or fail with 404
        JobPosting job = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MSG_JOB_NOT_FOUND));

        // Prevent duplicate applications
        if (applicationRepository.existsByStudent_IdAndJobPosting_Id(student.getId(), job.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, MSG_ALREADY_APPLIED);
        }

        // Create and persist application
        Application application = Application.builder()
                .student(student)
                .jobPosting(job)
                .status(ApplicationStatus.APPLIED)
                .build();

        applicationRepository.save(application);

        // Return the ID so the controller can use it
        return application.getId();
    }

    /**
     * Lists all applications of a student.
     *
     * @param email user email
     * @return list of {@link ApplicationResponse} DTOs
     * @throws ResponseStatusException if student profile not found
     */
    public List<ApplicationResponse> listApplications(String email) {
        // Ensure profile exists
        Student student = studentRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MSG_STUDENT_NOT_FOUND));

        // Map entities to DTOs
        return applicationRepository.findByStudent_Id(student.getId())
                .stream()
                .map(app -> new ApplicationResponse(
                        app.getJobPosting().getId(),
                        app.getJobPosting().getTitle(),
                        app.getJobPosting().getOrganization().getCompanyName(),
                        app.getStatus(),
                        app.getAppliedAt()
                ))
                .collect(Collectors.toList());
    }
}
