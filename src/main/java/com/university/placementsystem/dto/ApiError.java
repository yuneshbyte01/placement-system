package com.university.placementsystem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.OffsetDateTime;

/**
 * Standard API error response DTO.
 *
 * <p>Returned by {@code GlobalExceptionHandler} to provide clients with
 * structured error messages (status, message, timestamp).</p>
 */
@Data
@AllArgsConstructor
public class ApiError {

    // Human-readable error message
    @JsonProperty("error_message")
    private String message;

    // HTTP status code
    private int status;

    // Timestamp of the error
    private OffsetDateTime timestamp;
}
