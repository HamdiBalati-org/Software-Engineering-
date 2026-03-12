package com.hamdi.appointments.service;

import com.hamdi.appointments.domain.Appointment;
import com.hamdi.appointments.domain.AppointmentType;
import com.hamdi.appointments.repository.AppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

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

    // ==================== bookAppointment ====================

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
        // INDIVIDUAL → maxParticipants لازم == 1، لكن DT1 عنده max=3
        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.INDIVIDUAL);
        assertEquals(0, repo.findByDateTime(DT1).getCurrentParticipants());
    }

    @Test
    void testBookAppointment_RuleViolated_UrgentDuration() {
        // URGENT → duration لازم <= 30، هون 60
        service.bookAppointment("2026-06-01T11:00", 60, "user1", AppointmentType.URGENT);
        assertEquals(0, repo.findByDateTime(DT2).getCurrentParticipants());
    }

    // ==================== cancelAppointment ====================

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

    // ==================== modifyAppointment ====================

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

    // ==================== adminCancelAppointment ====================

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

    // ==================== adminModifyAppointment ====================

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

    // ==================== getAvailableAppointments ====================

    @Test
    void testGetAvailableAppointments() {
        assertEquals(2, service.getAvailableAppointments().size());
        service.bookAppointment("2026-06-01T11:00", 60, "user1", AppointmentType.VIRTUAL);
        assertEquals(1, service.getAvailableAppointments().size());
    }

    // ==================== Clock / Time Mocking Tests ====================

    @Test
    void testCancelAppointment_PastAppointment_Rejected() {
        // ✅ Clock مستقبلي → الموعد يبدو في الماضي
        Clock futureClock = Clock.fixed(
            LocalDateTime.of(2027, 1, 1, 0, 0).toInstant(ZoneOffset.UTC),
            ZoneId.of("UTC")
        );
        AppointmentService futureService = new AppointmentService(repo, true, futureClock);

        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.VIRTUAL);
        futureService.cancelAppointment("2026-06-01T10:00", "user1");

        // ✅ الإلغاء رُفض لأن الموعد في الماضي
        assertEquals(1, repo.findByDateTime(DT1).getCurrentParticipants());
    }

    @Test
    void testModifyAppointment_PastAppointment_Rejected() {
        // ✅ Clock مستقبلي → الموعد يبدو في الماضي
        Clock futureClock = Clock.fixed(
            LocalDateTime.of(2027, 1, 1, 0, 0).toInstant(ZoneOffset.UTC),
            ZoneId.of("UTC")
        );
        AppointmentService futureService = new AppointmentService(repo, true, futureClock);

        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.VIRTUAL);
        futureService.modifyAppointment("2026-06-01T10:00", "2026-06-01T11:00", "user1");

        // ✅ التعديل رُفض لأن الموعد في الماضي
        assertEquals(0, repo.findByDateTime(DT2).getCurrentParticipants());
    }

    @Test
    void testCancelAppointment_FutureAppointment_Accepted() {
        // ✅ Clock ماضي → الموعد يبدو في المستقبل
        Clock pastClock = Clock.fixed(
            LocalDateTime.of(2025, 1, 1, 0, 0).toInstant(ZoneOffset.UTC),
            ZoneId.of("UTC")
        );
        AppointmentService pastService = new AppointmentService(repo, true, pastClock);

        pastService.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.VIRTUAL);
        pastService.cancelAppointment("2026-06-01T10:00", "user1");

        // ✅ الإلغاء نجح
        assertEquals(0, repo.findByDateTime(DT1).getCurrentParticipants());
        assertEquals("Pending", repo.findByDateTime(DT1).getStatus());
    }

    @Test
    void testModifyAppointment_FutureAppointment_Accepted() {
        // ✅ Clock ماضي → الموعد يبدو في المستقبل
        Clock pastClock = Clock.fixed(
            LocalDateTime.of(2025, 1, 1, 0, 0).toInstant(ZoneOffset.UTC),
            ZoneId.of("UTC")
        );
        AppointmentService pastService = new AppointmentService(repo, true, pastClock);

        pastService.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.VIRTUAL);
        pastService.modifyAppointment("2026-06-01T10:00", "2026-06-01T11:00", "user1");

        // ✅ التعديل نجح
        assertEquals(0, repo.findByDateTime(DT1).getCurrentParticipants());
        assertEquals(1, repo.findByDateTime(DT2).getCurrentParticipants());
    }
}