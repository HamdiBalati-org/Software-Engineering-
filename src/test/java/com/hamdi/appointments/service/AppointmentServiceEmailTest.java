package com.hamdi.appointments.service;

import com.hamdi.appointments.domain.Appointment;
import com.hamdi.appointments.domain.AppointmentType;
import com.hamdi.appointments.notification.EmailSender;
import com.hamdi.appointments.notification.NotificationService;
import com.hamdi.appointments.repository.AppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for email behavior in AppointmentService.
 *
 * Uses Mockito mock instead of real Gmail sender.
 */
public class AppointmentServiceEmailTest {

    private AppointmentRepository repo;
    private AppointmentService service;
    private EmailSender mockEmailSender;

    @BeforeEach
    void setUp() {
        repo = new AppointmentRepository();

        repo.addAppointment(new Appointment(
                LocalDateTime.of(2026, 6, 1, 10, 0), 30, 3));

        repo.addAppointment(new Appointment(
                LocalDateTime.of(2026, 6, 1, 11, 0), 60, 1));

        mockEmailSender = mock(EmailSender.class);

        NotificationService notificationService =
                new NotificationService(mockEmailSender);

        Clock clock = Clock.fixed(
                LocalDateTime.of(2025, 1, 1, 0, 0).toInstant(ZoneOffset.UTC),
                ZoneId.of("UTC")
        );

        service = new AppointmentService(repo, true, clock, notificationService);
    }



    @Test
    void testBooking_SendsEmailOnce() {
        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.VIRTUAL);

        verify(mockEmailSender, timeout(1000).times(1))
                .sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testBooking_DuplicateBooking_EmailSentOnceOnly() {
        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.VIRTUAL);
        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.VIRTUAL);

        verify(mockEmailSender, timeout(1000).times(1))
                .sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testBooking_FailedRule_NoEmailSent() {
        service.bookAppointment("2026-06-01T11:00", 60, "user1", AppointmentType.URGENT);

        verify(mockEmailSender, after(300).never())
                .sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testBooking_NotFound_NoEmailSent() {
        service.bookAppointment("2026-06-01T23:00", 30, "user1", AppointmentType.VIRTUAL);

        verify(mockEmailSender, after(300).never())
                .sendEmail(anyString(), anyString(), anyString());
    }



    @Test
    void testCancel_SendsEmailOnce() {
        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.VIRTUAL);

        verify(mockEmailSender, timeout(1000).times(1))
                .sendEmail(anyString(), anyString(), anyString());

        clearInvocations(mockEmailSender);

        service.cancelAppointment("2026-06-01T10:00", "user1");

        verify(mockEmailSender, timeout(1000).times(1))
                .sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testCancel_NotBooked_NoEmail() {
        service.cancelAppointment("2026-06-01T10:00", "user1");

        verify(mockEmailSender, after(300).never())
                .sendEmail(anyString(), anyString(), anyString());
    }

 

    @Test
    void testModify_SendsEmailOnce() {
        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.VIRTUAL);

        verify(mockEmailSender, timeout(1000).times(1))
                .sendEmail(anyString(), anyString(), anyString());

        clearInvocations(mockEmailSender);

        service.modifyAppointment("2026-06-01T10:00", "2026-06-01T11:00", "user1");

        verify(mockEmailSender, timeout(1000).times(1))
                .sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testModify_Failed_NoEmail() {
        service.modifyAppointment("2026-06-01T10:00", "2026-06-01T11:00", "user1");

        verify(mockEmailSender, after(300).never())
                .sendEmail(anyString(), anyString(), anyString());
    }

   

    @Test
    void testAdminCancel_SendsEmailOnce() {
        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.VIRTUAL);

        verify(mockEmailSender, timeout(1000).times(1))
                .sendEmail(anyString(), anyString(), anyString());

        clearInvocations(mockEmailSender);

        service.adminCancelAppointment("2026-06-01T10:00", "user1", "admin");

        verify(mockEmailSender, timeout(1000).times(1))
                .sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testAdminModify_SendsEmailOnce() {
        service.bookAppointment("2026-06-01T10:00", 30, "user1", AppointmentType.VIRTUAL);

        verify(mockEmailSender, timeout(1000).times(1))
                .sendEmail(anyString(), anyString(), anyString());

        clearInvocations(mockEmailSender);

        service.adminModifyAppointment("2026-06-01T10:00", "2026-06-01T11:00", "user1", "admin");

        verify(mockEmailSender, timeout(1000).times(1))
                .sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testAdminModify_Failed_NoEmail() {
        service.adminModifyAppointment("2026-06-01T10:00", "2026-06-01T11:00", "user1", "admin");

        verify(mockEmailSender, after(300).never())
                .sendEmail(anyString(), anyString(), anyString());
    }
}