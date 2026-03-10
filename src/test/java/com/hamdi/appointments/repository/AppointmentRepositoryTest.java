package com.hamdi.appointments.repository;

import com.hamdi.appointments.domain.Appointment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AppointmentRepository.
 *
 * @author Hamdi
 * @version 1.0
 */
public class AppointmentRepositoryTest {

    private AppointmentRepository repo;
    private static final LocalDateTime DT1 = LocalDateTime.of(2026, 6, 1, 10, 0);
    private static final LocalDateTime DT2 = LocalDateTime.of(2026, 6, 1, 11, 0);

    @BeforeEach
    void setUp() {
        repo = new AppointmentRepository();
        repo.addAppointment(new Appointment(DT1, 30, 3));
        repo.addAppointment(new Appointment(DT2, 60, 1));
    }

    @Test
    void testFindByDateTime_Found() {
        assertNotNull(repo.findByDateTime(DT1));
    }

    @Test
    void testFindByDateTime_NotFound() {
        assertNull(repo.findByDateTime(LocalDateTime.of(2026, 6, 1, 23, 0)));
    }

    @Test
    void testGetAllAppointments() {
        assertEquals(2, repo.getAllAppointments().size());
    }

    @Test
    void testGetAvailableAppointments_AllAvailable() {
        assertEquals(2, repo.getAvailableAppointments().size());
    }

    @Test
    void testGetAvailableAppointments_OneFullyBooked() {
        Appointment a = repo.findByDateTime(DT2);
        a.incrementParticipants("user1");
        assertEquals(1, repo.getAvailableAppointments().size());
    }
}