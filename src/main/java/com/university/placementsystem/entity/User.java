package com.university.placementsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Represents a user in the Placement System.
 * Maps to the "users" table (avoiding MySQL reserved word "user").
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /** Primary key, auto-generated */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** User's display name */
    @NotBlank(message = "Username cannot be blank")
    @Column(nullable = false, length = 100)
    private String username;

    /** Encrypted password */
    @NotBlank(message = "Password cannot be blank")
    @Column(nullable = false)
    private String password;

    /** Role of the user (STUDENT, ORG, ADMIN) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    /** Email address (unique, valid format) */
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email is not valid")
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    /** Whether the user is active (default true) */
    @Column(nullable = false)
    private boolean active = true;

    /** Constructor for referencing a user by ID only */
    public User(Long id) {
        this.id = id;
    }
}
