package com.hamdi.appointments.notification;

import java.util.ArrayList;
import java.util.List;

/**
 * Sends notifications to users and records sent messages.
 *
 * @author Hamdi
 * @version 5.1
 */
public class NotificationService implements Observer {

    private final List<String> sentMessages = new ArrayList<>();
    private final EmailSender emailSender;

    private static final String SYSTEM_EMAIL = "hamdiabuayman25@gmail.com";

    public NotificationService(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public void notifyUser(String username, String message) {
        sentMessages.add(message);

        emailSender.sendEmail(
                SYSTEM_EMAIL,
                "Appointment Notification",
                message
        );
    }

    public List<String> getSentMessages() {
        return sentMessages;
    }

    public void clearMessages() {
        sentMessages.clear();
    }
}