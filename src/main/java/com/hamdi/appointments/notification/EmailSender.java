package com.hamdi.appointments.notification;

/**
 * EmailSender interface for sending emails.
 *
 * @author Hamdi
 * @version 2.0
 */
public interface EmailSender {

    /**
     * Sends an email.
     *
     * @param to the receiver email
     * @param subject the email subject
     * @param body the email body
     * @return true if the email was sent successfully
     */
    boolean sendEmail(String to, String subject, String body);
}