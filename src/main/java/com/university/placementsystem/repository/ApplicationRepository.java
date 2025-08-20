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
     * @param studentId the ID of the student
     * @return list of applications
     */
    List<Application> findByStudentId(Long studentId);

    /**
     * Find all applications for a specific job posting.
     * @param jobPostingId the ID of the job posting
     * @return list of applications
     */
    List<Application> findByJobPostingId(Long jobPostingId);

    /**
     * Check if a student has already applied for a specific job.
     * @param studentId the ID of the student
     * @param jobPostingId the ID of the job posting
     * @return true if the application exists, false otherwise
     */
    boolean existsByStudentIdAndJobPostingId(Long studentId, Long jobPostingId);
}
