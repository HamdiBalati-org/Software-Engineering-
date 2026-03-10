package com.hamdi.appointments.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AuthService.
 *
 * @author Hamdi
 * @version 1.0
 */
public class AuthServiceTest {

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService();
        authService.addAdministrator("admin", "1234");
        authService.addUser("user1", "1234");
    }

    @Test
    void testLogin_AdminSuccess() {
        assertTrue(authService.login("admin", "1234"));
    }

    @Test
    void testLogin_UserSuccess() {
        assertTrue(authService.login("user1", "1234"));
    }

    @Test
    void testLogin_WrongPassword() {
        assertFalse(authService.login("admin", "wrong"));
    }

    @Test
    void testLogin_WrongUsername() {
        assertFalse(authService.login("unknown", "1234"));
    }

    @Test
    void testIsAdmin_True() {
        assertTrue(authService.isAdmin("admin"));
    }

    @Test
    void testIsAdmin_False() {
        assertFalse(authService.isAdmin("user1"));
    }

    @Test
    void testIsUser_True() {
        assertTrue(authService.isUser("user1"));
    }

    @Test
    void testIsUser_False() {
        assertFalse(authService.isUser("admin"));
    }
}