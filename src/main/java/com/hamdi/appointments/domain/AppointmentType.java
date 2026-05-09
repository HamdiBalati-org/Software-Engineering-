package com.hamdi.appointments.domain;

/**
 * Represents the type of an appointment.
 * Each type has different booking rules applied via the Strategy Pattern.
 *
 * @author Hamdi
 * @version 1.0
 */
public enum AppointmentType {

    /** Urgent appointment - duration must not exceed 30 minutes. */
    URGENT,

    /** Follow-up appointment - duration must not exceed 60 minutes. */
    FOLLOW_UP,

    /** Assessment appointment - duration must not exceed 120 minutes. */
    ASSESSMENT,

    /** Virtual appointment - no restrictions. */
    VIRTUAL,

    /** In-person appointment - max 10 participants. */
    IN_PERSON,

    /** Individual appointment - exactly 1 participant. */
    INDIVIDUAL,

    /** Group appointment - at least 2 participants. */
    GROUP
}