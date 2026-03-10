package com.hamdi.appointments.strategy;

import com.hamdi.appointments.domain.Appointment;

/**
 * Strategy interface for appointment booking rules.
 *
 * @author Hamdi
 * @version 1.0
 */
public interface BookingRuleStrategy {

    /**
     * Validates whether the appointment meets the rule requirements.
     *
     * @param appointment the appointment to validate
     * @return true if valid
     */
    boolean isValid(Appointment appointment);

    /**
     * Returns a description of the rule.
     *
     * @return rule description
     */
    String getRuleDescription();
}