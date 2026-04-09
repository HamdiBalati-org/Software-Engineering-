package com.hamdi.appointments.service;

import com.hamdi.appointments.domain.Appointment;
import com.hamdi.appointments.domain.AppointmentType;
import com.hamdi.appointments.repository.AppointmentRepository;
import com.hamdi.appointments.notification.NotificationManager;
import com.hamdi.appointments.notification.NotificationService;
import com.hamdi.appointments.notification.EmailSender;
import com.hamdi.appointments.notification.GmailEmailSender;
import com.hamdi.appointments.strategy.*;
import com.hamdi.appointments.gui.LoginGUI;

import javax.swing.*;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

public class AppointmentService {

    private AppointmentRepository repo;
    private NotificationManager notificationManager;
    private boolean testMode;
    private Clock clock;
    private NotificationService notificationService;

    public AppointmentService(AppointmentRepository repo) {
        this(repo, false, Clock.systemDefaultZone(), null);
    }

    public AppointmentService(AppointmentRepository repo, boolean testMode) {
        this(repo, testMode, Clock.systemDefaultZone(), null);
    }

    public AppointmentService(AppointmentRepository repo, boolean testMode, Clock clock) {
        this(repo, testMode, clock, null);
    }

    public AppointmentService(AppointmentRepository repo, boolean testMode, Clock clock,
                              NotificationService notificationService) {

        this.repo = repo;
        this.testMode = testMode;
        this.clock = clock;

        this.notificationManager = new NotificationManager();

        if (notificationService != null) {
            this.notificationService = notificationService;
        } else {
            EmailSender emailSender =
                    new GmailEmailSender("hamdiabuayman25@gmail.com", "vewx idkq hsnh hltk");
            this.notificationService = new NotificationService(emailSender);
        }

        this.notificationManager.addObserver(this.notificationService);
    }

    private void showMessage(String message) {
        if (!testMode) {
            JOptionPane.showMessageDialog(null, message);
        }
    }

    public List<Appointment> getAvailableAppointments() {
        return repo.getAllAppointments();
    }

    public List<Appointment> getAllAppointments() {
        return repo.getAllAppointments();
    }

    private BookingRuleStrategy getRule(AppointmentType type) {
        switch (type) {
            case URGENT:     return new UrgentRule();
            case FOLLOW_UP:  return new FollowUpRule();
            case ASSESSMENT: return new AssessmentRule();
            case VIRTUAL:    return new VirtualRule();
            case IN_PERSON:  return new InPersonRule();
            case INDIVIDUAL: return new IndividualRule();
            case GROUP:      return new GroupRule();
            default:         return new VirtualRule();
        }
    }

    public void bookAppointment(String dateTime, int duration,
                                String username, AppointmentType type) {

        LocalDateTime dt = LocalDateTime.parse(dateTime);
        Appointment appointment = repo.findByDateTime(dt);

        if (appointment == null) {
            showMessage("Appointment not found.");
            return;
        }

        if (!dt.isAfter(LocalDateTime.now(clock))) {
            showMessage("It's too late! This appointment has already passed.");
            return;
        }

        if (duration != appointment.getDurationMinutes()) {
            showMessage("Invalid duration! Must be " + appointment.getDurationMinutes() + " mins.");
            return;
        }

        if (appointment.isBookedByUser(username)) {
            showMessage("You already booked this appointment!");
            return;
        }

        if (appointment.getCurrentParticipants() >= appointment.getMaxParticipants()) {
            showMessage("Appointment is fully booked.");
            return;
        }

        BookingRuleStrategy rule = getRule(type);
        if (!rule.isValid(appointment)) {
            showMessage("Booking rule violated!\n" + rule.getRuleDescription());
            return;
        }

        appointment.setTypeForUser(username, type);
        appointment.incrementParticipants(username);

        showMessage("Appointment booked successfully!\n" +
                "Type: " + type + "\n" +
                "Status: " + appointment.getStatus() + "\n" +
                "Participants: " + appointment.getCurrentParticipants());

        String message =
                "Appointment Booking Confirmation\n\n" +
                "Dear " + username + ",\n\n" +
                "Your appointment has been successfully booked.    • Here are the details:\n" +
                "  • Type : " + appointment.getTypeForUser(username) + "\n" +
                "  • Date & Time : " + appointment.getDateTime() + "\n" +
                "  • Duration : " + appointment.getDurationMinutes() + " minutes\n" +
                "  • Status : " + appointment.getStatusForUser(username) + "\n" +
                "  • Participants : " + appointment.getCurrentParticipants() +
                " / " + appointment.getMaxParticipants() + "\n\n" +
                "If you need to make any changes, please log in to the system.\n\n" +
                "Best regards,\n" +
                "Appointment Booking System.";

        notificationManager.notifyAllObservers(username, message);
    }

