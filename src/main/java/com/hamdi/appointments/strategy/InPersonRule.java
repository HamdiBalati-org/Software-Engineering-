package com.hamdi.appointments.strategy;

import com.hamdi.appointments.domain.Appointment;

/**
 * Booking rule for in-person appointments.
 * Allows a maximum of 10 participants.
 *
 * @author Hamdi
 * @version 1.0
 */
public class InPersonRule implements BookingRuleStrategy {

    private static final int MAX_PARTICIPANTS = 10;

    /**
     * Validates that the appointment does not exceed 10 participants.
     *
     * @param appointment the appointment to validate
     * @return true if maxParticipants <= 10
     */
    @Override
    public boolean isValid(Appointment appointment) {
        return appointment.getMaxParticipants() <= MAX_PARTICIPANTS;
    }

    /**
     * Returns a description of the in-person booking rule.
     *
     * @return rule description string
     */
    @Override
    public String getRuleDescription() {
        return "In-person appointments allow max " + MAX_PARTICIPANTS + " participants.";
    }
}