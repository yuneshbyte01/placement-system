package com.university.placementsystem.entity;

/**
 * Enumeration representing the status of a student's application
 * in the recruitment process.
 *
 * <p>Tracks the lifecycle of an application from submission
 * to final decision by the organization.</p>
 */
public enum ApplicationStatus {

    // Application has been submitted but not yet reviewed
    APPLIED,

    // Application has been shortlisted for further consideration
    SHORTLISTED,

    // Application has been rejected by the organization
    REJECTED,

    // Application has been accepted and the student has been selected
    SELECTED
}
