package com.university.placementsystem.repository;

import com.university.placementsystem.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link Organization} entities.
 *
 * <p>Provides CRUD operations and query methods to retrieve organizations
 * by their associated user or approval status.</p>
 */
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    // Find an organization by the linked user ID
    Optional<Organization> findByUserId(Long userId);

    // Find an organization by the linked user's email
    Optional<Organization> findByUserEmail(String email);

    // Find all organizations that are not approved
    List<Organization> findByApprovedFalse();
}
