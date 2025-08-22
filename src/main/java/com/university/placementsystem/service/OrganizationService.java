package com.university.placementsystem.service;

import com.university.placementsystem.dto.*;
import com.university.placementsystem.entity.JobPosting;
import com.university.placementsystem.entity.Organization;
import com.university.placementsystem.entity.User;
import com.university.placementsystem.repository.JobPostingRepository;
import com.university.placementsystem.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing organization profiles and job postings.
 *
 * <p>Handles business logic for profile creation/updates and job posting CRUD/listing.</p>
 */
@Service
@RequiredArgsConstructor
public class OrganizationService {

    // Repositories
    private final OrganizationRepository organizationRepository;
    private final JobPostingRepository jobPostingRepository;

    // ---- Messages / constants ----
    private static final String MSG_PROFILE_EXISTS = "Profile already exists";
    private static final String MSG_PROFILE_NOT_FOUND = "Organization profile not found";
    private static final String MSG_NOT_APPROVED = "Organization not approved by Admin. Cannot post jobs.";

    // ------------------- Profile Methods -------------------

    /**
     * Creates a new organization profile.
     *
     * @param userDTO logged-in user info
     * @param request profile creation request DTO
     * @throws ResponseStatusException if a profile already exists
     */
    public void createProfile(UserDTO userDTO, OrganizationCreateRequest request) {
        // Enforce one profile per user
        if (organizationRepository.findByUserEmail(userDTO.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, MSG_PROFILE_EXISTS);
        }

        // Build and persist organization profile
        Organization org = Organization.builder()
                .user(new User(userDTO.getId())) // link by user id only
                .companyName(request.getCompanyName())
                .industry(request.getIndustry())
                .location(request.getLocation())
                .description(request.getDescription())
                .approved(false)
                .build();

        organizationRepository.save(org);
    }

    /**
     * Retrieves the organization profile for a user.
     *
     * @param userDTO logged-in user info
     * @return optional {@link Organization}
     */
    public Optional<Organization> getProfile(UserDTO userDTO) {
        return organizationRepository.findByUserId(userDTO.getId());
    }

    /**
     * Updates the organization profile.
     *
     * @param userDTO logged-in user info
     * @param request profile update DTO
     * @throws ResponseStatusException if profile not found
     */
    public void updateProfile(UserDTO userDTO, OrganizationUpdateRequest request) {
        // Load or 404
        Organization org = organizationRepository.findByUserId(userDTO.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MSG_PROFILE_NOT_FOUND));

        // Apply changes
        org.setCompanyName(request.getCompanyName());
        org.setIndustry(request.getIndustry());
        org.setLocation(request.getLocation());
        org.setDescription(request.getDescription());

        organizationRepository.save(org);
    }

    // ------------------- Job Posting Methods -------------------

    /**
     * Creates a new job posting for the organization.
     *
     * @param userDTO logged-in user info
     * @param request job posting creation request DTO
     * @throws ResponseStatusException if a profile isn't found or org not approved
     */
    public void createJobPosting(UserDTO userDTO, JobPostingCreateRequest request) {
        // Ensure org profile exists
        Organization org = organizationRepository.findByUserId(userDTO.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MSG_PROFILE_NOT_FOUND));

        // Require admin approval before posting jobs
        if (!org.isApproved()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, MSG_NOT_APPROVED);
        }

        // Build and save job posting
        JobPosting job = JobPosting.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .skillsRequired(request.getSkillsRequired())
                .eligibilityCriteria(request.getEligibilityCriteria())
                .organization(org)
                .build();

        jobPostingRepository.save(job);
    }

    /**
     * Lists all job postings of the organization.
     *
     * @param userDTO logged-in user info
     * @return list of {@link JobPostingDTO}s
     * @throws ResponseStatusException if profile not found
     */
    public List<JobPostingDTO> listJobPostings(UserDTO userDTO) {
        // Ensure org profile exists
        Organization org = organizationRepository.findByUserId(userDTO.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MSG_PROFILE_NOT_FOUND));

        // Map entities to DTOs
        return jobPostingRepository.findByOrganizationId(org.getId())
                .stream()
                .map(job -> new JobPostingDTO(
                        job.getId(),
                        job.getTitle(),
                        job.getDescription(),
                        job.getSkillsRequired(),
                        job.getEligibilityCriteria(),
                        job.getCreatedAt(),
                        job.getOrganization().getCompanyName()
                ))
                .collect(Collectors.toList());
    }
}
