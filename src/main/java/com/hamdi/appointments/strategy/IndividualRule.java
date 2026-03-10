package com.hamdi.appointments.strategy;

import com.hamdi.appointments.domain.Appointment;

/**
 * Booking rule for individual appointments.
 * Allows exactly 1 participant.
 *
 * @author Hamdi
 * @version 1.0
 */
public class IndividualRule implements BookingRuleStrategy {

    private static final int MAX_PARTICIPANTS = 1;

    /**
     * Validates that the appointment allows exactly 1 participant.
     *
     * @param appointment the appointment to validate
     * @return true if maxParticipants == 1
     */
    @Override
    public boolean isValid(Appointment appointment) {
        return appointment.getMaxParticipants() == MAX_PARTICIPANTS;
    }

    /**
     * Returns a description of the individual booking rule.
     *
     * @return rule description string
     */
    @Override
    public String getRuleDescription() {
        return "Individual appointments allow only " + MAX_PARTICIPANTS + " participant.";
    }
}