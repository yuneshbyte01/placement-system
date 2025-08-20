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
     * Otherwise, saves the user with an encoded password and role.
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

        User user = User.builder()
                .username(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(UserRole.valueOf(registerRequest.getRole().toUpperCase()))
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
     * @param loginRequest request body containing email and password
     * @return ResponseEntity with JWT token and user details
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());

        if (userOpt.isEmpty() ||
                !passwordEncoder.matches(loginRequest.getPassword(), userOpt.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", MSG_INVALID_CREDENTIALS));
        }

        User user = userOpt.get();
        String token = jwtUtil.generateToken(
                String.valueOf(user.getId()),
                user.getEmail(),
                user.getRole(),
                user.getUsername()
        );

        return ResponseEntity.ok(Map.of(
                "token", token,
                "email", user.getEmail(),
                "role", user.getRole().name()
        ));
    }
}
