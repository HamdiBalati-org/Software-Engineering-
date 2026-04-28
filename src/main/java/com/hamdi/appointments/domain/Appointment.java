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
 * @version 2.0
 */
/*
Appointment class is a domain class that represents a scheduled appointment in the system.
*/



public class Appointment {

    private static final String STATUS_PENDING = "Pending";
    private static final String STATUS_CONFIRMED = "Confirmed";
    private static final String STATUS_FULL = "FULL";

    private LocalDateTime dateTime;
    private int durationMinutes;
    private int maxParticipants;
    private int currentParticipants;
    private String status;
    private List<String> bookedUsers;
    private Map<String, AppointmentType> userTypes;

        /**
     * Creates a new Appointment with Pending status.
     *
     * @param dateTime        the date and time
     * @param durationMinutes the duration in minutes
     * @param maxParticipants the maximum participants
     */

    public Appointment(LocalDateTime dateTime, int durationMinutes, int maxParticipants) {
        this.dateTime = dateTime;
        this.durationMinutes = durationMinutes;
        this.maxParticipants = maxParticipants;
        this.currentParticipants = 0;
        this.status = STATUS_PENDING;
        this.bookedUsers = new ArrayList<>();
        this.userTypes = new HashMap<>();
    }

    public Appointment(LocalDateTime dateTime, int durationMinutes,
                       int maxParticipants, AppointmentType type) {
        this.dateTime = dateTime;
        this.durationMinutes = durationMinutes;
        this.maxParticipants = maxParticipants;
        this.currentParticipants = 0;
        this.status = STATUS_PENDING;
        this.bookedUsers = new ArrayList<>();
        this.userTypes = new HashMap<>();
        this.userTypes.put("DEFAULT", type);
    }

    /** @return the appointment date and time */
    public LocalDateTime getDateTime() {
        return dateTime;
    }
/** @return duration in minutes */
    public int getDurationMinutes() {
        return durationMinutes;
    }
/** @return max participants */
    public int getMaxParticipants() {
        return maxParticipants;
    }
/** @return current participants */
    public int getCurrentParticipants() {
        return currentParticipants;
    }

    /**
     * Returns the general appointment status.
     *
     * @return appointment status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Returns the status as seen by a specific user.
     * Booked users see Confirmed.
     * Other users see FULL if the appointment is full.
     *
     * @param username the username
     * @return status for that user
     */
    public String getStatusForUser(String username) {
        if (isBookedByUser(username)) {
            return STATUS_CONFIRMED;
        }

        if (currentParticipants >= maxParticipants) {
            return STATUS_FULL;
        }

        return status;
    }

    public List<String> getBookedUsers() {
        return bookedUsers;
    }

    public AppointmentType getTypeForUser(String username) {
        return userTypes.get(username);
    }

    public Map<String, AppointmentType> getUserTypes() {
        return userTypes;
    }

      /**
     * Sets the appointment status.
     *
     * @param status the new status
     */
    public void setStatus(String status) {
        this.status = status;
    }

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
     * Increments participants and updates status.
     *
     * @param username the booking username
     */
    public void incrementParticipants(String username) {
        if (currentParticipants < maxParticipants && !isBookedByUser(username)) {
            currentParticipants++;
            bookedUsers.add(username);

            if (currentParticipants >= maxParticipants) {
                this.status = STATUS_FULL;
            } else {
                this.status = STATUS_CONFIRMED;
            }
        }
    }

        /**
     * Cancels a booking for a specific user.
     *
     * @param username the username to cancel
     * @return true if cancelled successfully
     */

    public boolean cancelBooking(String username) {
        if (!isBookedByUser(username)) {
            return false;
        }

        bookedUsers.remove(username);
        userTypes.remove(username);
        currentParticipants--;

        if (currentParticipants <= 0) {
            this.status = STATUS_PENDING;
        } else if (currentParticipants < maxParticipants) {
            this.status = STATUS_CONFIRMED;
        } else {
            this.status = STATUS_FULL;
        }

        return true;
    }
}