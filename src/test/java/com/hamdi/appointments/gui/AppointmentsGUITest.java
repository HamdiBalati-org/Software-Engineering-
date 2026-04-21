package com.hamdi.appointments.gui;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.hamdi.appointments.gui.AppointmentsGUI;
import com.hamdi.appointments.service.AppointmentService;
import com.hamdi.appointments.repository.AppointmentRepository;

public class AppointmentsGUITest {

    private JFrame frame;

    @AfterEach
    void tearDown() throws Exception {
        if (frame != null) {
            SwingUtilities.invokeAndWait(() -> frame.dispose());
        }
    }

    @Test
    void shouldCreateUserGUI() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            AppointmentService service = new AppointmentService(new AppointmentRepository());
            frame = new AppointmentsGUI(service, "user1", false);
        });

        assertNotNull(frame);
        assertTrue(frame.isDisplayable());
    }

    
    
    @Test
    void shouldCreateAdminGUI() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            AppointmentService service = new AppointmentService(new AppointmentRepository());
            frame = new AppointmentsGUI(service, "admin", true);
        });

        assertNotNull(frame);
    }
    @Test
    void shouldCreateAdminGUIAndBeDisplayable() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            AppointmentService service = new AppointmentService(new AppointmentRepository());
            frame = new AppointmentsGUI(service, "admin", true);
        });

        assertNotNull(frame);
        assertTrue(frame.isDisplayable());
    }
    @Test
    void shouldCreateUserGUIAndBeDisplayable() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            AppointmentService service = new AppointmentService(new AppointmentRepository());
            frame = new AppointmentsGUI(service, "user1", false);
        });

        assertNotNull(frame);
        assertTrue(frame.isDisplayable());
    }
    
}