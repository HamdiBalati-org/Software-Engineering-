package com.hamdi.appointments.notification;

import java.util.ArrayList;
import java.util.List;

/**

* Sends notifications to users and records sent messages.
* (Email sending removed for testing and security purposes)
*
* @author Hamdi
* @version 2.0
  */
  public class NotificationService implements Observer {

  private List<String> sentMessages = new ArrayList<>();

  /**

  * Records the notification (no real email sending).
  *
  * @param username the username to notify
  * @param message  the notification message
    */
    @Override
    public void notifyUser(String username, String message) {
    // ✅ فقط تسجيل الرسالة
    sentMessages.add("Notification for " + username + ": " + message);
    }

  /**

  * Returns all messages sent by this notification service.
  *
  * @return list of sent messages
    */
    public List<String> getSentMessages() {
    return sentMessages;
    }

  /**

  * Clears all recorded messages.
    */
    public void clearMessages() {
    sentMessages.clear();
    }
    }
