package com.hamdi.appointments.repository;

import com.hamdi.appointments.domain.Appointment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AppointmentRepository.
 *
 * @author Hamdi
 * @version 2.0
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
    void testGetAllAppointments_ContainsExpectedAppointments() {
        List<Appointment> all = repo.getAllAppointments();

        assertTrue(all.stream().anyMatch(a -> a.getDateTime().equals(DT1)));
        assertTrue(all.stream().anyMatch(a -> a.getDateTime().equals(DT2)));
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
        assertTrue(repo.getAvailableAppointments().stream()
                .noneMatch(app -> app.getDateTime().equals(DT2)));
    }

    @Test
    void testAddAppointment_NewAppointmentCanBeRetrieved() {
        LocalDateTime dt3 = LocalDateTime.of(2026, 6, 1, 12, 0);
        Appointment newAppointment = new Appointment(dt3, 45, 2);

        repo.addAppointment(newAppointment);

        assertNotNull(repo.findByDateTime(dt3));
        assertEquals(3, repo.getAllAppointments().size());
    }
}