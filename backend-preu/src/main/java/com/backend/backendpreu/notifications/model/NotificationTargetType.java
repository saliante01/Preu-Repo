package com.backend.backendpreu.notifications.model;

/**
 * Defines the logical target type of a notification.
 */
public enum NotificationTargetType {

    /**
     * Notification is targeted to a specific user.
     */
    USER,

    /**
     * Notification is targeted to all users
     * associated with an academic period.
     */
    ACADEMIC_PERIOD
}
