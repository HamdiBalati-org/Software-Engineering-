package com.hamdi.appointments.strategy;

import com.hamdi.appointments.domain.Appointment;

/**
 * Booking rule for urgent appointments.
 * Duration must not exceed 30 minutes.
 *
 * @author Hamdi
 * @version 1.0
 */
public class UrgentRule implements BookingRuleStrategy {

    private static final int MAX_DURATION = 30;

    /**
     * Validates that the appointment duration does not exceed 30 minutes.
     *
     * @param appointment the appointment to validate
     * @return true if durationMinutes <= 30
     */
    @Override
    public boolean isValid(Appointment appointment) {
        return appointment.getDurationMinutes() <= MAX_DURATION;
    }

    /**
     * Returns a description of the urgent booking rule.
     *
     * @return rule description string
     */
    @Override
    public String getRuleDescription() {
        return "Urgent appointments must not exceed " + MAX_DURATION + " minutes.";
    }
}