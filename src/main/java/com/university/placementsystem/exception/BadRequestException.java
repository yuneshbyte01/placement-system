package com.university.placementsystem.exception;

/**
 * Thrown when a request is invalid due to bad input or business rules.
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
