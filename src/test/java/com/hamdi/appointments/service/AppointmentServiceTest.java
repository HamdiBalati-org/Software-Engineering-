package com.hamdi.appointments.service;

import com.hamdi.appointments.domain.Appointment;
import com.hamdi.appointments.domain.AppointmentType;
import com.hamdi.appointments.repository.AppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AppointmentService.
 *
 * @author Hamdi
 * @version 1.0
 */
public class AppointmentServiceTest {

    private AppointmentRepository repo;
    private AppointmentService service;

    private static final LocalDateTime DT1 = LocalDateTime.of(2026, 6, 1, 10, 0);
    private static final LocalDateTime DT2 = LocalDateTime.of(2026, 6, 1, 11, 0);

    @BeforeEach
    void setUp() {
        repo = new AppointmentRepository();
        repo.addAppointment(new Appointment(DT1, 30, 3));
        repo.addAppointment(new Appointment(DT2, 60, 1));
        service = new AppointmentService(repo, true);
    }

    @Test
    void testBookAppointment_Success() {
        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.VIRTUAL);
        Appointment a = repo.findByDateTime(DT1);
        assertEquals(1, a.getCurrentParticipants());
        assertEquals("Confirmed", a.getStatus());
        assertEquals(AppointmentType.VIRTUAL, a.getTypeForUser("user1"));
    }

    @Test
    void testBookAppointment_NotFound() {
        service.bookAppointment("2026-06-01T23:00", 30, "user1", AppointmentType.VIRTUAL);
        assertEquals(0, repo.findByDateTime(DT1).getCurrentParticipants());
    }

    @Test
    void testBookAppointment_WrongDuration() {
        service.bookAppointment("2026-06-01T10:00", 99, "user1", AppointmentType.VIRTUAL);
        assertEquals(0, repo.findByDateTime(DT1).getCurrentParticipants());
    }

    @Test
    void testBookAppointment_AlreadyBooked() {
        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.VIRTUAL);
        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.VIRTUAL);
        assertEquals(1, repo.findByDateTime(DT1).getCurrentParticipants());
    }

    @Test
    void testBookAppointment_FullyBooked() {
        service.bookAppointment("2026-06-01T11:00", 60, "user1", AppointmentType.VIRTUAL);
        service.bookAppointment("2026-06-01T11:00", 60, "user2", AppointmentType.VIRTUAL);
        assertEquals(1, repo.findByDateTime(DT2).getCurrentParticipants());
    }

    @Test
    void testBookAppointment_MultipleUsers_DifferentTypes() {
        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.URGENT);
        service.bookAppointment("2026-06-01T10:00", 30, "user2", AppointmentType.VIRTUAL);
        Appointment a = repo.findByDateTime(DT1);
        assertEquals(AppointmentType.URGENT,  a.getTypeForUser("user1"));
        assertEquals(AppointmentType.VIRTUAL, a.getTypeForUser("user2"));
    }

    @Test
    void testBookAppointment_RuleViolated_Individual() {
        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.INDIVIDUAL);
        assertEquals(0, repo.findByDateTime(DT1).getCurrentParticipants());
    }

    @Test
    void testBookAppointment_RuleViolated_UrgentDuration() {
        service.bookAppointment("2026-06-01T11:00", 60, "user1", AppointmentType.URGENT);
        assertEquals(0, repo.findByDateTime(DT2).getCurrentParticipants());
    }

    @Test
    void testCancelAppointment_Success() {
        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.VIRTUAL);
        service.cancelAppointment("2026-06-01T10:00", "user1");
        Appointment a = repo.findByDateTime(DT1);
        assertEquals(0, a.getCurrentParticipants());
        assertEquals("Pending", a.getStatus());
    }

    @Test
    void testCancelAppointment_NotBooked() {
        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.VIRTUAL);
        service.cancelAppointment("2026-06-01T10:00", "user2");
        assertEquals(1, repo.findByDateTime(DT1).getCurrentParticipants());
    }

    @Test
    void testCancelAppointment_NotFound() {
        service.cancelAppointment("2026-06-01T23:00", "user1");
        assertEquals(0, repo.findByDateTime(DT1).getCurrentParticipants());
    }

    @Test
    void testModifyAppointment_Success() {
        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.VIRTUAL);
        service.modifyAppointment("2026-06-01T10:00", "2026-06-01T11:00", "user1");
        assertEquals(0, repo.findByDateTime(DT1).getCurrentParticipants());
        assertEquals(1, repo.findByDateTime(DT2).getCurrentParticipants());
    }

    @Test
    void testModifyAppointment_TypeTransferred() {
        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.VIRTUAL);
        service.modifyAppointment("2026-06-01T10:00", "2026-06-01T11:00", "user1");
        assertEquals(AppointmentType.VIRTUAL, repo.findByDateTime(DT2).getTypeForUser("user1"));
    }

    @Test
    void testModifyAppointment_NotBooked() {
        service.modifyAppointment("2026-06-01T10:00", "2026-06-01T11:00", "user1");
        assertEquals(0, repo.findByDateTime(DT2).getCurrentParticipants());
    }

    @Test
    void testAdminCancelAppointment_Success() {
        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.VIRTUAL);
        service.adminCancelAppointment("2026-06-01T10:00", "user1", "admin");
        assertEquals(0, repo.findByDateTime(DT1).getCurrentParticipants());
    }

    @Test
    void testAdminCancelAppointment_UserNotBooked() {
        service.adminCancelAppointment("2026-06-01T10:00", "user1", "admin");
        assertEquals(0, repo.findByDateTime(DT1).getCurrentParticipants());
    }

    @Test
    void testAdminModifyAppointment_Success() {
        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.VIRTUAL);
        service.adminModifyAppointment("2026-06-01T10:00", "2026-06-01T11:00", "user1", "admin");
        assertEquals(0, repo.findByDateTime(DT1).getCurrentParticipants());
        assertEquals(1, repo.findByDateTime(DT2).getCurrentParticipants());
    }

    @Test
    void testAdminModifyAppointment_UserNotBooked() {
        service.adminModifyAppointment("2026-06-01T10:00", "2026-06-01T11:00", "user1", "admin");
        assertEquals(0, repo.findByDateTime(DT2).getCurrentParticipants());
    }

    @Test
    void testGetAvailableAppointments() {
        assertEquals(2, service.getAvailableAppointments().size());
        service.bookAppointment("2026-06-01T11:00", 60, "user1", AppointmentType.VIRTUAL);
        assertEquals(1, service.getAvailableAppointments().size());
    }
}