    public void cancelAppointment(String dateTime, String username) {

        LocalDateTime dt = LocalDateTime.parse(dateTime);
        Appointment appointment = repo.findByDateTime(dt);

        if (appointment == null) {
            showMessage("Appointment not found.");
            return;
        }

        if (!dt.isAfter(LocalDateTime.now(clock))) {
            showMessage("It's too late! Cannot cancel past appointments.");
            return;
        }

        if (!appointment.isBookedByUser(username)) {
            showMessage("You have no booking for this appointment!");
            return;
        }

        AppointmentType userType = appointment.getTypeForUser(username);
        appointment.cancelBooking(username);

        showMessage("Appointment at " + dateTime + " cancelled successfully.");

      //  AppointmentType userType = appointment.getTypeForUser(username);

      

      
        String message =
                "Appointment Cancellation Confirmation\n\n" +
                "Dear " + username + ",\n\n" +
                "Your appointment has been successfully cancelled.\n• Here is a summary:\n" +
                "  • Type : " + userType + "\n" +
                "  • Date & Time : " + appointment.getDateTime() + "\n" +
                "  • Duration : " + appointment.getDurationMinutes() + " minutes\n" +
                "  • Status : Cancelled\n\n" +
                "Best regards,\n" +
                "Appointment Booking System";

        notificationManager.notifyAllObservers(username, message);
       
    }

    public void modifyAppointment(String oldDateTime, String newDateTime, String username) {

        LocalDateTime oldDt = LocalDateTime.parse(oldDateTime);
        LocalDateTime newDt = LocalDateTime.parse(newDateTime);

        Appointment oldAppointment = repo.findByDateTime(oldDt);
        Appointment newAppointment = repo.findByDateTime(newDt);

        if (oldAppointment == null) {
            showMessage("Original appointment not found.");
            return;
        }

        if (!oldDt.isAfter(LocalDateTime.now(clock))) {
            showMessage("It's too late! Cannot modify past appointments.");
            return;
        }

        if (!oldAppointment.isBookedByUser(username)) {
            showMessage("You have no booking for this appointment!");
            return;
        }

        if (newAppointment == null) {
            showMessage("New appointment not found.");
            return;
        }

        if (newAppointment.getCurrentParticipants() >= newAppointment.getMaxParticipants()) {
            showMessage("New appointment is fully booked.");
            return;
        }

        if (newAppointment.isBookedByUser(username)) {
            showMessage("You already booked the new appointment!");
            return;
        }

        AppointmentType oldType = oldAppointment.getTypeForUser(username);
        oldAppointment.cancelBooking(username);
        newAppointment.setTypeForUser(username, oldType);
        newAppointment.incrementParticipants(username);

        showMessage("Appointment modified successfully!\n" +
                "From: " + oldDateTime + "\n" +
                "To:   " + newDateTime);

        String message =
                "Appointment Updated Successfully\n\n" +
                "Dear " + username + ",\n\n" +
                "Your appointment has been modified. Here are your new appointment details:\n\n" +
                "  • Type : " + newAppointment.getTypeForUser(username) + "\n" +
                "  • Date & Time : " + newAppointment.getDateTime() + "\n" +
                "  • Duration : " + newAppointment.getDurationMinutes() + " minutes\n" +
                "  • Status : " + newAppointment.getStatusForUser(username) + "\n" +
                "  • Participants: " + newAppointment.getCurrentParticipants() +
                " / " + newAppointment.getMaxParticipants() + "\n\n" +
                "If you need to make any changes, please log in to the system.\n\n" +
                "Best regards,\n" +
                "Appointment Booking System";

        notificationManager.notifyAllObservers(username, message);
    }

