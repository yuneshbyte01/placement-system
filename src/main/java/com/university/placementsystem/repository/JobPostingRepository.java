package com.university.placementsystem.repository;

import com.university.placementsystem.entity.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for managing {@link JobPosting} entities.
 *
 * <p>Provides CRUD operations and custom queries for job postings.</p>
 */
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    // Find all job postings created by a given organization
    List<JobPosting> findByOrganizationId(Long organizationId);
}
