package com.university.placementsystem.controller;

import com.university.placementsystem.dto.OrganizationCreateRequest;
import com.university.placementsystem.dto.OrganizationDTO;
import com.university.placementsystem.dto.OrganizationUpdateRequest;
import com.university.placementsystem.dto.UserDTO;
import com.university.placementsystem.entity.Organization;
import com.university.placementsystem.entity.User;
import com.university.placementsystem.repository.OrganizationRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/organization")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationRepository organizationRepository;

    // ------------------- Profile Endpoints -------------------

    @GetMapping("/test")
    public ResponseEntity<?> testOrganizationEndpoint(Authentication authentication) {
        UserDTO user = (UserDTO) authentication.getPrincipal();
        return ResponseEntity.ok(Map.of(
                "message", "ORGANIZATION endpoint accessed successfully",
                "email", user.getEmail(),
                "role", user.getRole().name()
        ));
    }

    @PostMapping("/profile")
    public ResponseEntity<?> createProfile(Authentication authentication,
                                           @Valid @RequestBody OrganizationCreateRequest request) {
        UserDTO user = (UserDTO) authentication.getPrincipal();

        if (organizationRepository.findByUserEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Profile already exists"));
        }

        // Create organization profile
        Organization organization = Organization.builder()
                .user(new User(user.getId()))
                .companyName(request.getCompanyName())
                .industry(request.getIndustry())
                .location(request.getLocation())
                .description(request.getDescription())
                .build();

        organizationRepository.save(organization);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Organization profile created successfully"));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        UserDTO user = (UserDTO) authentication.getPrincipal();

        Optional<Organization> orgOpt = organizationRepository.findByUserEmail(user.getEmail());

        return orgOpt
                .map(org -> new OrganizationDTO(
                        org.getCompanyName(),
                        org.getIndustry(),
                        org.getLocation(),
                        org.getDescription(),
                        org.getUser().getEmail()
                ))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body((OrganizationDTO) Map.of("message", "Organization profile not found")));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(Authentication authentication,
                                           @Valid @RequestBody OrganizationUpdateRequest request) {
        UserDTO user = (UserDTO) authentication.getPrincipal();

        Optional<Organization> orgOpt = organizationRepository.findByUserEmail(user.getEmail());

        if (orgOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Organization profile not found"));
        }

        Organization organization = orgOpt.get();
        organization.setCompanyName(request.getCompanyName());
        organization.setIndustry(request.getIndustry());
        organization.setLocation(request.getLocation());
        organization.setDescription(request.getDescription());

        organizationRepository.save(organization);

        return ResponseEntity.ok(Map.of("message", "Profile updated successfully"));
    }
}
