package com.hamdi.appointments.gui;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GUIValidatorTest {

    @Test
    void testIsValidDateTime_Valid() {
        assertTrue(GUIValidator.isValidDateTime("2026-06-01T10:00"));
    }

    @Test
    void testIsValidDateTime_Empty() {
        assertFalse(GUIValidator.isValidDateTime(""));
    }

    @Test
    void testIsValidDateTime_Null() {
        assertFalse(GUIValidator.isValidDateTime(null));
    }

    @Test
    void testIsValidDateTime_Whitespace() {
        assertFalse(GUIValidator.isValidDateTime("   "));
    }

    @Test
    void testIsValidDuration_Valid() {
        assertTrue(GUIValidator.isValidDuration("30"));
    }

    @Test
    void testIsValidDuration_Invalid() {
        assertFalse(GUIValidator.isValidDuration("abc"));
    }

    @Test
    void testIsValidDuration_Empty() {
        assertFalse(GUIValidator.isValidDuration(""));
    }

    @Test
    void testIsValidDuration_Null() {
        assertFalse(GUIValidator.isValidDuration(null));
    }

    @Test
    void testIsValidDuration_Whitespace() {
        assertFalse(GUIValidator.isValidDuration("   "));
    }

    @Test
    void testIsValidUser_Valid() {
        assertTrue(GUIValidator.isValidUser("user1"));
    }

    @Test
    void testIsValidUser_Empty() {
        assertFalse(GUIValidator.isValidUser(""));
    }

    @Test
    void testIsValidUser_Null() {
        assertFalse(GUIValidator.isValidUser(null));
    }

    @Test
    void testIsValidUser_Whitespace() {
        assertFalse(GUIValidator.isValidUser("   "));
    }
}