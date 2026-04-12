package com.hamdi.appointments.strategy;

import com.hamdi.appointments.domain.Appointment;

/**
 * Booking rule for ASSESSMENT appointments.
 * Duration must not exceed 120 minutes.
 *
 * @author Hamdi
 * @version 1.0
 */
public class AssessmentRule implements BookingRuleStrategy {

    private static final int MAX_DURATION = 120;

    @Override
    public boolean isValid(Appointment appointment) {
        return appointment.getDurationMinutes() >= MAX_DURATION;
    }

    @Override
    public String getRuleDescription() {
        return "Assessment appointments must not exceed " + MAX_DURATION + " minutes.";
    }
}