package com.hamdi.appointments.notification;

public class NotificationService implements Observer {

    @Override
    public void notifyUser(String username, String message) {
        System.out.println("Notification for " + username + ": " + message);
    }
}
