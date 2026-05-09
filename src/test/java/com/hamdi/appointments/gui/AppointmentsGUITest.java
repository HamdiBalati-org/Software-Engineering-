package com.hamdi.appointments.gui;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.hamdi.appointments.domain.AppointmentType;
import com.hamdi.appointments.repository.AppointmentRepository;
import com.hamdi.appointments.service.AppointmentService;

public class AppointmentsGUITest {

    private JFrame frame;

    @AfterEach
    void tearDown() throws Exception {
        if (frame != null) {
            SwingUtilities.invokeAndWait(() -> frame.dispose());
        }
    }

    @Test
    void shouldCreateUserGUIAndBeDisplayable() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            AppointmentService service = new AppointmentService(new AppointmentRepository());
            frame = new AppointmentsGUI(service, "user1", false);
        });

        assertNotNull(frame);
        assertTrue(frame.isDisplayable());
        assertEquals("Appointments - user1", frame.getTitle());
    }

    @Test
    void shouldCreateAdminGUIAndBeDisplayable() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            AppointmentService service = new AppointmentService(new AppointmentRepository());
            frame = new AppointmentsGUI(service, "admin", true);
        });

        assertNotNull(frame);
        assertTrue(frame.isDisplayable());
        assertEquals("Appointments - admin [ADMIN]", frame.getTitle());
    }

    @Test
    void userTableShouldDisplayAvailableAppointments() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            AppointmentService service = new AppointmentService(new AppointmentRepository());
            service.addAppointment("2026-05-10T10:00", 30, 3);

            frame = new AppointmentsGUI(service, "user1", false);

            JTable table = getPrivateField(frame, "table", JTable.class);
            DefaultTableModel model = (DefaultTableModel) table.getModel();

            assertEquals(1, model.getRowCount());
            assertEquals("2026-05-10T10:00", model.getValueAt(0, 0).toString());
            assertEquals(30, model.getValueAt(0, 1));
            assertEquals(3, model.getValueAt(0, 2));
        });
    }

    @Test
    void adminTableShouldDisplayAllAppointmentsWithBookedUsers() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            AppointmentService service = new AppointmentService(new AppointmentRepository());
            service.addAppointment("2026-05-11T12:00", 45, 5);
            service.bookAppointment("2026-05-11T12:00", 45, "user1", AppointmentType.INDIVIDUAL);

            frame = new AppointmentsGUI(service, "admin", true);

            JTable table = getPrivateField(frame, "table", JTable.class);
            DefaultTableModel model = (DefaultTableModel) table.getModel();

            assertEquals(1, model.getRowCount());
assertEquals("2026-05-11T12:00", model.getValueAt(0, 0).toString());            assertEquals(45, model.getValueAt(0, 1));
            assertEquals("-", model.getValueAt(0, 5));
        });
    }

    @Test
    void selectingUserTableRowShouldFillDateTimeAndDurationFields() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            AppointmentService service = new AppointmentService(new AppointmentRepository());
            service.addAppointment("2026-05-12T09:30", 60, 2);

            frame = new AppointmentsGUI(service, "user1", false);

            JTable table = getPrivateField(frame, "table", JTable.class);
            JTextField dateTimeField = getPrivateField(frame, "dateTimeField", JTextField.class);
            JTextField durationField = getPrivateField(frame, "durationField", JTextField.class);

            table.setRowSelectionInterval(0, 0);

            assertEquals("2026-05-12T09:30", dateTimeField.getText());
            assertEquals("60", durationField.getText());
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