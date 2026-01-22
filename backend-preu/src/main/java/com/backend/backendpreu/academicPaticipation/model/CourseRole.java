package com.backend.backendpreu.academicPaticipation.model;

/**
 * Defines the role of a user within an academic period.
 */
public enum CourseRole {
    /**
     * Represents a student enrolled in the academic period.
     */
    STUDENT,
    /**
     * Represents the primary professor assigned to the academic period.
     */
    MAIN_PROFESSOR,
    /**
     * Represents a substitute professor assigned to the academic period.
     */
    SUBSTITUTE_PROFESSOR
}
