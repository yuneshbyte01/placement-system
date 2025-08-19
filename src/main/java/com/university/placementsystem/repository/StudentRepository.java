package com.university.placementsystem.repository;

import com.university.placementsystem.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for Student entity.
 */
public interface StudentRepository extends JpaRepository<Student, Long> {

    /**
     * Find a Student profile by the linked User ID.
     * @param userId the ID of the User
     * @return optional Student
     */
    Optional<Student> findByUserId(Long userId);

    /**
     * Find a Student profile by the linked User's email.
     * @param email the email of the User
     * @return optional Student
     */
    Optional<Student> findByUserEmail(String email);
}
