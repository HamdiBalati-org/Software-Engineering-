package com.hamdi.appointments.notification;

/**
 * Observer interface for the notification system.
 * Implementing classes receive user notifications.
 *
 * @author Hamdi
 * @version 1.0
 */
public interface Observer {

    /**
     * Sends a notification to a specific user.
     *
     * @param username the username to notify
     * @param message  the notification message
     */
    void notifyUser(String username, String message);
}
