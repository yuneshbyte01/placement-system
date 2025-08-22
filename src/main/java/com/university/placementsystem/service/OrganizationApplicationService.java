package com.university.placementsystem.service;

import com.university.placementsystem.dto.UserDTO;
import com.university.placementsystem.entity.*;
import com.university.placementsystem.repository.ApplicationRepository;
import com.university.placementsystem.repository.JobPostingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/**
 * Service for organizations to manage application statuses for their job postings.
 *
 * <p>Performs role checks, ownership validation, and safe status transitions.</p>
 */
@Service
@RequiredArgsConstructor
public class OrganizationApplicationService {

    // Data access
    private final ApplicationRepository applicationRepository;
    private final JobPostingRepository jobPostingRepository;

    // ---- Messages / constants ----
    private static final String MSG_ROLE_REQUIRED = "Access denied: ORG role required";
    private static final String MSG_JOB_NOT_FOUND = "Job posting not found";
    private static final String MSG_NOT_OWNER = "You do not own this job posting";
    private static final String MSG_APPLICATION_NOT_FOUND = "Application not found";
    private static final String MSG_APPLICATION_NOT_BELONG = "Application does not belong to this job";
    private static final String MSG_CANNOT_UPDATE_FINALIZED = "Cannot update application already finalized";
    private static final String MSG_APPLICATION_UPDATED = "Application status updated";

    /**
     * Updates the status of an application for a job posting owned by the organization.
     *
     * @param orgUser        authenticated organization user
     * @param jobId          ID of the job posting
     * @param applicationId  ID of the application to update
     * @param newStatus      new status to set
     * @return a small response map with message and updated info
     * @throws ResponseStatusException if role/ownership checks fail or data is invalid
     */
    public Map<String, Object> updateApplicationStatus(UserDTO orgUser,
                                                       Long jobId,
                                                       Long applicationId,
                                                       ApplicationStatus newStatus) {
        // Require ORGANIZATION role
        if (orgUser.getRole() != UserRole.ORGANIZATION) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, MSG_ROLE_REQUIRED);
        }

        // Load job and verify ownership
        JobPosting job = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MSG_JOB_NOT_FOUND));

        if (!job.getOrganization().getUser().getId().equals(orgUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, MSG_NOT_OWNER);
        }

        // Load the application and ensure it belongs to the job
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MSG_APPLICATION_NOT_FOUND));

        if (!application.getJobPosting().getId().equals(jobId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, MSG_APPLICATION_NOT_BELONG);
        }

        // Disallow changing finalized statuses
        if (application.getStatus() == ApplicationStatus.SELECTED
                || application.getStatus() == ApplicationStatus.REJECTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, MSG_CANNOT_UPDATE_FINALIZED);
        }

        // Persist new status
        application.setStatus(newStatus);
        applicationRepository.save(application);

        // Return minimal confirmation payload
        return Map.of(
                "message", MSG_APPLICATION_UPDATED,
                "applicationId", application.getId(),
                "newStatus", newStatus.toString()
        );
    }
}
