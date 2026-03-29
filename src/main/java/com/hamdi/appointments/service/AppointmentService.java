package com.hamdi.appointments.service;

import com.hamdi.appointments.domain.Appointment;
import com.hamdi.appointments.domain.AppointmentType;
import com.hamdi.appointments.repository.AppointmentRepository;
import com.hamdi.appointments.notification.NotificationManager;
import com.hamdi.appointments.notification.NotificationService;
import com.hamdi.appointments.strategy.*;
import com.hamdi.appointments.gui.LoginGUI;

import javax.swing.*;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Coordinates appointment booking, cancellation, and modification.
 *
 * @author Hamdi
 * @version 1.0
 */
public class AppointmentService {

    private AppointmentRepository repo;
    private NotificationManager notificationManager;
    private boolean testMode;
    private Clock clock;

    /**
     * Creates an AppointmentService for production use.
     *
     * @param repo the appointment repository
     */
    public AppointmentService(AppointmentRepository repo) {
        this.repo      = repo;
        this.testMode  = false;
        this.clock     = Clock.systemDefaultZone();
        this.notificationManager = new NotificationManager();
        this.notificationManager.addObserver(new NotificationService());
    }

    /**
     * Creates an AppointmentService for testing without GUI dialogs.
     *
     * @param repo     the appointment repository
     * @param testMode true to disable GUI dialogs
     */
    public AppointmentService(AppointmentRepository repo, boolean testMode) {
        this.repo      = repo;
        this.testMode  = testMode;
        this.clock     = Clock.systemDefaultZone();
        this.notificationManager = new NotificationManager();
        this.notificationManager.addObserver(new NotificationService());
    }

    /**
     * Creates an AppointmentService for testing with a custom clock.
     *
     * @param repo     the appointment repository
     * @param testMode true to disable GUI dialogs
     * @param clock    the clock to use for time-based operations
     */
    public AppointmentService(AppointmentRepository repo, boolean testMode, Clock clock) {
        this.repo      = repo;
        this.testMode  = testMode;
        this.clock     = clock;
        this.notificationManager = new NotificationManager();
        this.notificationManager.addObserver(new NotificationService());
    }

    /**
     * Shows a message dialog in production mode only.
     *
     * @param message the message to display
     */
    private void showMessage(String message) {
        if (!testMode) {
            JOptionPane.showMessageDialog(null, message);
        }
    }

    /**
     * Returns all available appointments.
     *
     * @return list of available appointments
     */
    public List<Appointment> getAvailableAppointments() {
        return repo.getAvailableAppointments();
    }

    /**
     * Returns all appointments for admin use.
     *
     * @return list of all appointments
     */
    public List<Appointment> getAllAppointments() {
        return repo.getAllAppointments();
    }

    /**
     * Returns the booking rule for the given appointment type.
     *
     * @param type the appointment type
     * @return the corresponding BookingRuleStrategy
     */
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

    /**
     * Books an appointment for a specific user with a chosen type.
     *
     * @param dateTime the date and time string
     * @param duration the requested duration in minutes
     * @param username the booking username
     * @param type     the appointment type chosen by the user
     */
    public void bookAppointment(String dateTime, int duration,
                                String username, AppointmentType type) {

        LocalDateTime dt = LocalDateTime.parse(dateTime);
        Appointment appointment = repo.findByDateTime(dt);

        if (appointment == null) {
            showMessage("Appointment not found.");
            return;
        }

        // ✅ check إذا الموعد فات
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
                "Type: "         + type + "\n" +
                "Status: "       + appointment.getStatus() + "\n" +
                "Participants: " + appointment.getCurrentParticipants());

        notificationManager.notifyAllObservers(username,
                "Your " + type + " appointment at " + dateTime + " is confirmed.");
    }

    /**
     * Cancels a user's booking. Only future appointments can be cancelled.
     *
     * @param dateTime the appointment date and time string
     * @param username the username requesting cancellation
     */
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

        appointment.cancelBooking(username);

        showMessage("Appointment at " + dateTime + " cancelled successfully.");

        notificationManager.notifyAllObservers(username,
                "Your appointment at " + dateTime + " has been cancelled.");
    }

    /**
     * Modifies a user's booking from one slot to another.
     *
     * @param oldDateTime the current appointment date and time
     * @param newDateTime the new appointment date and time
     * @param username    the username requesting modification
     */
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

        notificationManager.notifyAllObservers(username,
                "Your appointment has been modified to " + newDateTime);
    }

    /**
     * Allows admin to cancel any user's booking.
     *
     * @param dateTime  the appointment date and time
     * @param username  the target username
     * @param adminName the admin performing the action
     */
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

        appointment.cancelBooking(username);

        // ✅ رسالة للمستخدم تنتظره
        if (!testMode) {
            LoginGUI.addPendingMessage(username,
                "Admin '" + adminName + "' cancelled your appointment at " + dateTime);
        }

        showMessage("Admin " + adminName + " cancelled " + username +
                "'s appointment at " + dateTime + " successfully.");

        notificationManager.notifyAllObservers(username,
                "Your appointment at " + dateTime + " was cancelled by admin.");
    }

    /**
     * Allows admin to modify any user's booking.
     *
     * @param oldDateTime the current appointment date and time
     * @param newDateTime the new appointment date and time
     * @param username    the target username
     * @param adminName   the admin performing the action
     */
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

        // ✅ رسالة للمستخدم تنتظره
        if (!testMode) {
            LoginGUI.addPendingMessage(username,
                "Admin '" + adminName + "' modified your appointment from "
                + oldDateTime + " to " + newDateTime);
        }

        showMessage("Admin " + adminName + " modified " + username + "'s appointment!\n" +
                "From: " + oldDateTime + "\n" +
                "To:   " + newDateTime);

        notificationManager.notifyAllObservers(username,
                "Your appointment was modified to " + newDateTime + " by admin.");
    }
}
