package com.hamdi.appointments.notification;

import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;

/**
 * Sends notifications to users via GUI dialog and records sent messages.
 *
 * @author Hamdi
 * @version 1.0
 */
public class NotificationService implements Observer {

    // ✅ قائمة لحفظ الرسائل المبعوتة - مطلوبة للـ Tests
    private List<String> sentMessages = new ArrayList<>();

    /**
     * Displays a notification message to the user via a GUI dialog
     * and records the message for testing purposes.
     *
     * @param username the username to notify
     * @param message  the notification message
     */
    @Override
    public void notifyUser(String username, String message) {
        // ✅ حفظ الرسالة
        sentMessages.add("Notification for " + username + ": " + message);

        // ✅ عرض الرسالة على GUI
        JOptionPane.showMessageDialog(null,
            "Notification for " + username + ":\n" + message);
    }

    /**
     * Returns all messages sent by this notification service.
     * Used in test mode to verify notifications were sent.
     *
     * @return list of sent messages
     */
    public List<String> getSentMessages() {
        return sentMessages;
    }

    /**
     * Clears all recorded messages.
     * Used between tests to reset state.
     */
    public void clearMessages() {
        sentMessages.clear();
    }
}