package com.hamdi.appointments.strategy;

import com.hamdi.appointments.domain.Appointment;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for all BookingRuleStrategy implementations.
 *
 * @author Hamdi
 * @version 1.0
 */
public class StrategyTest {

    private static final LocalDateTime DT = LocalDateTime.of(2026, 6, 1, 10, 0);

    // ==================== UrgentRule ====================

    @Test
    void testUrgentRule_Valid() {
        Appointment a = new Appointment(DT, 30, 3);
        assertTrue(new UrgentRule().isValid(a));
    }

    @Test
    void testUrgentRule_Invalid() {
        Appointment a = new Appointment(DT, 60, 3);
        assertFalse(new UrgentRule().isValid(a));
    }

    @Test
    void testUrgentRule_Description() {
        assertNotNull(new UrgentRule().getRuleDescription());
    }

    // ==================== FollowUpRule ====================

    @Test
    void testFollowUpRule_Valid() {
        Appointment a = new Appointment(DT, 60, 3);
        assertTrue(new FollowUpRule().isValid(a));
    }

    @Test
    void testFollowUpRule_Invalid() {
        Appointment a = new Appointment(DT, 90, 3);
        assertFalse(new FollowUpRule().isValid(a));
    }

    @Test
    void testFollowUpRule_Description() {
        assertNotNull(new FollowUpRule().getRuleDescription());
    }

    // ==================== AssessmentRule ====================

    @Test
    void testAssessmentRule_Valid() {
        Appointment a = new Appointment(DT, 120, 3);
        assertTrue(new AssessmentRule().isValid(a));
    }

    @Test
    void testAssessmentRule_Invalid() {
        Appointment a = new Appointment(DT, 130, 3);
        assertFalse(new AssessmentRule().isValid(a));
    }

    @Test
    void testAssessmentRule_Description() {
        assertNotNull(new AssessmentRule().getRuleDescription());
    }

    // ==================== VirtualRule ====================

    @Test
    void testVirtualRule_AlwaysValid() {
        Appointment a = new Appointment(DT, 999, 999);
        assertTrue(new VirtualRule().isValid(a));
    }

    @Test
    void testVirtualRule_Description() {
        assertNotNull(new VirtualRule().getRuleDescription());
    }

    // ==================== InPersonRule ====================

    @Test
    void testInPersonRule_Valid() {
        Appointment a = new Appointment(DT, 30, 10);
        assertTrue(new InPersonRule().isValid(a));
    }

    @Test
    void testInPersonRule_Invalid() {
        Appointment a = new Appointment(DT, 30, 11);
        assertFalse(new InPersonRule().isValid(a));
    }

    @Test
    void testInPersonRule_Description() {
        assertNotNull(new InPersonRule().getRuleDescription());
    }

    // ==================== IndividualRule ====================

    @Test
    void testIndividualRule_Valid() {
        Appointment a = new Appointment(DT, 30, 1);
        assertTrue(new IndividualRule().isValid(a));
    }

    @Test
    void testIndividualRule_Invalid() {
        Appointment a = new Appointment(DT, 30, 3);
        assertFalse(new IndividualRule().isValid(a));
    }

    @Test
    void testIndividualRule_Description() {
        assertNotNull(new IndividualRule().getRuleDescription());
    }

    // ==================== GroupRule ====================

    @Test
    void testGroupRule_Valid() {
        Appointment a = new Appointment(DT, 30, 5);
        assertTrue(new GroupRule().isValid(a));
    }

    @Test
    void testGroupRule_Invalid() {
        Appointment a = new Appointment(DT, 30, 1);
        assertFalse(new GroupRule().isValid(a));
    }

    @Test
    void testGroupRule_Description() {
        assertNotNull(new GroupRule().getRuleDescription());
    }
}