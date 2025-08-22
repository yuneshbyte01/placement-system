package com.university.placementsystem.service;

import com.university.placementsystem.dto.AuthResponse;
import com.university.placementsystem.dto.LoginRequest;
import com.university.placementsystem.dto.RegisterRequest;
import com.university.placementsystem.entity.User;
import com.university.placementsystem.entity.UserRole;
import com.university.placementsystem.exception.AuthenticationException;
import com.university.placementsystem.exception.BadRequestException;
import com.university.placementsystem.exception.ForbiddenException;
import com.university.placementsystem.repository.UserRepository;
import com.university.placementsystem.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for handling authentication and user registration.
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Register new users with role validation</li>
 *   <li>Authenticate users and generate JWT tokens</li>
 *   <li>Validate user account status</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    // Dependencies
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // ---- Constant messages ----
    private static final String MSG_EMAIL_USED = "Email already in use";
    private static final String MSG_INVALID_ROLE = "Invalid role";
    private static final String MSG_ADMIN_FORBIDDEN = "Cannot assign ADMIN role via registration";
    private static final String MSG_INVALID_CREDENTIALS = "Invalid credentials";
    private static final String MSG_ACCOUNT_DEACTIVATED = "User account is deactivated";

    /**
     * Registers a new user in the system.
     *
     * @param request DTO containing registration data
     * @return persisted {@link User} entity
     * @throws BadRequestException   if email already in use or role is invalid
     * @throws ForbiddenException    if attempting to register an ADMIN via public API
     */
    public User register(RegisterRequest request) {
        // Reject if an active user already registered with this email
        if (userRepository.existsByEmailAndActiveTrue(request.getEmail())) {
            throw new BadRequestException(MSG_EMAIL_USED);
        }

        // Determine role (default to STUDENT)
        String roleStr = request.getRole() != null ? request.getRole().toUpperCase() : "STUDENT";

        // Prevent creating ADMIN via registration API
        if ("ADMIN".equals(roleStr)) {
            throw new ForbiddenException(MSG_ADMIN_FORBIDDEN);
        }

        // Validate role string
        final UserRole role;
        try {
            role = UserRole.valueOf(roleStr);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(MSG_INVALID_ROLE);
        }

        // Build new User (password hashed)
        User user = User.builder()
                .username(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .active(true)
                .build();

        // Persist and return
        return userRepository.save(user);
    }

    /**
     * Authenticates a user and generates a JWT token.
     *
     * @param request DTO containing login credentials
     * @return {@link AuthResponse} containing JWT token and user info
     * @throws AuthenticationException if credentials are invalid
     * @throws ForbiddenException      if the account is deactivated
     */
    public AuthResponse login(LoginRequest request) {
        // Fetch an active user by email or fail fast (active=true narrows the query)
        User user = userRepository.findByEmailAndActiveTrue(request.getEmail())
                .orElseThrow(() -> new AuthenticationException(MSG_INVALID_CREDENTIALS));

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthenticationException(MSG_INVALID_CREDENTIALS);
        }

        // Defensive: ensure an account is active (should already be true from a query)
        if (!user.isActive()) {
            throw new ForbiddenException(MSG_ACCOUNT_DEACTIVATED);
        }

        // Generate JWT token for the authenticated user
        String token = jwtUtil.generateToken(
                String.valueOf(user.getId()),
                user.getEmail(),
                user.getRole(),
                user.getUsername()
        );

        // Return token + minimal user info
        return new AuthResponse(token, user.getEmail(), user.getRole().name());
    }
}
