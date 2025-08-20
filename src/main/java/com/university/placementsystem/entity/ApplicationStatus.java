package com.university.placementsystem.entity;

/**
 * Enum representing the status of a student's application to a job.
 * <p>
 * This enum is used to track the progress of a student's application
 */
public enum ApplicationStatus {

    /** The application has been submitted but not yet reviewed. */
    APPLIED,

    /** The application has been shortlisted for further consideration. */
    SHORTLISTED,

    /** The application has been rejected by the organization. */
    REJECTED,

    /** The application has been accepted and the student has been selected. */
    SELECTED
}
