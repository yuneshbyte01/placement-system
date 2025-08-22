package com.university.placementsystem.service;

import com.university.placementsystem.dto.ApplicationDTO;
import com.university.placementsystem.dto.JobPostingDTO;
import com.university.placementsystem.dto.OrganizationDTO;
import com.university.placementsystem.entity.Application;
import com.university.placementsystem.entity.JobPosting;
import com.university.placementsystem.entity.Organization;
import com.university.placementsystem.entity.User;
import com.university.placementsystem.repository.ApplicationRepository;
import com.university.placementsystem.repository.JobPostingRepository;
import com.university.placementsystem.repository.OrganizationRepository;
import com.university.placementsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin service for managing users, organizations, job postings, and applications.
 *
 * <p>Provides read/update operations used by administrative endpoints.</p>
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    // Repositories
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final JobPostingRepository jobPostingRepository;
    private final ApplicationRepository applicationRepository;

    // ---- Messages / constants ----
    private static final String MSG_USER_NOT_FOUND = "User not found";
    private static final String MSG_ORG_NOT_FOUND = "Organization not found";
    private static final String MSG_APP_NOT_FOUND = "Application not found";

    // ---------- Users ----------

    /**
     * Returns all users in the system.
     *
     * @return list of users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Deactivates a user account.
     *
     * @param id user id
     * @throws ResponseStatusException if the user does not exist
     */
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MSG_USER_NOT_FOUND));
        user.setActive(false);
        userRepository.save(user);
    }

    /**
     * Activates a user account.
     *
     * @param id user id
     * @throws ResponseStatusException if the user does not exist
     */
    public void activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MSG_USER_NOT_FOUND));
        user.setActive(true);
        userRepository.save(user);
    }

    // ---------- Organizations ----------

    /**
     * Returns all organizations.
     *
     * @return list of organization DTOs
     */
    public List<OrganizationDTO> getAllOrganizations() {
        return organizationRepository.findAll()
                .stream()
                .map(this::toOrganizationDTO)
                .collect(Collectors.toList());
    }

    /**
     * Returns organizations that are pending approval.
     *
     * @return list of pending organization DTOs
     */
    public List<OrganizationDTO> getPendingOrganizations() {
        return organizationRepository.findByApprovedFalse()
                .stream()
                .map(this::toOrganizationDTO)
                .collect(Collectors.toList());
    }

    /**
     * Approves an organization.
     *
     * @param id organization id
     * @throws ResponseStatusException if the organization does not exist
     */
    public void approveOrganization(Long id) {
        Organization org = organizationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MSG_ORG_NOT_FOUND));
        org.setApproved(true);
        organizationRepository.save(org);
    }

    /**
     * Rejects an organization (sets approved=false).
     *
     * @param id organization id
     * @throws ResponseStatusException if the organization does not exist
     */
    public void rejectOrganization(Long id) {
        Organization org = organizationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MSG_ORG_NOT_FOUND));
        org.setApproved(false);
        organizationRepository.save(org);
        // If you intend to delete on reject, replace with: organizationRepository.delete(org);
    }

    // ---------- Jobs ----------

    /**
     * Returns all job postings across organizations.
     *
     * @return list of job posting DTOs
     */
    public List<JobPostingDTO> getAllJobPostings() {
        return jobPostingRepository.findAll()
                .stream()
                .map(this::toJobPostingDTO)
                .collect(Collectors.toList());
    }

    // ---------- Applications ----------

    /**
     * Returns all applications across organizations.
     *
     * @return list of application DTOs
     */
    public List<ApplicationDTO> getAllApplications() {
        return applicationRepository.findAll()
                .stream()
                .map(this::toApplicationDTO)
                .collect(Collectors.toList());
    }

    /**
     * Returns a single application by id.
     *
     * @param id application id
     * @return application DTO
     * @throws ResponseStatusException if the application does not exist
     */
    public ApplicationDTO getApplicationById(Long id) {
        return applicationRepository.findById(id)
                .map(this::toApplicationDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MSG_APP_NOT_FOUND));
    }

    // ---------- Mappers ----------

    // Maps Organization entity to DTO
    private OrganizationDTO toOrganizationDTO(Organization org) {
        return new OrganizationDTO(
                org.getCompanyName(),
                org.getIndustry(),
                org.getLocation(),
                org.getDescription(),
                org.isApproved(),
                org.getUser().getEmail()
        );
    }

    // Maps JobPosting entity to DTO
    private JobPostingDTO toJobPostingDTO(JobPosting job) {
        return new JobPostingDTO(
                job.getId(),
                job.getTitle(),
                job.getDescription(),
                job.getSkillsRequired(),
                job.getEligibilityCriteria(),
                job.getCreatedAt(),
                job.getOrganization().getCompanyName()
        );
    }

    // Maps Application entity to DTO
    private ApplicationDTO toApplicationDTO(Application app) {
        return new ApplicationDTO(
                app.getId(),
                app.getStudent().getUser().getUsername(),
                app.getStudent().getUser().getEmail(),
                app.getJobPosting().getTitle(),
                app.getJobPosting().getOrganization().getCompanyName(),
                app.getStatus().name()
        );
    }
}
