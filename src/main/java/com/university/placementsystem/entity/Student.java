package com.university.placementsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Represents a student profile linked to a {@link User}.
 * <p>
 * Stores academic details, skills, and resume information.
 */
@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    /** Primary key, auto-generated. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Associated user account.
     * - One-to-one relationship with {@link User}.
     * - Unique per student.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @NotNull(message = "User cannot be null")
    private User user;

    /** University name (cannot be blank). */
    @NotBlank(message = "University cannot be blank")
    @Column(nullable = false, length = 150)
    private String university;

    /** Degree name (cannot be blank). */
    @NotBlank(message = "Degree cannot be blank")
    @Column(nullable = false, length = 100)
    private String degree;

    /**
     * Graduation year.
     * Must be between 1900 and 2100.
     */
    @NotNull(message = "Graduation year cannot be null")
    @Min(value = 1900, message = "Graduation year must be at least 1900")
    @Max(value = 2100, message = "Graduation year must be at most 2100")
    @Column(name = "graduation_year", nullable = false)
    private Integer graduationYear;

    /**
     * Skills (comma-separated for now).
     * <p>
     * In the future, consider normalizing into a separate table
     * or storing as JSON.
     */
    @Column(length = 500)
    private String skills;

    /**
     * File system path to the uploaded resume (PDF).
     */
    @Column(name = "resume_path")
    private String resumePath;
}
