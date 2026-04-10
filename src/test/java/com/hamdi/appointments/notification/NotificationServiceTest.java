package com.hamdi.appointments.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

/**
 * Unit tests for NotificationManager using Mockito.
 *
 * @author Hamdi
 * @version 2.0
 */
public class NotificationServiceTest {

    private Observer mockObserver;
    private NotificationManager manager;

    @BeforeEach
    void setUp() {
        mockObserver = mock(Observer.class);
        manager = new NotificationManager();
    }

    @Test
    void testAddObserver_ThenNotify_CallsObserverOnce() {
        manager.addObserver(mockObserver);

        manager.notifyAllObservers("user1", "Test message");

        verify(mockObserver, times(1)).notifyUser("user1", "Test message");
    }

    @Test
    void testNotifyAllObservers_CalledTwice() {
        manager.addObserver(mockObserver);

        manager.notifyAllObservers("user1", "Message 1");
        manager.notifyAllObservers("user1", "Message 2");

        verify(mockObserver, times(2)).notifyUser(anyString(), anyString());
    }

    @Test
    void testNotifyAllObservers_CorrectUsername() {
        manager.addObserver(mockObserver);

        manager.notifyAllObservers("user1", "Hello");

        verify(mockObserver).notifyUser(eq("user1"), eq("Hello"));
    }

    @Test
    void testNotifyWithoutObservers_DoesNothing() {
        manager.notifyAllObservers("user1", "Hello");

        verifyNoInteractions(mockObserver);
    }

    @Test
    void testAddSameObserverTwice_ShouldNotDuplicateNotification() {
        manager.addObserver(mockObserver);
        manager.addObserver(mockObserver);

        manager.notifyAllObservers("user1", "Hello");

        verify(mockObserver, times(1)).notifyUser("user1", "Hello");
    }
}