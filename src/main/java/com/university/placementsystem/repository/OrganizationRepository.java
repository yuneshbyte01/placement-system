package com.university.placementsystem.repository;

import com.university.placementsystem.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for Organization entity.
 * <p>
 * Extends JpaRepository to provide CRUD operations for organizations.
 * Additional query methods allow finding an organization by its associated user.
 */
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    /**
     * Find an Organization profile by the linked User ID.
     *
     * @param userId the ID of the associated User
     * @return Optional containing the Organization if found, empty otherwise
     */
    Optional<Organization> findByUserId(Long userId);

    /**
     * Find an Organization profile by the linked User's email.
     *
     * @param email the email of the associated User
     * @return Optional containing the Organization if found, empty otherwise
     */
    Optional<Organization> findByUserEmail(String email);
}
