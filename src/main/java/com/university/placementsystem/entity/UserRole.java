package com.university.placementsystem.entity;

/**
 * Enumeration of user roles within the Placement System.
 *
 * <p>Each role defines the level of access and the actions a user can perform.
 * Roles are also mapped to Spring Security authorities.</p>
 */
public enum UserRole {

    // A student who can view opportunities and apply for job postings
    STUDENT,

    // An organization that can create job postings and manage applications
    ORGANIZATION,

    // An administrator with full system access and management privileges
    ADMIN;

    /**
     * Returns the role in Spring Security authority format.
     *
     * <p>Example: {@code STUDENT -> "ROLE_STUDENT"}.</p>
     *
     * @return the role name prefixed with {@code ROLE_}
     */
    public String asAuthority() {
        return "ROLE_" + this.name();
    }

    /**
     * Returns a user-friendly string representation of the role.
     *
     * <p>Example: {@code STUDENT -> "Student"}.</p>
     *
     * @return the role name in display-friendly format
     */
    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
