package com.hamdi.appointments.strategy;

import com.hamdi.appointments.domain.Appointment;

/**
 * Booking rule for group appointments.
 * Requires at least 2 participants.
 *
 * @author Hamdi
 * @version 1.0
 */
public class GroupRule implements BookingRuleStrategy {

    private static final int MIN_PARTICIPANTS = 2;

    /**
     * Validates that the appointment supports at least 2 participants.
     *
     * @param appointment the appointment to validate
     * @return true if maxParticipants >= 2
     */
    @Override
    public boolean isValid(Appointment appointment) {
        return appointment.getMaxParticipants() >= MIN_PARTICIPANTS;
    }

    /**
     * Returns a description of the group booking rule.
     *
     * @return rule description string
     */
    @Override
    public String getRuleDescription() {
        return "Group appointments require at least " + MIN_PARTICIPANTS + " participants.";
    }
}