    public void adminCancelAppointment(String dateTime, String username, String adminName) {

        LocalDateTime dt = LocalDateTime.parse(dateTime);
        Appointment appointment = repo.findByDateTime(dt);

        if (appointment == null) {
            showMessage("Appointment not found.");
            return;
        }

        if (!appointment.isBookedByUser(username)) {
            showMessage(username + " has no booking for this appointment!");
            return;
        }
        AppointmentType userType = appointment.getTypeForUser(username);
        appointment.cancelBooking(username);

        if (!testMode) {
            LoginGUI.addPendingMessage(username,
                    " The Admin" + " cancelled your appointment at " + dateTime);
        }

        showMessage("Admin " +  adminName + " cancelled " + username +
                "'s appointment at " + dateTime + " successfully.");
       

        String message =
                "Appointment Cancelled by Administrator\n\n" +
                "Dear " + username + ",\n\n" +
                "Your appointment has been cancelled by the administrator. \n  • Here is a summary:\n" +
                "  • Type : " + userType + "\n" +
                "  • Date & Time : " + appointment.getDateTime() + "\n" +
                "  • Duration : " + appointment.getDurationMinutes() + " minutes\n" +
                "  • Status : Cancelled\n" +
                "  • Cancelled By : " + "The Admin of the system" + "\n\n" +
                "If you need more information, please contact the administrator.\n\n" +
                "Best regards,\n" +
                "Appointment Booking System";

        notificationManager.notifyAllObservers(username, message);
    }

    public void adminModifyAppointment(String oldDateTime, String newDateTime,
                                       String username, String adminName) {

        LocalDateTime oldDt = LocalDateTime.parse(oldDateTime);
        LocalDateTime newDt = LocalDateTime.parse(newDateTime);

        Appointment oldAppointment = repo.findByDateTime(oldDt);
        Appointment newAppointment = repo.findByDateTime(newDt);

        if (oldAppointment == null) {
            showMessage("Original appointment not found.");
            return;
        }

        if (!oldAppointment.isBookedByUser(username)) {
            showMessage(username + " has no booking for this appointment!");
            return;
        }

        if (newAppointment == null) {
            showMessage("New appointment not found.");
            return;
        }

        if (newAppointment.getCurrentParticipants() >= newAppointment.getMaxParticipants()) {
            showMessage("New appointment is fully booked.");
            return;
        }

        if (newAppointment.isBookedByUser(username)) {
            showMessage(username + " already booked the new appointment!");
            return;
        }

        AppointmentType oldType = oldAppointment.getTypeForUser(username);
        oldAppointment.cancelBooking(username);
        newAppointment.setTypeForUser(username, oldType);
        newAppointment.incrementParticipants(username);

        if (!testMode) {
            LoginGUI.addPendingMessage(username,
                    
            		"The Admin"+ " modified your appointment from "
                            + oldDateTime + " to " + newDateTime);
        }

        showMessage( "Admin "   + adminName + " modified " + username + "'s appointment!\n" +
                "From: " + oldDateTime + "\n" +
                "To:   " + newDateTime);

        String message =
                "Appointment modified by Administrator\n\n" +
                "Dear " + username + ",\n\n" +
                "Your appointment has been updated by the administrator. Here are your new appointment details:\n\n" +
                "  • Type : " + newAppointment.getTypeForUser(username) + "\n" +
                "  • Date & Time : " + newAppointment.getDateTime() + "\n" +
                "  • Duration : " + newAppointment.getDurationMinutes() + " minutes\n" +
                "  • Status : " + newAppointment.getStatusForUser(username) + "\n" +
                "  • Participants: " + newAppointment.getCurrentParticipants() +
                " / " + newAppointment.getMaxParticipants() + "\n" +
                "  • Modified By : " + "The Admin of the system" + "\n\n" +
                "If you have any questions, please contact the administrator.\n\n" +
                "Best regards,\n" +
                "Appointment Booking System";

        notificationManager.notifyAllObservers(username, message);
    }
}