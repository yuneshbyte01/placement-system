package com.university.placementsystem.exception;

/**
 * Thrown when access is denied (e.g., insufficient role or disabled account).
 */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
