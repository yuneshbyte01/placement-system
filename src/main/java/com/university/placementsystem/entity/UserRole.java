package com.university.placementsystem.entity;

/**
 * Enum representing different user roles in the Placement System.
 * Each role defines the level of access and actions a user can perform.
 */
public enum UserRole {

    /** A student user who can apply for job postings and view opportunities. */
    STUDENT,

    /** An organization user who can post jobs and manage applications. */
    ORGANIZATION,

    /** An administrator with full system access and management privileges. */
    ADMIN
}
