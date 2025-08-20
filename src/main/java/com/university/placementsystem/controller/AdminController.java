package com.university.placementsystem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Entry point for all admin-related APIs.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    /**
     * Simple test endpoint to verify admin access.
     * GET /api/admin/test
     */
    @GetMapping("/test")
    public ResponseEntity<?> testAdmin() {
        return ResponseEntity.ok(
                new java.util.HashMap<>() {{
                    put("message", "Admin module active");
                    put("status", "success");
                }}
        );
    }
}
