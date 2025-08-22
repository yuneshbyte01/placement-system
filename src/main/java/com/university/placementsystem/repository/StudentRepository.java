package com.university.placementsystem.repository;

import com.university.placementsystem.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing {@link Student} entities.
 *
 * <p>Provides CRUD operations and query methods for retrieving students
 * by their associated user.</p>
 */
public interface StudentRepository extends JpaRepository<Student, Long> {

    // Find a student by the linked user ID
    Optional<Student> findByUserId(Long userId);

    // Find a student by the linked user's email
    Optional<Student> findByUserEmail(String email);
}
