package com.hamdi.appointments.strategy;

import com.hamdi.appointments.domain.Appointment;

/**
 * Booking rule for FOLLOW_UP appointments.
 * Duration must not exceed 60 minutes.
 *
 * @author Hamdi
 * @version 1.0
 */
public class FollowUpRule implements BookingRuleStrategy {

    private static final int MAX_DURATION = 60;

    @Override
    public boolean isValid(Appointment appointment) {
        return appointment.getDurationMinutes() <= MAX_DURATION;
    }

    @Override
    public String getRuleDescription() {
        return "Follow-up appointments must not exceed " + MAX_DURATION + " minutes.";
    }
}