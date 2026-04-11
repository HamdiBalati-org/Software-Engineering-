package com.hamdi.appointments.notification;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GmailEmailSenderTest {

    @Test
    void testConstructor_StoresCredentials() {
        GmailEmailSender sender = new GmailEmailSender("test@gmail.com", "password123");
        assertNotNull(sender);
    }

    @Test
    void testSendEmail_ReturnsFalse_WhenInvalidCredentials() {
        GmailEmailSender sender = new GmailEmailSender("invalid@gmail.com", "wrongpassword");
        boolean result = sender.sendEmail("to@gmail.com", "Subject", "Body");
        assertFalse(result);
    }
}