package com.hamdi.appointments.notification;

public class FakeEmailSender implements EmailSender {

    @Override
    public boolean sendEmail(String to, String subject, String body) {
        return true;
    }
}