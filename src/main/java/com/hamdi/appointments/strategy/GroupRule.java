package com.hamdi.appointments.strategy;

import com.hamdi.appointments.domain.Appointment;

/**
 * Booking rule for group appointments.
 * Group booking means the appointment must support at least two participants.
 *
 * @author Hamdi
 * @version 2.0
 */
public class GroupRule implements BookingRuleStrategy {

    private static final int REQUIRED_PARTICIPANTS = 2;

    /**
     * Validates that the appointment can support a group booking.
     *
     * @param appointment the appointment to validate
     * @return true if maxParticipants >= 2
     */
    @Override
    public boolean isValid(Appointment appointment) {
        return appointment.getMaxParticipants() >= REQUIRED_PARTICIPANTS;
    }

    /**
     * Returns a description of the group booking rule.
     *
     * @return rule description string
     */
    @Override
    public String getRuleDescription() {
        return "Group appointments require at least "
                + REQUIRED_PARTICIPANTS + " available participant slots.";
    }
}