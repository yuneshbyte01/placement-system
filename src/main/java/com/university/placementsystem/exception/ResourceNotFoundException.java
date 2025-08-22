package com.university.placementsystem.exception;

/**
 * Thrown when a requested resource (e.g., User, Student, JobPosting) is not found.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
