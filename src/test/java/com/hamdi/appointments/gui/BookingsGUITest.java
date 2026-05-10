package com.hamdi.appointments.gui;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;

import javax.swing.JButton;
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

    @Test
void backButtonShouldCloseBookingsWindow() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
        AppointmentService service =
                new AppointmentService(new AppointmentRepository());

        frame = new BookingsGUI(service, "admin");

        JButton backButton =
                getPrivateField(frame, "backButton", JButton.class);

        backButton.doClick();

        assertFalse(frame.isDisplayable());
    });
}

@Test
void bookingsTableShouldHaveCorrectColumns() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
        AppointmentService service =
                new AppointmentService(new AppointmentRepository());

        frame = new BookingsGUI(service, "admin");

        JTable table =
                getPrivateField(frame, "table", JTable.class);

        DefaultTableModel model =
                (DefaultTableModel) table.getModel();

        assertEquals("User", model.getColumnName(0));
        assertEquals("DateTime", model.getColumnName(1));
        assertEquals("Duration", model.getColumnName(2));
        assertEquals("Type", model.getColumnName(3));
        assertEquals("Status", model.getColumnName(4));
    });
}
}