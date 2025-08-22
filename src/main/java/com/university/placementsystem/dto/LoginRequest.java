package com.university.placementsystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for login requests.
 *
 * <p>Used when a user attempts to log into the system.
 * Contains the email and password required for authentication.</p>
 */
@Data
public class LoginRequest {

    // User's email address (must be a valid format and not blank)
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email is not valid")
    private String email;

    // User's account password (must not be blank)
    @NotBlank(message = "Password cannot be blank")
    private String password;
}
