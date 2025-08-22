package com.university.placementsystem.dto;

import com.university.placementsystem.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO representing an authenticated user.
 *
 * <p>Populated by the authentication system and injected into controllers.</p>
 */
@Data
@AllArgsConstructor
public class UserDTO {

    // Unique ID of the user
    private Long id;

    // Username of the user
    private String name;

    // Email address of the user
    private String email;

    // Role of the user (e.g., STUDENT, ADMIN, RECRUITER)
    private UserRole role;
}
