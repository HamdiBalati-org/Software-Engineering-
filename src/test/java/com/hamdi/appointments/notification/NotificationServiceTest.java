package com.hamdi.appointments.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    
    
    
    @Test
    void testNotifyUser_AddsMessageToList() {
        EmailSender mockSender = mock(EmailSender.class);
        NotificationService service = new NotificationService(mockSender);

        service.notifyUser("user1", "Hello");

        assertEquals(1, service.getSentMessages().size());
    }
    
    
    @Test
    void testClearMessages_EmptiesList() {
        EmailSender mockSender = mock(EmailSender.class);
        NotificationService service = new NotificationService(mockSender);

        service.notifyUser("user1", "Msg1");
        service.notifyUser("user1", "Msg2");

        service.clearMessages();

        assertTrue(service.getSentMessages().isEmpty());
    }
    @Test
    void testGetSentMessages_NotNull() {
        EmailSender mockSender = mock(EmailSender.class);
        NotificationService service = new NotificationService(mockSender);

        assertNotNull(service.getSentMessages());
    }
    
    
    @Test
    void testNotifyUser_SendsEmail1() {
        EmailSender mockSender = mock(EmailSender.class);
        NotificationService service = new NotificationService(mockSender);

        service.notifyUser("user1", "Test");

        verify(mockSender, timeout(1000).times(1))
                .sendEmail(anyString(), anyString(), anyString());
    }
    
    
    @Test
    void testClearMessages() {
        EmailSender mockSender = mock(EmailSender.class);
        NotificationService service = new NotificationService(mockSender);

        service.notifyUser("user1", "Hello");
        service.clearMessages();

        assertTrue(service.getSentMessages().isEmpty());
    }
    
    
    
    @Test
    void testNotifyUser_SendsEmail() {
        EmailSender mockSender = mock(EmailSender.class);
        NotificationService service = new NotificationService(mockSender);

        service.notifyUser("user1", "Hello");

        verify(mockSender, timeout(1000).times(1))
            .sendEmail(anyString(), anyString(), anyString());
    }
    
    
    @Test
    void testNotificationService_StoresSentMessage() {
        EmailSender mockSender = mock(EmailSender.class);
        NotificationService service = new NotificationService(mockSender);

        service.notifyUser("user1", "Stored message");

        assertEquals(1, service.getSentMessages().size());
        assertEquals("Stored message", service.getSentMessages().get(0));
    }
    
    
    
    
    
    @Test
    void testNotificationService_ClearMessages() {
        EmailSender mockSender = mock(EmailSender.class);
        NotificationService service = new NotificationService(mockSender);

        service.notifyUser("user1", "Msg1");
        service.notifyUser("user1", "Msg2");
        service.clearMessages();

        assertTrue(service.getSentMessages().isEmpty());
    }
    
    @Test
    void testNotificationService_SendsEmail() {
        EmailSender mockSender = mock(EmailSender.class);
        NotificationService service = new NotificationService(mockSender);

        service.notifyUser("user1", "Email message");

        verify(mockSender, timeout(1000).times(1))
                .sendEmail(anyString(), anyString(), anyString());
    }
    
    
    
    
    
    
}