package com.backend.backendpreu.users.model;

/**
 * Defines the role of a user within the system.
 * <p>
 * Roles are used for authorization and access control.
 */
public enum Role {

    /**
     * Student enrolled in one or more academic periods.
     */
    STUDENT,

    /**
     * Professor responsible for teaching courses
     * and managing academic content.
     */
    PROFESSOR,

    /**
     * System administrator with full management privileges.
     */
    ADMIN
}
