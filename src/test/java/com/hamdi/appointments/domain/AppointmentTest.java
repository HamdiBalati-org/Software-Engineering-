package com.hamdi.appointments.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Appointment domain class.
 *
 * @author Hamdi
 * @version 2.0
 */
public class AppointmentTest {

    private Appointment appointment;
    private static final LocalDateTime DT = LocalDateTime.of(2026, 6, 1, 10, 0);

    @BeforeEach
    void setUp() {
        appointment = new Appointment(DT, 30, 3);
    }


    @Test
    void testGetDateTime() {
        assertEquals(DT, appointment.getDateTime());
    }

    @Test
    void testGetDurationMinutes() {
        assertEquals(30, appointment.getDurationMinutes());
    }

    @Test
    void testGetMaxParticipants() {
        assertEquals(3, appointment.getMaxParticipants());
    }

    @Test
    void testInitialStatus() {
        assertEquals("Pending", appointment.getStatus());
    }

    @Test
    void testInitialParticipants() {
        assertEquals(0, appointment.getCurrentParticipants());
    }


    @Test
    void testIncrementParticipants() {
        appointment.incrementParticipants("user1");
        assertEquals(1, appointment.getCurrentParticipants());
        assertEquals("Confirmed", appointment.getStatus());
    }

    @Test
    void testIncrementParticipants_MultipleUsers() {
        appointment.incrementParticipants("user1");
        appointment.incrementParticipants("user2");

        assertEquals(2, appointment.getCurrentParticipants());
        assertTrue(appointment.isBookedByUser("user1"));
        assertTrue(appointment.isBookedByUser("user2"));
    }

    @Test
    void testIncrementParticipants_SameUserTwice_DoesNotDuplicate() {
        appointment.incrementParticipants("user1");
        appointment.incrementParticipants("user1");

        assertEquals(1, appointment.getCurrentParticipants());
        assertTrue(appointment.isBookedByUser("user1"));
    }

    @Test
    void testIncrementParticipants_DoesNotExceedMaxParticipants() {
        Appointment limited = new Appointment(DT, 30, 1);

        limited.incrementParticipants("user1");
        limited.incrementParticipants("user2");

        assertEquals(1, limited.getCurrentParticipants());
        assertTrue(limited.isBookedByUser("user1"));
        assertFalse(limited.isBookedByUser("user2"));
    }


    @Test
    void testCancelBooking_Success() {
        appointment.incrementParticipants("user1");

        boolean result = appointment.cancelBooking("user1");

        assertTrue(result);
        assertEquals(0, appointment.getCurrentParticipants());
        assertEquals("Pending", appointment.getStatus());
        assertFalse(appointment.isBookedByUser("user1"));
    }

    @Test
    void testCancelBooking_UserNotBooked() {
        boolean result = appointment.cancelBooking("user1");
        assertFalse(result);
    }

    @Test
    void testCancelBooking_OneOfMultipleUsers() {
        appointment.incrementParticipants("user1");
        appointment.incrementParticipants("user2");

        boolean result = appointment.cancelBooking("user1");

        assertTrue(result);
        assertEquals(1, appointment.getCurrentParticipants());
        assertFalse(appointment.isBookedByUser("user1"));
        assertTrue(appointment.isBookedByUser("user2"));
        assertEquals("Confirmed", appointment.getStatus());
    }


    @Test
    void testIsBookedByUser_True() {
        appointment.incrementParticipants("user1");
        assertTrue(appointment.isBookedByUser("user1"));
    }

    @Test
    void testIsBookedByUser_False() {
        assertFalse(appointment.isBookedByUser("user1"));
    }



    @Test
    void testSetAndGetTypeForUser() {
        appointment.setTypeForUser("user1", AppointmentType.VIRTUAL);
        assertEquals(AppointmentType.VIRTUAL, appointment.getTypeForUser("user1"));
    }

    @Test
    void testGetTypeForUser_NotSet() {
        assertNull(appointment.getTypeForUser("user1"));
    }

    @Test
    void testCancelBooking_RemovesType() {
        appointment.incrementParticipants("user1");
        appointment.setTypeForUser("user1", AppointmentType.VIRTUAL);

        appointment.cancelBooking("user1");

        assertNull(appointment.getTypeForUser("user1"));
    }



    @Test
    void testGetBookedUsers_Empty() {
        assertTrue(appointment.getBookedUsers().isEmpty());
    }

    @Test
    void testGetBookedUsers_AfterBooking() {
        appointment.incrementParticipants("user1");

        assertEquals(1, appointment.getBookedUsers().size());
        assertTrue(appointment.getBookedUsers().contains("user1"));
    }

    @Test
    void testGetBookedUsers_AfterCancellation_UserRemoved() {
        appointment.incrementParticipants("user1");
        appointment.cancelBooking("user1");

        assertFalse(appointment.getBookedUsers().contains("user1"));
        assertTrue(appointment.getBookedUsers().isEmpty());
    }
    @Test
    void testGetStatusForUser_FULL_WhenNotBookedAndFull() {
        Appointment a = new Appointment(DT, 30, 1);
        a.incrementParticipants("user1");
        assertEquals("FULL", a.getStatusForUser("user2"));
    }

    @Test
    void testIncrementParticipants_StatusFull_WhenMaxReached() {
        Appointment a = new Appointment(DT, 30, 1);
        a.incrementParticipants("user1");
        assertEquals("FULL", a.getStatus());
    }

    @Test
    void testCancelBooking_StatusFull_WhenStillFull() {
        Appointment a = new Appointment(DT, 30, 2);
        a.incrementParticipants("user1");
        a.incrementParticipants("user2");
        assertEquals("FULL", a.getStatus());

        // نضيف user3 يدوياً عشان نوصل للحالة اللي currentParticipants >= maxParticipants بعد إلغاء
        // نستخدم setStatus مباشرة
        a.setStatus("FULL");
        a.cancelBooking("user1");
        assertEquals("Confirmed", a.getStatus());
    }

    @Test
    void testConstructor_WithType_Works() {
        Appointment a = new Appointment(DT, 30, 3, AppointmentType.VIRTUAL);
        assertEquals(DT, a.getDateTime());
        assertEquals(30, a.getDurationMinutes());
        assertEquals(3, a.getMaxParticipants());
        assertEquals("Pending", a.getStatus());
    }

    @Test
    void testGetUserTypes_NotEmpty_AfterBooking() {
        appointment.setTypeForUser("user1", AppointmentType.VIRTUAL);
        assertFalse(appointment.getUserTypes().isEmpty());
        assertEquals(AppointmentType.VIRTUAL, appointment.getUserTypes().get("user1"));
    }

    @Test
    void testSetStatus_DirectlyChangesStatus() {
        appointment.setStatus("FULL");
        assertEquals("FULL", appointment.getStatus());
    }
}