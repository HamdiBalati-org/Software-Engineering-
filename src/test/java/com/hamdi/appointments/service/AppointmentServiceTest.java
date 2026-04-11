package com.hamdi.appointments.service;

import com.hamdi.appointments.domain.Appointment;
import com.hamdi.appointments.domain.AppointmentType;
import com.hamdi.appointments.notification.FakeEmailSender;
import com.hamdi.appointments.notification.NotificationService;
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
 * @version 3.0
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

        NotificationService notificationService =
                new NotificationService(new FakeEmailSender());

        Clock clock = Clock.fixed(
                LocalDateTime.of(2025, 1, 1, 0, 0).toInstant(ZoneOffset.UTC),
                ZoneId.of("UTC")
        );

        service = new AppointmentService(repo, true, clock, notificationService);
    }

    @Test
    void testBookAppointment_Success() {
        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.VIRTUAL);

        Appointment a = repo.findByDateTime(DT1);
        assertEquals(1, a.getCurrentParticipants());
        assertEquals("Confirmed", a.getStatusForUser("user1"));
        assertEquals(AppointmentType.VIRTUAL, a.getTypeForUser("user1"));
        assertTrue(a.isBookedByUser("user1"));
    }

    @Test
    void testBookAppointment_NotFound() {
        service.bookAppointment("2026-06-01T23:00", 30, "user1", AppointmentType.VIRTUAL);

        assertEquals(0, repo.findByDateTime(DT1).getCurrentParticipants());
        assertFalse(repo.findByDateTime(DT1).isBookedByUser("user1"));
    }

    @Test
    void testBookAppointment_WrongDuration() {
        service.bookAppointment("2026-06-01T10:00", 99, "user1", AppointmentType.VIRTUAL);

        Appointment a = repo.findByDateTime(DT1);
        assertEquals(0, a.getCurrentParticipants());
        assertFalse(a.isBookedByUser("user1"));
        assertNull(a.getTypeForUser("user1"));
    }

    @Test
    void testBookAppointment_AlreadyBooked() {
        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.VIRTUAL);
        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.VIRTUAL);

        Appointment a = repo.findByDateTime(DT1);
        assertEquals(1, a.getCurrentParticipants());
        assertTrue(a.isBookedByUser("user1"));
    }

    @Test
    void testBookAppointment_FullyBooked() {
        service.bookAppointment("2026-06-01T11:00", 60, "user1", AppointmentType.VIRTUAL);
        service.bookAppointment("2026-06-01T11:00", 60, "user2", AppointmentType.VIRTUAL);

        Appointment a = repo.findByDateTime(DT2);
        assertEquals(1, a.getCurrentParticipants());
        assertTrue(a.isBookedByUser("user1"));
        assertFalse(a.isBookedByUser("user2"));
    }

    @Test
    void testBookAppointment_MultipleUsers_DifferentTypes() {
        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.URGENT);
        service.bookAppointment("2026-06-01T10:00", 30, "user2", AppointmentType.VIRTUAL);

        Appointment a = repo.findByDateTime(DT1);
        assertEquals(2, a.getCurrentParticipants());
        assertEquals(AppointmentType.URGENT, a.getTypeForUser("user1"));
        assertEquals(AppointmentType.VIRTUAL, a.getTypeForUser("user2"));
    }

    @Test
    void testBookAppointment_Individual_AllowedWhenAppointmentCapacityIsGreaterThanOne() {
        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.INDIVIDUAL);

        Appointment a = repo.findByDateTime(DT1);
        assertEquals(1, a.getCurrentParticipants());
        assertEquals(AppointmentType.INDIVIDUAL, a.getTypeForUser("user1"));
    }

    @Test
    void testBookAppointment_RuleViolated_UrgentDuration() {
        service.bookAppointment("2026-06-01T11:00", 60, "user1", AppointmentType.URGENT);

        Appointment a = repo.findByDateTime(DT2);
        assertEquals(0, a.getCurrentParticipants());
        assertFalse(a.isBookedByUser("user1"));
        assertNull(a.getTypeForUser("user1"));
    }

    @Test
    void testBookAppointment_MultipleUsers_UntilFull_ThenRejectNext() {
        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.VIRTUAL);
        service.bookAppointment("2026-06-01T10:00", 30, "user2", AppointmentType.VIRTUAL);
        service.bookAppointment("2026-06-01T10:00", 30, "user3", AppointmentType.VIRTUAL);
        service.bookAppointment("2026-06-01T10:00", 30, "user4", AppointmentType.VIRTUAL);

        Appointment a = repo.findByDateTime(DT1);
        assertEquals(3, a.getCurrentParticipants());
        assertFalse(a.isBookedByUser("user4"));
    }

    @Test
    void testBookAppointment_FollowUp_Valid() {
        service.bookAppointment("2026-06-01T11:00", 60, "user1", AppointmentType.FOLLOW_UP);
        assertEquals(1, repo.findByDateTime(DT2).getCurrentParticipants());
    }

    @Test
    void testBookAppointment_Assessment_Valid() {
        LocalDateTime dt3 = LocalDateTime.of(2026, 6, 1, 12, 0);
        repo.addAppointment(new Appointment(dt3, 120, 3));

        service.bookAppointment("2026-06-01T12:00", 120, "user1", AppointmentType.ASSESSMENT);

        assertEquals(1, repo.findByDateTime(dt3).getCurrentParticipants());
    }

    @Test
    void testBookAppointment_InPerson_Valid() {
        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.IN_PERSON);
        assertEquals(1, repo.findByDateTime(DT1).getCurrentParticipants());
    }

    @Test
    void testBookAppointment_Group_Valid() {
        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.GROUP);
        assertEquals(1, repo.findByDateTime(DT1).getCurrentParticipants());
    }

    @Test
    void testBookAppointment_Individual_Valid() {
        service.bookAppointment("2026-06-01T11:00", 60, "user1", AppointmentType.INDIVIDUAL);
        assertEquals(1, repo.findByDateTime(DT2).getCurrentParticipants());
    }

    @Test
    void testCancelAppointment_Success() {
        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.VIRTUAL);

        service.cancelAppointment("2026-06-01T10:00", "user1");

        Appointment a = repo.findByDateTime(DT1);
        assertEquals(0, a.getCurrentParticipants());
        assertEquals("Pending", a.getStatus());
        assertFalse(a.isBookedByUser("user1"));
        assertNull(a.getTypeForUser("user1"));
    }

    @Test
    void testCancelAppointment_NotBooked() {
        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.VIRTUAL);

        service.cancelAppointment("2026-06-01T10:00", "user2");

        Appointment a = repo.findByDateTime(DT1);
        assertEquals(1, a.getCurrentParticipants());
        assertTrue(a.isBookedByUser("user1"));
        assertFalse(a.isBookedByUser("user2"));
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
        assertFalse(repo.findByDateTime(DT1).isBookedByUser("user1"));
        assertTrue(repo.findByDateTime(DT2).isBookedByUser("user1"));
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
    void testModifyAppointment_OldNotFound() {
        service.modifyAppointment("2026-06-01T23:00", "2026-06-01T11:00", "user1");
        assertEquals(0, repo.findByDateTime(DT2).getCurrentParticipants());
    }

    @Test
    void testModifyAppointment_NewNotFound() {
        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.VIRTUAL);

        service.modifyAppointment("2026-06-01T10:00", "2026-06-01T23:00", "user1");

        assertEquals(1, repo.findByDateTime(DT1).getCurrentParticipants());
        assertTrue(repo.findByDateTime(DT1).isBookedByUser("user1"));
    }

    @Test
    void testModifyAppointment_NewFullyBooked() {
        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.VIRTUAL);
        service.bookAppointment("2026-06-01T11:00", 60, "user2", AppointmentType.VIRTUAL);

        service.modifyAppointment("2026-06-01T10:00", "2026-06-01T11:00", "user1");

        assertEquals(1, repo.findByDateTime(DT1).getCurrentParticipants());
        assertTrue(repo.findByDateTime(DT1).isBookedByUser("user1"));
    }

    @Test
    void testModifyAppointment_NewAlreadyBooked() {
        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.VIRTUAL);
        service.bookAppointment("2026-06-01T11:00", 60, "user1", AppointmentType.VIRTUAL);

        service.modifyAppointment("2026-06-01T10:00", "2026-06-01T11:00", "user1");

        assertEquals(1, repo.findByDateTime(DT1).getCurrentParticipants());
        assertEquals(1, repo.findByDateTime(DT2).getCurrentParticipants());
    }

    @Test
    void testAdminCancelAppointment_Success() {
        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.VIRTUAL);

        service.adminCancelAppointment("2026-06-01T10:00", "user1", "admin");

        Appointment a = repo.findByDateTime(DT1);
        assertEquals(0, a.getCurrentParticipants());
        assertFalse(a.isBookedByUser("user1"));
        assertNull(a.getTypeForUser("user1"));
    }

    @Test
    void testAdminCancelAppointment_UserNotBooked() {
        service.adminCancelAppointment("2026-06-01T10:00", "user1", "admin");
        assertEquals(0, repo.findByDateTime(DT1).getCurrentParticipants());
    }

    @Test
    void testAdminCancelAppointment_NotFound() {
        service.adminCancelAppointment("2026-06-01T23:00", "user1", "admin");
        assertEquals(0, repo.findByDateTime(DT1).getCurrentParticipants());
    }

    @Test
    void testAdminModifyAppointment_Success() {
        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.VIRTUAL);

        service.adminModifyAppointment("2026-06-01T10:00", "2026-06-01T11:00", "user1", "admin");

        assertEquals(0, repo.findByDateTime(DT1).getCurrentParticipants());
        assertEquals(1, repo.findByDateTime(DT2).getCurrentParticipants());
        assertTrue(repo.findByDateTime(DT2).isBookedByUser("user1"));
    }

    @Test
    void testAdminModifyAppointment_TypeTransferred() {
        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.URGENT);

        LocalDateTime dt3 = LocalDateTime.of(2026, 6, 1, 12, 0);
        repo.addAppointment(new Appointment(dt3, 30, 2));

        service.adminModifyAppointment("2026-06-01T10:00", "2026-06-01T12:00", "user1", "admin");

        assertEquals(AppointmentType.URGENT, repo.findByDateTime(dt3).getTypeForUser("user1"));
    }

    @Test
    void testAdminModifyAppointment_UserNotBooked() {
        service.adminModifyAppointment("2026-06-01T10:00", "2026-06-01T11:00", "user1", "admin");
        assertEquals(0, repo.findByDateTime(DT2).getCurrentParticipants());
    }

    @Test
    void testAdminModifyAppointment_OldNotFound() {
        service.adminModifyAppointment("2026-06-01T23:00", "2026-06-01T11:00", "user1", "admin");
        assertEquals(0, repo.findByDateTime(DT2).getCurrentParticipants());
    }

    @Test
    void testAdminModifyAppointment_NewNotFound() {
        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.VIRTUAL);

        service.adminModifyAppointment("2026-06-01T10:00", "2026-06-01T23:00", "user1", "admin");

        assertEquals(1, repo.findByDateTime(DT1).getCurrentParticipants());
        assertTrue(repo.findByDateTime(DT1).isBookedByUser("user1"));
    }

    @Test
    void testAdminModifyAppointment_NewFullyBooked() {
        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.VIRTUAL);
        service.bookAppointment("2026-06-01T11:00", 60, "user2", AppointmentType.VIRTUAL);

        service.adminModifyAppointment("2026-06-01T10:00", "2026-06-01T11:00", "user1", "admin");

        assertEquals(1, repo.findByDateTime(DT1).getCurrentParticipants());
        assertTrue(repo.findByDateTime(DT1).isBookedByUser("user1"));
    }

    @Test
    void testAdminModifyAppointment_NewAlreadyBooked() {
        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.VIRTUAL);
        service.bookAppointment("2026-06-01T11:00", 60, "user1", AppointmentType.VIRTUAL);

        service.adminModifyAppointment("2026-06-01T10:00", "2026-06-01T11:00", "user1", "admin");

        assertEquals(1, repo.findByDateTime(DT1).getCurrentParticipants());
        assertEquals(1, repo.findByDateTime(DT2).getCurrentParticipants());
    }

    @Test
    void testGetAllAppointments() {
        assertEquals(2, service.getAllAppointments().size());
    }

    @Test
    void testGetAvailableAppointments_BeforeAndAfterFullBooking() {
        assertEquals(2, service.getAvailableAppointments().size());

        service.bookAppointment("2026-06-01T11:00", 60, "user1", AppointmentType.VIRTUAL);

        assertEquals(2, service.getAvailableAppointments().size());
    }

    @Test
    void testCancelAppointment_PastAppointment_Rejected() {
        NotificationService notificationService =
                new NotificationService(new FakeEmailSender());

        Clock futureClock = Clock.fixed(
                LocalDateTime.of(2027, 1, 1, 0, 0).toInstant(ZoneOffset.UTC),
                ZoneId.of("UTC")
        );

        AppointmentService futureService =
                new AppointmentService(repo, true, futureClock, notificationService);

        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.VIRTUAL);
        futureService.cancelAppointment("2026-06-01T10:00", "user1");

        assertEquals(1, repo.findByDateTime(DT1).getCurrentParticipants());
        assertTrue(repo.findByDateTime(DT1).isBookedByUser("user1"));
    }

    @Test
    void testModifyAppointment_PastAppointment_Rejected() {
        NotificationService notificationService =
                new NotificationService(new FakeEmailSender());

        Clock futureClock = Clock.fixed(
                LocalDateTime.of(2027, 1, 1, 0, 0).toInstant(ZoneOffset.UTC),
                ZoneId.of("UTC")
        );

        AppointmentService futureService =
                new AppointmentService(repo, true, futureClock, notificationService);

        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.VIRTUAL);
        futureService.modifyAppointment("2026-06-01T10:00", "2026-06-01T11:00", "user1");

        assertEquals(1, repo.findByDateTime(DT1).getCurrentParticipants());
        assertEquals(0, repo.findByDateTime(DT2).getCurrentParticipants());
    }

    @Test
    void testCancelAppointment_FutureAppointment_Accepted() {
        NotificationService notificationService =
                new NotificationService(new FakeEmailSender());

        Clock pastClock = Clock.fixed(
                LocalDateTime.of(2025, 1, 1, 0, 0).toInstant(ZoneOffset.UTC),
                ZoneId.of("UTC")
        );

        AppointmentService pastService =
                new AppointmentService(repo, true, pastClock, notificationService);

        pastService.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.VIRTUAL);
        pastService.cancelAppointment("2026-06-01T10:00", "user1");

        assertEquals(0, repo.findByDateTime(DT1).getCurrentParticipants());
        assertEquals("Pending", repo.findByDateTime(DT1).getStatus());
    }

    @Test
    void testModifyAppointment_FutureAppointment_Accepted() {
        NotificationService notificationService =
                new NotificationService(new FakeEmailSender());

        Clock pastClock = Clock.fixed(
                LocalDateTime.of(2025, 1, 1, 0, 0).toInstant(ZoneOffset.UTC),
                ZoneId.of("UTC")
        );

        AppointmentService pastService =
                new AppointmentService(repo, true, pastClock, notificationService);

        pastService.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.VIRTUAL);
        pastService.modifyAppointment("2026-06-01T10:00", "2026-06-01T11:00", "user1");

        assertEquals(0, repo.findByDateTime(DT1).getCurrentParticipants());
        assertEquals(1, repo.findByDateTime(DT2).getCurrentParticipants());
    }
    @Test
    void testConstructor_Default_Works() {
        AppointmentService s = new AppointmentService(repo);
        assertNotNull(s.getAllAppointments());
    }

    @Test
    void testConstructor_TestMode_Works() {
        AppointmentService s = new AppointmentService(repo, true);
        assertNotNull(s.getAllAppointments());
    }

    @Test
    void testConstructor_WithClock_Works() {
        Clock clock = Clock.fixed(
            LocalDateTime.of(2025, 1, 1, 0, 0).toInstant(ZoneOffset.UTC),
            ZoneId.of("UTC"));
        AppointmentService s = new AppointmentService(repo, true, clock);
        assertNotNull(s.getAllAppointments());
    }

    @Test
    void testBookAppointment_PastAppointment_Rejected() {
        Clock futureClock = Clock.fixed(
            LocalDateTime.of(2027, 1, 1, 0, 0).toInstant(ZoneOffset.UTC),
            ZoneId.of("UTC"));
        NotificationService ns = new NotificationService(new FakeEmailSender());
        AppointmentService futureService = new AppointmentService(repo, true, futureClock, ns);

        futureService.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.VIRTUAL);

        assertFalse(repo.findByDateTime(DT1).isBookedByUser("user1"));
    }

    @Test
    void testGetAvailableAppointments_ReturnsSameAsAll() {
        assertEquals(service.getAllAppointments().size(),
                     service.getAvailableAppointments().size());
    }
}