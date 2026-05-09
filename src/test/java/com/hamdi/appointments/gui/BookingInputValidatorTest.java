package com.hamdi.appointments.gui;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BookingInputValidatorTest {

    @Test
    void testValidateBookInput_Valid() {
        assertNull(BookingInputValidator.validateBookInput("2026-06-01T10:00", "30"));
    }

    @Test
    void testValidateBookInput_EmptyDateTime() {
        assertEquals("Please enter a DateTime!",
                BookingInputValidator.validateBookInput("", "30"));
    }

    @Test
    void testValidateBookInput_NullDateTime() {
        assertEquals("Please enter a DateTime!",
                BookingInputValidator.validateBookInput(null, "30"));
    }

    @Test
    void testValidateBookInput_InvalidDuration() {
        assertEquals("Invalid duration!",
                BookingInputValidator.validateBookInput("2026-06-01T10:00", "abc"));
    }

    @Test
    void testValidateCancelInput_Valid() {
        assertNull(BookingInputValidator.validateCancelInput("2026-06-01T10:00"));
    }

    @Test
    void testValidateCancelInput_Empty() {
        assertEquals("Please enter a DateTime!",
                BookingInputValidator.validateCancelInput(""));
    }

    @Test
    void testValidateCancelInput_Null() {
        assertEquals("Please enter a DateTime!",
                BookingInputValidator.validateCancelInput(null));
    }

    @Test
    void testValidateModifyInput_Valid() {
        assertNull(BookingInputValidator.validateModifyInput(
                "2026-06-01T10:00", "2026-06-01T11:00"));
    }

    @Test
    void testValidateModifyInput_EmptyOld() {
        assertEquals("Please enter the current DateTime!",
                BookingInputValidator.validateModifyInput("", "2026-06-01T11:00"));
    }

    @Test
    void testValidateModifyInput_EmptyNew() {
        assertEquals("Please enter the new DateTime!",
                BookingInputValidator.validateModifyInput("2026-06-01T10:00", ""));
    }

    @Test
    void testValidateModifyInput_NullOld() {
        assertEquals("Please enter the current DateTime!",
                BookingInputValidator.validateModifyInput(null, "2026-06-01T11:00"));
    }

    @Test
    void testValidateModifyInput_NullNew() {
        assertEquals("Please enter the new DateTime!",
                BookingInputValidator.validateModifyInput("2026-06-01T10:00", null));
    }

    @Test
    void testValidateAdminInput_Valid() {
        assertNull(BookingInputValidator.validateAdminInput("user1", "2026-06-01T10:00"));
    }

    @Test
    void testValidateAdminInput_EmptyUser() {
        assertEquals("Please enter a Target User!",
                BookingInputValidator.validateAdminInput("", "2026-06-01T10:00"));
    }

    @Test
    void testValidateAdminInput_NullUser() {
        assertEquals("Please enter a Target User!",
                BookingInputValidator.validateAdminInput(null, "2026-06-01T10:00"));
    }

    @Test
    void testValidateAdminInput_EmptyDateTime() {
        assertEquals("Please enter a DateTime!",
                BookingInputValidator.validateAdminInput("user1", ""));
    }

    @Test
    void testValidateAdminInput_NullDateTime() {
        assertEquals("Please enter a DateTime!",
                BookingInputValidator.validateAdminInput("user1", null));
    }
}