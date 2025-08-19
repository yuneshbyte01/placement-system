package com.university.placementsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Represents a User entity in the Placement System.
 * This entity maps to the "users" table in the database.
 *
 * Features:
 * - Uses JPA annotations for persistence.
 * - Includes validation constraints for input fields.
 * - Lombok annotations reduce boilerplate code.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users") // "user" is reserved in MySQL, so we use "users"
public class User {

    /** Primary key, auto-generated (identity strategy for MySQL). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** User's display name (cannot be blank). */
    @NotBlank(message = "Username cannot be blank")
    private String username;

    /** Encrypted password (cannot be blank). */
    @NotBlank(message = "Password cannot be blank")
    private String password;

    /**
     * Role of the user in the system.
     * Stored as a String (e.g., STUDENT, ORG, ADMIN).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    /**
     * Email address of the user.
     * - Must be unique (DB constraint).
     * - Must be a valid email format.
     * - Cannot be blank.
     */
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email is not valid")
    @Column(nullable = false, unique = true)
    private String email;
}
