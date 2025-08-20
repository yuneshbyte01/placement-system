package com.university.placementsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Represents an organization profile linked to a {@link User}.
 * Stores company-specific information.
 */
@Entity
@Table(name = "organizations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization {

    /** Primary key, auto-generated. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Associated user account.
     * - One-to-one relationship with {@link User}.
     * - Unique per organization.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @NotNull(message = "User cannot be null")
    private User user;

    /** Company name (cannot be blank). */
    @NotBlank(message = "Company name cannot be blank")
    @Column(nullable = false, length = 150)
    private String companyName;

    /** Industry (optional). */
    @Column(length = 100)
    private String industry;

    /** Location (optional). */
    @Column(length = 150)
    private String location;

    /** Description (optional). */
    @Column(length = 500)
    private String description;
}
