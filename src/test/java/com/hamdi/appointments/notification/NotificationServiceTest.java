package com.hamdi.appointments.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

/**
 * Unit tests for NotificationService using Mockito.
 *
 * @author Hamdi
 * @version 1.0
 */
public class NotificationServiceTest {

    private Observer mockObserver;
    private NotificationManager manager;

    @BeforeEach
    void setUp() {
        mockObserver = Mockito.mock(Observer.class);
        manager = new NotificationManager();
        manager.addObserver(mockObserver);
    }

    @Test
    void testNotifyAllObservers_Called() {
        manager.notifyAllObservers("user1", "Test message");
        verify(mockObserver, times(1)).notifyUser("user1", "Test message");
    }

    @Test
    void testNotifyAllObservers_CalledTwice() {
        manager.notifyAllObservers("user1", "Message 1");
        manager.notifyAllObservers("user1", "Message 2");
        verify(mockObserver, times(2)).notifyUser(anyString(), anyString());
    }

    @Test
    void testNotifyAllObservers_CorrectUsername() {
        manager.notifyAllObservers("user1", "Hello");
        verify(mockObserver).notifyUser(eq("user1"), anyString());
    }
}
