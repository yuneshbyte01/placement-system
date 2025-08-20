package com.university.placementsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Represents a user in the Placement System.
 * <p>
 * Features:
 * - Uses JPA annotations for persistence.
 * - Includes validation constraints for input fields.
 * - Lombok annotations reduce boilerplate code.
 * - Maps to the "users" table (instead of "user", which is reserved in MySQL).
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /** Primary key, auto-generated (IDENTITY strategy for MySQL). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** User's display name (cannot be blank). */
    @NotBlank(message = "Username cannot be blank")
    @Column(nullable = false, length = 100)
    private String username;

    /** Encrypted password (cannot be blank). */
    @NotBlank(message = "Password cannot be blank")
    @Column(nullable = false)
    private String password;

    /** Role of the user in the system (e.g., STUDENT, ORG, ADMIN). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    /**
     * Email address of the user.
     * - Must be unique (DB constraint).
     * - Must be a valid email format.
     * - Cannot be blank.
     */
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email is not valid")
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    /**
     * Constructor for referencing a user by ID only.
     * Useful when setting relationships without fetching the full entity.
     */
    public User(Long id) {
        this.id = id;
    }
}
