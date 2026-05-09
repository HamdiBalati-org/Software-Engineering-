package com.hamdi.appointments.strategy;

import com.hamdi.appointments.domain.Appointment;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class StrategyTest {

    private static final LocalDateTime DT = LocalDateTime.of(2026, 6, 1, 10, 0);

    // ================= URGENT =================

    @Test
    void testUrgentRule_Valid_Exactly30Minutes() {
        Appointment a = new Appointment(DT, 30, 3);
        assertTrue(new UrgentRule().isValid(a));
    }

    @Test
    void testUrgentRule_Valid_Above30Minutes() {
        Appointment a = new Appointment(DT, 60, 3);
        assertTrue(new UrgentRule().isValid(a));
    }

    @Test
    void testUrgentRule_Invalid_Below30Minutes() {
        Appointment a = new Appointment(DT, 20, 3);
        assertFalse(new UrgentRule().isValid(a));
    }

    @Test
    void urgentBoundary_JustAbove30() {
        Appointment a = new Appointment(DT, 31, 1);
        assertTrue(new UrgentRule().isValid(a));
    }
    @Test
    void testGroupRule_Invalid_WhenCapacityZero() {
        Appointment a = new Appointment(DT, 30, 0);
        assertFalse(new GroupRule().isValid(a));
    }
    @Test
    void testVirtualRule_WithNormalValues() {
        Appointment a = new Appointment(DT, 30, 1);
        assertTrue(new VirtualRule().isValid(a));
    }
    @Test
    void urgentEdge_ZeroDuration() {
        Appointment a = new Appointment(DT, 0, 1);
        assertFalse(new UrgentRule().isValid(a));
    }

    @Test
    void testUrgentRule_Description_NotBlank() {
        assertFalse(new UrgentRule().getRuleDescription().isBlank());
    }

    // ================= FOLLOW UP =================

    @Test
    void testFollowUpRule_Valid_Exactly60Minutes() {
        Appointment a = new Appointment(DT, 60, 3);
        assertTrue(new FollowUpRule().isValid(a));
    }

    @Test
    void testFollowUpRule_Valid_Above60Minutes() {
        Appointment a = new Appointment(DT, 90, 3);
        assertTrue(new FollowUpRule().isValid(a));
    }

    @Test
    void testFollowUpRule_Invalid_Below60Minutes() {
        Appointment a = new Appointment(DT, 45, 3);
        assertFalse(new FollowUpRule().isValid(a));
    }

    @Test
    void followUpBoundary_JustAbove60() {
        Appointment a = new Appointment(DT, 61, 1);
        assertTrue(new FollowUpRule().isValid(a));
    }

    @Test
    void testFollowUpRule_Description_NotBlank() {
        assertFalse(new FollowUpRule().getRuleDescription().isBlank());
    }

    // ================= ASSESSMENT =================

    @Test
    void testAssessmentRule_Valid_Exactly120Minutes() {
        Appointment a = new Appointment(DT, 120, 3);
        assertTrue(new AssessmentRule().isValid(a));
    }

    @Test
    void testAssessmentRule_Valid_Above120Minutes() {
        Appointment a = new Appointment(DT, 130, 3);
        assertTrue(new AssessmentRule().isValid(a));
    }

    @Test
    void testAssessmentRule_Invalid_Below120Minutes() {
        Appointment a = new Appointment(DT, 100, 3);
        assertFalse(new AssessmentRule().isValid(a));
    }

    @Test
    void assessmentBoundary_JustAbove120() {
        Appointment a = new Appointment(DT, 121, 1);
        assertTrue(new AssessmentRule().isValid(a));
    }

    @Test
    void testAssessmentRule_Description_NotBlank() {
        assertFalse(new AssessmentRule().getRuleDescription().isBlank());
    }

    // ================= VIRTUAL =================

    @Test
    void testVirtualRule_AlwaysValid() {
        Appointment a = new Appointment(DT, 999, 999);
        assertTrue(new VirtualRule().isValid(a));
    }

    @Test
    void testVirtualRule_Description_NotBlank() {
        assertFalse(new VirtualRule().getRuleDescription().isBlank());
    }

    // ================= IN PERSON =================

    @Test
    void testInPersonRule_Valid_WhenMaxParticipantsIs10() {
        Appointment a = new Appointment(DT, 30, 10);
        assertTrue(new InPersonRule().isValid(a));
    }

    @Test
    void testInPersonRule_Invalid_WhenMaxParticipantsExceeds10() {
        Appointment a = new Appointment(DT, 30, 11);
        assertFalse(new InPersonRule().isValid(a));
    }

    @Test
    void inPersonBoundary_Exactly10() {
        Appointment a = new Appointment(DT, 30, 10);
        assertTrue(new InPersonRule().isValid(a));
    }

    @Test
    void testInPersonRule_Description_NotBlank() {
        assertFalse(new InPersonRule().getRuleDescription().isBlank());
    }

    // ================= INDIVIDUAL =================

    @Test
    void testIndividualRule_Valid_WhenCapacityIsOne() {
        Appointment a = new Appointment(DT, 30, 1);
        assertTrue(new IndividualRule().isValid(a));
    }

    @Test
    void testIndividualRule_Invalid_WhenCapacityGreaterThanOne() {
        Appointment a = new Appointment(DT, 30, 3);
        assertFalse(new IndividualRule().isValid(a));
    }

    @Test
    void testIndividualRule_Invalid_WhenCapacityIsZero() {
        Appointment a = new Appointment(DT, 30, 0);
        assertFalse(new IndividualRule().isValid(a));
    }

    @Test
    void testIndividualRule_Description_NotBlank() {
        assertFalse(new IndividualRule().getRuleDescription().isBlank());
    }

    // ================= GROUP =================

    @Test
    void testGroupRule_Valid_WhenCapacityIsTwo() {
        Appointment a = new Appointment(DT, 30, 2);
        assertTrue(new GroupRule().isValid(a));
    }

    @Test
    void testGroupRule_Valid_WhenCapacityGreaterThanTwo() {
        Appointment a = new Appointment(DT, 30, 5);
        assertTrue(new GroupRule().isValid(a));
    }

    @Test
    void testGroupRule_Invalid_WhenCapacityIsOne() {
        Appointment a = new Appointment(DT, 30, 1);
        assertFalse(new GroupRule().isValid(a));
    }

    @Test
    void testGroupRule_Description_NotBlank() {
        assertFalse(new GroupRule().getRuleDescription().isBlank());
    }
}