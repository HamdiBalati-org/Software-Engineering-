package com.hamdi.appointments.gui;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.hamdi.appointments.repository.AppointmentRepository;
import com.hamdi.appointments.service.AppointmentService;

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
            AppointmentService service =
                    new AppointmentService(new AppointmentRepository());

            frame = new BookingsGUI(service, "admin");
        });

        assertNotNull(frame);
        assertTrue(frame.isDisplayable());
        assertEquals("All Bookings - Admin: admin", frame.getTitle());
    }

    @Test
    void shouldShowNoBookingsLabelWhenEmpty() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            AppointmentService service =
                    new AppointmentService(new AppointmentRepository());

            frame = new BookingsGUI(service, "admin");

            JLabel label =
                    getPrivateField(frame, "noBookingsLabel", JLabel.class);

            assertTrue(label.isVisible());
        });
    }

    @SuppressWarnings("unchecked")
    private static <T> T getPrivateField(Object object, String fieldName, Class<T> type) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}