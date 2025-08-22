package com.university.placementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO representing the response after a successful login.
 *
 * <p>Contains the JWT token and basic user information.</p>
 */
@Data
@AllArgsConstructor
public class AuthResponse {

    // JWT token issued upon successful login
    private String token;

    // Email of the authenticated user
    private String email;

    // Role of the authenticated user (e.g., STUDENT, ORGANIZATION, ADMIN)
    private String role;
}
