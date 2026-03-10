package com.hamdi.appointments.repository;

import com.hamdi.appointments.domain.Appointment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * In-memory repository for storing and retrieving appointments.
 *
 * @author Hamdi
 * @version 1.0
 */
public class AppointmentRepository {

    private List<Appointment> appointments = new ArrayList<>();

    /**
     * Adds a new appointment to the repository.
     *
     * @param a the appointment to add
     */
    public void addAppointment(Appointment a) {
        appointments.add(a);
    }

    /**
     * Finds an appointment by its date and time.
     *
     * @param dt the date and time to search for
     * @return the matching appointment or null if not found
     */
    public Appointment findByDateTime(LocalDateTime dt) {
        for (Appointment a : appointments) {
            if (a.getDateTime().equals(dt)) {
                return a;
            }
        }
        return null;
    }

    /**
     * Returns all appointments that are not fully booked.
     *
     * @return list of available appointments
     */
    public List<Appointment> getAvailableAppointments() {
        List<Appointment> available = new ArrayList<>();
        for (Appointment a : appointments) {
            if (a.getCurrentParticipants() < a.getMaxParticipants()) {
                available.add(a);
            }
        }
        return available;
    }

    /**
     * Returns all appointments in the system.
     *
     * @return list of all appointments
     */
    public List<Appointment> getAllAppointments() {
        return appointments; // ← للأدمن يشوف الكل
    }
}