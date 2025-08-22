package com.university.placementsystem.repository;

import com.university.placementsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link User} entities.
 *
 * <p>Provides database operations for authentication and account management.</p>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find an active user by email (used in login)
    Optional<User> findByEmailAndActiveTrue(String email);

    // Check if an active user already exists with the given email
    boolean existsByEmailAndActiveTrue(String email);
}
