package com.hamdi.appointments.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Appointment domain class.
 *
 * @author Hamdi
 * @version 1.0
 */
public class AppointmentTest {

    private Appointment appointment;
    private static final LocalDateTime DT = LocalDateTime.of(2026, 6, 1, 10, 0);

    @BeforeEach
    void setUp() {
        appointment = new Appointment(DT, 30, 3);
    }

    // ==================== Basic Getters ====================

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

    // ==================== incrementParticipants ====================

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

    // ==================== cancelBooking ====================

    @Test
    void testCancelBooking_Success() {
        appointment.incrementParticipants("user1");
        boolean result = appointment.cancelBooking("user1");
        assertTrue(result);
        assertEquals(0, appointment.getCurrentParticipants());
        assertEquals("Pending", appointment.getStatus());
    }

    @Test
    void testCancelBooking_UserNotBooked() {
        boolean result = appointment.cancelBooking("user1");
        assertFalse(result);
    }

    // ==================== isBookedByUser ====================

    @Test
    void testIsBookedByUser_True() {
        appointment.incrementParticipants("user1");
        assertTrue(appointment.isBookedByUser("user1"));
    }

    @Test
    void testIsBookedByUser_False() {
        assertFalse(appointment.isBookedByUser("user1"));
    }

    // ==================== setTypeForUser / getTypeForUser ====================

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

    // ==================== getBookedUsers ====================

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
}
