package com.university.placementsystem.repository;

import com.university.placementsystem.entity.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for JobPosting entity.
 */
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    /**
     * Find all job postings created by a specific organization.
     * @param organizationId the ID of the organization
     * @return list of job postings
     */
    List<JobPosting> findByOrganizationId(Long organizationId);
}
