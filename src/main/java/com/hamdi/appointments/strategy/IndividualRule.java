package com.hamdi.appointments.strategy;

import com.hamdi.appointments.domain.Appointment;

/**
 * Booking rule for individual appointments.
 * Individual booking means one user books for themselves.
 * The appointment only needs to support at least one participant.
 *
 * @author Hamdi
 * @version 2.0
 */
public class IndividualRule implements BookingRuleStrategy {

    private static final int REQUIRED_PARTICIPANTS = 1;

    /**
     * Validates that the appointment can support an individual booking.
     *
     * @param appointment the appointment to validate
     * @return true if maxParticipants >= 1
     */
    @Override
    public boolean isValid(Appointment appointment) {
        return appointment.getMaxParticipants() == REQUIRED_PARTICIPANTS;
    }

    /**
     * Returns a description of the individual booking rule.
     *
     * @return rule description string
     */
    @Override
    public String getRuleDescription() {
        return "Individual appointments require at least "
                + REQUIRED_PARTICIPANTS + " available participant slot.";
    }
}