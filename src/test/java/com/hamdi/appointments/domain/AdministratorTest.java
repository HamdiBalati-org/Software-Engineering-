package com.hamdi.appointments.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AdministratorTest {

    @Test
    void testAdminCreation() {
        Administrator admin = new Administrator("admin", "1234");

        assertEquals("admin", admin.getUsername());
        assertEquals("1234", admin.getPassword());
    }

    @Test
    void testAdminUsernameNotNull() {
        Administrator admin = new Administrator("admin", "1234");

        assertNotNull(admin.getUsername());
    }

    @Test
    void testAdminPasswordNotNull() {
        Administrator admin = new Administrator("admin", "1234");

        assertNotNull(admin.getPassword());
    }
}