package com.hamdi.appointments.notification;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class GmailEmailSender implements EmailSender {

    private final String fromEmail;
    private final String appPassword;

    public GmailEmailSender(String fromEmail, String appPassword) {
        this.fromEmail = fromEmail;
        this.appPassword = appPassword;
    }

    @Override
    public boolean sendEmail(String to, String subject, String body) {

        String host = "smtp.gmail.com";
        String port = "587";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, appPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(to)
            );
            message.setSubject(subject);
            message.setText(body);

           Transport.send(message);
            System.out.println("Email sent successfully.");
            return true;

        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
}