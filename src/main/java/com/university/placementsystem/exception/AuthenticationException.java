package com.university.placementsystem.exception;

/**
 * Thrown when authentication fails due to invalid credentials or token issues.
 */
public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
}
