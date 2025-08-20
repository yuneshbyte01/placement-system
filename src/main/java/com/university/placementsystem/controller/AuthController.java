package com.university.placementsystem.controller;

import com.university.placementsystem.dto.LoginRequest;
import com.university.placementsystem.dto.RegisterRequest;
import com.university.placementsystem.entity.User;
import com.university.placementsystem.entity.UserRole;
import com.university.placementsystem.repository.UserRepository;
import com.university.placementsystem.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

/**
 * Authentication Controller.
 * <p>
 * Provides endpoints for:
 * - Registering new users
 * - Logging in users and returning JWT tokens
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // --- Response Messages ---
    private static final String MSG_EMAIL_USED = "Email already in use";
    private static final String MSG_REGISTER_SUCCESS = "Registered successfully";
    private static final String MSG_INVALID_CREDENTIALS = "Invalid credentials";

    /**
     * Register a new user.
     * <p>
     * If the email is already taken, returns HTTP 400.
     * If the role is not provided, defaults to "STUDENT".
     * If the role is "ADMIN", returns HTTP 403.
     * Otherwise, save the user with an encoded password and role.
     *
     * @param registerRequest request body containing user registration data
     * @return ResponseEntity with status and message
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", MSG_EMAIL_USED));
        }

        // Default role if none provided
        String requestedRole = registerRequest.getRole() != null
                ? registerRequest.getRole().toUpperCase()
                : "STUDENT";

        // Prevent creating ADMIN via API
        if (requestedRole.equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Cannot assign ADMIN role via registration"));
        }

        UserRole role;
        try {
            role = UserRole.valueOf(requestedRole);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Invalid role"));
        }

        User user = User.builder()
                .username(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(role)
                .active(true)
                .build();

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", MSG_REGISTER_SUCCESS));
    }


    /**
     * Authenticate user and return JWT token.
     * <p>
     * If credentials are invalid, returns HTTP 401.
     *
     * @param request request body containing email and password
     * @return ResponseEntity with JWT token and user details
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        if (!user.isActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Your account is deactivated. Contact admin."));
        }

        String token = jwtUtil.generateToken(String.valueOf(user.getId()), user.getEmail(), user.getRole(),
                user.getUsername());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "email", user.getEmail(),
                "role", user.getRole().name()
        ));
    }

}
