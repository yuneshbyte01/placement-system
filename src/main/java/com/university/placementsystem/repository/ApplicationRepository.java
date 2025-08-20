package com.university.placementsystem.repository;

import com.university.placementsystem.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for Application entity.
 * Handles student applications for job postings.
 */
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    /**
     * Find all applications made by a specific student.
     */
    List<Application> findByStudent_Id(Long studentId);

    /**
     * Find all applications for a specific job posting.
     */
    List<Application> findByJobPosting_Id(Long jobPostingId);

    /**
     * Check if a student has already applied for a specific job.
     */
    boolean existsByStudent_IdAndJobPosting_Id(Long studentId, Long jobPostingId);
}
