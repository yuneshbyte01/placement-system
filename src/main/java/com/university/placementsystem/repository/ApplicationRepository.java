package com.university.placementsystem.repository;

import com.university.placementsystem.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for managing {@link Application} entities.
 *
 * <p>Provides query methods for accessing applications by student or job posting,
 * and for checking if a student has already applied for a specific job.</p>
 */
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // Find all applications submitted by a given student
    List<Application> findByStudent_Id(Long studentId);

    // Find all applications for a given job posting
    List<Application> findByJobPosting_Id(Long jobPostingId);

    // Check whether a student has already applied for a given job posting
    boolean existsByStudent_IdAndJobPosting_Id(Long studentId, Long jobPostingId);
}
