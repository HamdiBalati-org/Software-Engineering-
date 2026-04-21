package com.hamdi.appointments.UandAandA;

import java.time.LocalDateTime;

import com.hamdi.appointments.domain.Appointment;
import com.hamdi.appointments.repository.AppointmentRepository;
import com.hamdi.appointments.service.AuthService;

/**
 * Seeds initial users, administrators, and appointments for the system.
 *
 * @author Hamdi
 * @version 1.0
 */
public class Data {

    /**
     * Adds initial administrators, users, and appointments.
     *
     * @param repo the appointment repository
     * @param auth the authentication service
     */
    public static void initialize(AppointmentRepository repo, AuthService auth) {
        if (repo == null || auth == null) {
            throw new IllegalArgumentException("Repository and AuthService must not be null.");
        }

        // Administrators
        auth.addAdministrator("abood", "1234");
        auth.addAdministrator("hamdi", "1122");
        auth.addAdministrator("hamood", "1122");
        // Users
        auth.addUser("user1", "1234");
        auth.addUser("user2", "1234");
        auth.addUser("user3", "1234");

        // Appointments
        repo.addAppointment(new Appointment(LocalDateTime.of(2026, 6, 1, 10, 0), 25, 1));
        repo.addAppointment(new Appointment(LocalDateTime.of(2026, 6, 1, 11, 0), 60, 3));
        repo.addAppointment(new Appointment(LocalDateTime.of(2026, 6, 1, 12, 0), 45, 5));
        repo.addAppointment(new Appointment(LocalDateTime.of(2026, 6, 1, 13, 0), 30, 1));
        repo.addAppointment(new Appointment(LocalDateTime.of(2026, 6, 1, 14, 0), 60, 2));
        repo.addAppointment(new Appointment(LocalDateTime.of(2026, 6, 1, 15, 0), 90, 3));
        repo.addAppointment(new Appointment(LocalDateTime.of(2026, 3, 1, 16, 0), 45, 8));
        
    }

    /**
     * Prevent instantiation.
     */
    private Data() {
    }
}