package com.university.placementsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Student profile entity linked to a User.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Associated user account
     */
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @NotNull(message = "User cannot be null")
    private User user;

    @NotBlank(message = "University cannot be blank")
    private String university;

    @NotBlank(message = "Degree cannot be blank")
    private String degree;

    /**
     * Graduation year must be between 1900 and 2100
     */
    @NotNull(message = "Graduation year cannot be null")
    @Min(value = 1900, message = "Graduation year must be at least 1900")
    @Max(value = 2100, message = "Graduation year must be at most 2100")
    @Column(name = "graduation_year")
    private Integer graduationYear;

    /**
     * Comma-separated skills for now
     */
    private String skills;

    /**
     * Path to an uploaded resume file
     */
    private String resumePath;
}
