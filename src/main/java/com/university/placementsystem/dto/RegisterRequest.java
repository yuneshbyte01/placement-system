package com.university.placementsystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for handling new user registration requests.
 *
 * <p>Encapsulates the basic user information required
 * to create a new account in the system.</p>
 */
@Data
public class RegisterRequest {

    // Full name of the user
    @NotBlank(message = "Name cannot be blank")
    private String name;

    // Email address of the user (must be a valid format)
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email is not valid")
    private String email;

    // Password chosen by the user
    @NotBlank(message = "Password cannot be blank")
    private String password;

    // Role of the user (e.g., STUDENT, ORGANIZATION, ADMIN)
    @NotBlank(message = "Role cannot be blank")
    private String role;
}
