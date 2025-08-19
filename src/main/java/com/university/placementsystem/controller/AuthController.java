package com.university.placementsystem.controller;

import com.university.placementsystem.dto.LoginRequest;
import com.university.placementsystem.dto.RegisterRequest;
import com.university.placementsystem.entity.User;
import com.university.placementsystem.entity.UserRole;
import com.university.placementsystem.repository.UserRepository;
import com.university.placementsystem.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for Authentication: Register and Login endpoints.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    /**
     * Register a new user.
     *
     * @param request registration DTO
     * @return success or error message
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request) {
        Map<String, String> response = new HashMap<>();

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            response.put("message", "Error: Email is already in use!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Build new user and save
        User user = User.builder()
                .username(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.valueOf(request.getRole().toUpperCase()))
                .build();

        userRepository.save(user);

        response.put("message", "User registered successfully!");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Login a user and generate JWT token.
     *
     * @param request login DTO
     * @return JWT token with user info or error
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest request) {
        Map<String, Object> response = new HashMap<>();

        // Find the user by email
        var userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty() || !passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())) {
            response.put("message", "Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        var user = userOpt.get();

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole(), user.getUsername());

        // Prepare response
        response.put("token", token);
        response.put("email", user.getEmail());
        response.put("role", user.getRole().name());

        return ResponseEntity.ok(response);
    }
}
