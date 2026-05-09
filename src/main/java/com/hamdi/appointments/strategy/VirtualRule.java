package com.hamdi.appointments.strategy;

import com.hamdi.appointments.domain.Appointment;

/**
 * Booking rule for virtual appointments.
 * No restrictions apply.
 *
 * @author Hamdi
 * @version 1.0
 */
public class VirtualRule implements BookingRuleStrategy {

    /**
     * Always returns true as virtual appointments have no restrictions.
     *
     * @param appointment the appointment to validate
     * @return true always
     */
    @Override
    public boolean isValid(Appointment appointment) {
        return true;
    }

    /**
     * Returns a description of the virtual booking rule.
     *
     * @return rule description string
     */
    @Override
    public String getRuleDescription() {
        return "Virtual appointments have no restrictions.";
    }
}