package com.hamdi.appointments.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    void testUserCreation() {
        User user = new User("user1", "1234");

        assertEquals("user1", user.getUsername());
        assertEquals("1234", user.getPassword());
    }

    @Test
    void testUsernameNotNull() {
        User user = new User("user1", "1234");

        assertNotNull(user.getUsername());
    }

    @Test
    void testPasswordNotNull() {
        User user = new User("user1", "1234");

        assertNotNull(user.getPassword());
    }
}