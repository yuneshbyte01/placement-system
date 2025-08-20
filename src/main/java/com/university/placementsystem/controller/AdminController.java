package com.university.placementsystem.controller;

import com.university.placementsystem.entity.User;
import com.university.placementsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;

    /** Test endpoint */
    @GetMapping("/test")
    public ResponseEntity<?> testAdmin() {
        return ResponseEntity.ok(Map.of(
                "message", "Admin module active",
                "status", "success"
        ));
    }

    /** List all users */
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    /** Deactivate a user */
    @PutMapping("/users/{id}/deactivate")
    public ResponseEntity<?> deactivateUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setActive(false);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "message", "User deactivated",
                "userId", user.getId()
        ));
    }

    /** Activate a user */
    @PutMapping("/users/{id}/activate")
    public ResponseEntity<?> activateUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setActive(true);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "message", "User activated",
                "userId", user.getId()
        ));
    }
}
