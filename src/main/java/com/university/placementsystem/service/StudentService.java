package com.university.placementsystem.service;

import com.university.placementsystem.dto.StudentCreateRequest;
import com.university.placementsystem.dto.StudentDTO;
import com.university.placementsystem.dto.StudentUpdateRequest;
import com.university.placementsystem.entity.Student;
import com.university.placementsystem.entity.User;
import com.university.placementsystem.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Service for managing student profiles and resume uploads.
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Create, update, and retrieve student profiles</li>
 *   <li>Handle PDF resume uploads with validations</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class StudentService {

    // Repository dependency
    private final StudentRepository studentRepository;

    // Upload directory (configurable via application.properties)
    @Value("${student.upload-dir:uploads/resumes}")
    private String uploadDir;

    // ---- Constants ----
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String PDF_CONTENT_TYPE = "application/pdf";
    private static final String MSG_PROFILE_EXISTS = "Profile already exists";
    private static final String MSG_PROFILE_NOT_FOUND = "STUDENT profile not found";
    private static final String MSG_INVALID_FILE = "Invalid file: Only PDF under 5MB allowed";

    /**
     * Retrieve student profile by user email.
     *
     * @param email user email
     * @return StudentDTO with profile data
     * @throws ResponseStatusException if profile not found
     */
    public StudentDTO getProfile(String email) {
        return studentRepository.findByUserEmail(email)
                .map(student -> new StudentDTO(
                        student.getUniversity(),
                        student.getDegree(),
                        student.getGraduationYear(),
                        student.getSkills(),
                        student.getResumePath(),
                        student.getUser().getEmail()
                ))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MSG_PROFILE_NOT_FOUND));
    }

    /**
     * Create a new student profile.
     *
     * @param email    user email
     * @param request  profile creation DTO
     * @param userId   ID of the logged-in user
     * @throws ResponseStatusException if the profile already exists
     */
    public void createProfile(String email, StudentCreateRequest request, Long userId) {
        // Check if a profile already exists for this email
        if (studentRepository.findByUserEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, MSG_PROFILE_EXISTS);
        }

        // Build a new profile entity
        Student student = Student.builder()
                .user(new User(userId)) // associate with a user by ID
                .university(request.getUniversity())
                .degree(request.getDegree())
                .graduationYear(request.getGraduationYear())
                .skills(request.getSkills())
                .build();

        studentRepository.save(student);
    }

    /**
     * Update an existing student profile.
     *
     * @param email   user email
     * @param request profile update DTO
     * @throws ResponseStatusException if profile not found
     */
    public void updateProfile(String email, StudentUpdateRequest request) {
        // Lookup profile or throw if missing
        Student student = studentRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MSG_PROFILE_NOT_FOUND));

        // Apply updates
        student.setUniversity(request.getUniversity());
        student.setDegree(request.getDegree());
        student.setGraduationYear(request.getGraduationYear());
        student.setSkills(request.getSkills());

        studentRepository.save(student);
    }

    /**
     * Upload a PDF resume for the student.
     *
     * @param email user email
     * @param file  Multipart PDF file
     * @return path where the file is saved
     * @throws ResponseStatusException for invalid file or storage error
     */
    public String uploadResume(String email, MultipartFile file) {
        validateFile(file);

        try {
            // Ensure directory exists
            Path dirPath = Path.of(uploadDir).toAbsolutePath();
            if (!Files.exists(dirPath)) Files.createDirectories(dirPath);

            // Generate a unique file name
            String fileName = sanitizeFileName(email) + "_" + UUID.randomUUID() + ".pdf";
            Path destination = dirPath.resolve(fileName);

            // Save file
            Files.copy(file.getInputStream(), destination, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            // Update the profile with a resume path
            Student student = studentRepository.findByUserEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MSG_PROFILE_NOT_FOUND));

            student.setResumePath(destination.toString());
            studentRepository.save(student);

            return destination.toString();

        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to store file: " + ex.getMessage());
        }
    }

    // ---- Private helpers ----

    /** Validate PDF file type and size */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()
                || !PDF_CONTENT_TYPE.equalsIgnoreCase(file.getContentType())
                || file.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, MSG_INVALID_FILE);
        }
    }

    /** Sanitize email for safe file names */
    private String sanitizeFileName(String email) {
        return email.replaceAll("[^a-zA-Z0-9]", "_");
    }
}
