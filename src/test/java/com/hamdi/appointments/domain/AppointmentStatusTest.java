package com.hamdi.appointments.domain;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class AppointmentStatusTest {

    private static final LocalDateTime DT = LocalDateTime.of(2026, 6, 1, 10, 0);

    @Test
    void testGetStatusForUser_Confirmed() {
        Appointment a = new Appointment(DT, 30, 3);
        a.incrementParticipants("user1");
        assertEquals("Confirmed", a.getStatusForUser("user1"));
    }

    @Test
    void testGetStatusForUser_Pending_WhenNotBooked() {
        Appointment a = new Appointment(DT, 30, 3);
        assertEquals("Pending", a.getStatusForUser("user1"));
    }

    @Test
    void testGetStatus_Pending_WhenNoParticipants() {
        Appointment a = new Appointment(DT, 30, 3);
        assertEquals("Pending", a.getStatus());
    }

    @Test
    void testGetStatus_Confirmed_AfterBooking() {
        Appointment a = new Appointment(DT, 30, 3);
        a.incrementParticipants("user1");
        assertEquals("Confirmed", a.getStatus());
    }

    @Test
    void testGetStatus_BackToPending_AfterAllCancel() {
        Appointment a = new Appointment(DT, 30, 3);
        a.incrementParticipants("user1");
        a.cancelBooking("user1");
        assertEquals("Pending", a.getStatus());
    }
}