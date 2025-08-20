package com.university.placementsystem.entity;

/**
 * Enum representing different user roles in the Placement System.
 * <p>
 * Each role defines the level of access and actions a user can perform.
 */
public enum UserRole {

    /** A student user who can apply for job postings and view opportunities. */
    STUDENT,

    /** An organization user who can post jobs and manage applications. */
    ORGANIZATION,

    /** An administrator with full system access and management privileges. */
    ADMIN;

    /**
     * Returns the role in Spring Security format (e.g., "ROLE_STUDENT").
     *
     * @return prefixed role name
     */
    public String asAuthority() {
        return "ROLE_" + this.name();
    }

    /**
     * Returns a user-friendly string (capitalized).
     *
     * @return display-friendly role
     */
    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
