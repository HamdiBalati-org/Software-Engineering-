
package com.hamdi.appointments.gui;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.hamdi.appointments.gui.BookingsGUI;
import com.hamdi.appointments.service.AppointmentService;
import com.hamdi.appointments.repository.AppointmentRepository;

public class BookingsGUITest {

    private JFrame frame;

    @AfterEach
    void tearDown() throws Exception {
        if (frame != null) {
            SwingUtilities.invokeAndWait(() -> frame.dispose());
        }
    }

    @Test
    void shouldCreateBookingsGUI() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            AppointmentService service = new AppointmentService(new AppointmentRepository());
            frame = new BookingsGUI(service, "admin");
        });

        assertNotNull(frame);
    }
}