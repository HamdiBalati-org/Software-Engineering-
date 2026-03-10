package com.hamdi.appointments.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a scheduled appointment in the system.
 *
 * @author Hamdi
 * @version 1.0
 */
public class Appointment {

    private LocalDateTime dateTime;
    private int durationMinutes;
    private int maxParticipants;
    private int currentParticipants;
    private String status;
    private List<String> bookedUsers;
    private Map<String, AppointmentType> userTypes; // ✅ نوع لكل مستخدم

    /**
     * Creates a new Appointment with Pending status and no type.
     *
     * @param dateTime        the date and time
     * @param durationMinutes the duration in minutes
     * @param maxParticipants the maximum participants
     */
    public Appointment(LocalDateTime dateTime, int durationMinutes, int maxParticipants) {
        this.dateTime            = dateTime;
        this.durationMinutes     = durationMinutes;
        this.maxParticipants     = maxParticipants;
        this.currentParticipants = 0;
        this.status              = "Pending";
        this.bookedUsers         = new ArrayList<>();
        this.userTypes           = new HashMap<>();
    }

    /**
     * Creates a new Appointment with a predefined type.
     *
     * @param dateTime        the date and time
     * @param durationMinutes the duration in minutes
     * @param maxParticipants the maximum participants
     * @param type            the appointment type
     */
    public Appointment(LocalDateTime dateTime, int durationMinutes,
                       int maxParticipants, AppointmentType type) {
        this.dateTime            = dateTime;
        this.durationMinutes     = durationMinutes;
        this.maxParticipants     = maxParticipants;
        this.currentParticipants = 0;
        this.status              = "Pending";
        this.bookedUsers         = new ArrayList<>();
        this.userTypes           = new HashMap<>();
    }

    /** @return the appointment date and time */
    public LocalDateTime getDateTime() { return dateTime; }

    /** @return duration in minutes */
    public int getDurationMinutes() { return durationMinutes; }

    /** @return max participants */
    public int getMaxParticipants() { return maxParticipants; }

    /** @return current participants */
    public int getCurrentParticipants() { return currentParticipants; }

    /** @return appointment status */
    public String getStatus() { return status; }

    /** @return booked users list */
    public List<String> getBookedUsers() { return bookedUsers; }

    /**
     * Returns the type chosen by a specific user.
     *
     * @param username the username
     * @return the appointment type or null
     */
    public AppointmentType getTypeForUser(String username) {
        return userTypes.get(username);
    }

    /**
     * Returns the userTypes map.
     *
     * @return map of username to AppointmentType
     */
    public Map<String, AppointmentType> getUserTypes() { return userTypes; }

    /**
     * Sets the appointment status.
     *
     * @param status the new status
     */
    public void setStatus(String status) { this.status = status; }

    /**
     * Sets the type for a specific user.
     *
     * @param username the username
     * @param type     the appointment type
     */
    public void setTypeForUser(String username, AppointmentType type) {
        userTypes.put(username, type);
    }

    /**
     * Checks if a user already booked this appointment.
     *
     * @param username the username to check
     * @return true if already booked
     */
    public boolean isBookedByUser(String username) {
        return bookedUsers.contains(username);
    }

    /**
     * Increments participants and sets status to Confirmed.
     *
     * @param username the booking username
     */
    public void incrementParticipants(String username) {
        if (currentParticipants < maxParticipants && !isBookedByUser(username)) {
            currentParticipants++;
            bookedUsers.add(username);
            this.status = "Confirmed";
        }
    }

    /**
     * Cancels a booking for a specific user.
     *
     * @param username the username to cancel
     * @return true if cancelled successfully
     */
    public boolean cancelBooking(String username) {
        if (!isBookedByUser(username)) return false;
        bookedUsers.remove(username);
        userTypes.remove(username); // ✅ نمسح النوع مع الحجز
        currentParticipants--;
        this.status = currentParticipants > 0 ? "Confirmed" : "Pending";
        return true;
    }
}