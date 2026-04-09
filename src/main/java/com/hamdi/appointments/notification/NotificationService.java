package com.hamdi.appointments.notification;

import java.util.ArrayList;
import java.util.List;

/**
 * Sends notifications to users and records sent messages.
 * Also sends email notifications asynchronously to avoid GUI freezing.
 *
 * @author Hamdi
 * @version 5.0
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

        // إرسال الإيميل بالخلفية حتى لا يعلق الـ GUI
        new Thread(() -> {
            emailSender.sendEmail(
                    SYSTEM_EMAIL,
                    "Appointment Notification",
                    message
            );
        }).start();
    }

    public List<String> getSentMessages() {
        return sentMessages;
    }

    public void clearMessages() {
        sentMessages.clear();
    }
}