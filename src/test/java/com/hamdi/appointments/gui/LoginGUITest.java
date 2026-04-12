
package com.hamdi.appointments.gui;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.hamdi.appointments.gui.LoginGUI;

public class LoginGUITest {

    private JFrame frame;

    @AfterEach
    void tearDown() throws Exception {
        if (frame != null) {
            SwingUtilities.invokeAndWait(() -> frame.dispose());
        }
    }

    @Test
    void shouldCreateLoginGUI() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            frame = new LoginGUI();
        });

        assertNotNull(frame);
        assertTrue(frame.isDisplayable());
    }

    @Test
    void shouldHandlePendingMessages() {
        LoginGUI.addPendingMessage("user1", "Test Message");

        var msgs = LoginGUI.popPendingMessages("user1");

        assertEquals(1, msgs.size());
    }
    
    
}