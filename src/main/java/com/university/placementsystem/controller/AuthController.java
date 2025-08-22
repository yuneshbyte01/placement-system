package com.university.placementsystem.controller;

import com.university.placementsystem.dto.LoginRequest;
import com.university.placementsystem.dto.RegisterRequest;
import com.university.placementsystem.dto.AuthResponse;
import com.university.placementsystem.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for user authentication and registration endpoints.
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Expose REST endpoints for registration and login</li>
 *   <li>Translate service exceptions into HTTP responses</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    // Dependencies
    private final AuthService authService;

    // ---- Messages / constants ----
    private static final String MSG_REGISTERED = "Registered successfully";
    private static final String MSG_INTERNAL   = "Internal server error";

    /**
     * Endpoint for registering a new user.
     *
     * @param request DTO containing registration info
     * @return 201 on success; 400 for invalid input; 500 for unexpected errors
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            // Delegate to service; returns persisted user (unused here)
            authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(MSG_REGISTERED);
        } catch (IllegalArgumentException ex) {
            // Bad input / business rule violation
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            // Fallback for unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MSG_INTERNAL);
        }
    }

    /**
     * Endpoint for logging in a user.
     *
     * @param request DTO containing email and password
     * @return 200 with JWT token and user info; 401 for invalid credentials; 403 if deactivated; 500 otherwise
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // Delegate to service to authenticate and issue JWT
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            // Invalid credentials
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        } catch (IllegalStateException ex) {
            // Account deactivated or forbidden
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (Exception ex) {
            // Fallback for unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MSG_INTERNAL);
        }
    }
}
