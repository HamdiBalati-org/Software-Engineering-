package com.hamdi.appointments.gui;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.hamdi.appointments.domain.Appointment;
import com.hamdi.appointments.domain.AppointmentType;
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
                    new AppointmentService(new AppointmentRepository(), true);

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
                    new AppointmentService(new AppointmentRepository(), true);

            frame = new BookingsGUI(service, "admin");

            JLabel label =
                    getPrivateField(frame, "noBookingsLabel", JLabel.class);

            assertTrue(label.isVisible());
        });
    }

    @Test
    void bookingsTableShouldHaveCorrectColumns() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            AppointmentService service =
                    new AppointmentService(new AppointmentRepository(), true);

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

    @Test
    void shouldLoadBookedAppointmentIntoTable() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            AppointmentRepository repo = new AppointmentRepository();

            Appointment appointment = new Appointment(
                    LocalDateTime.now().plusDays(1),
                    30,
                    2
            );

            appointment.setTypeForUser("hamdi", AppointmentType.VIRTUAL);
            appointment.incrementParticipants("hamdi");

            repo.addAppointment(appointment);

            AppointmentService service =
                    new AppointmentService(repo, true);

            frame = new BookingsGUI(service, "admin");

            JTable table =
                    getPrivateField(frame, "table", JTable.class);

            DefaultTableModel model =
                    (DefaultTableModel) table.getModel();

            assertEquals(1, model.getRowCount());
            assertEquals("hamdi", model.getValueAt(0, 0));
            assertEquals(appointment.getDateTime(), model.getValueAt(0, 1));
            assertEquals(30, model.getValueAt(0, 2));
            assertEquals(AppointmentType.VIRTUAL, model.getValueAt(0, 3));
        });
    }

    @Test
    void selectingTableRowShouldFillUserAndDateTimeFields() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            AppointmentRepository repo = new AppointmentRepository();

            Appointment appointment = new Appointment(
                    LocalDateTime.now().plusDays(1),
                    45,
                    2
            );

            appointment.setTypeForUser("samer", AppointmentType.IN_PERSON);
            appointment.incrementParticipants("samer");

            repo.addAppointment(appointment);

            AppointmentService service =
                    new AppointmentService(repo, true);

            frame = new BookingsGUI(service, "admin");

            JTable table =
                    getPrivateField(frame, "table", JTable.class);

            JTextField userField =
                    getPrivateField(frame, "userField", JTextField.class);

            JTextField dateTimeField =
                    getPrivateField(frame, "dateTimeField", JTextField.class);

            table.setRowSelectionInterval(0, 0);

            assertEquals("samer", userField.getText());
            assertEquals(appointment.getDateTime().toString(), dateTimeField.getText());
        });
    }

    @Test
    void adminCancelButtonShouldCancelBookingAndRefreshTable() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            AppointmentRepository repo = new AppointmentRepository();

            Appointment appointment = new Appointment(
                    LocalDateTime.now().plusDays(1),
                    30,
                    2
            );

            appointment.setTypeForUser("hamdi", AppointmentType.VIRTUAL);
            appointment.incrementParticipants("hamdi");

            repo.addAppointment(appointment);

            AppointmentService service =
                    new AppointmentService(repo, true);

            frame = new BookingsGUI(service, "admin");

            JTextField userField =
                    getPrivateField(frame, "userField", JTextField.class);

            JTextField dateTimeField =
                    getPrivateField(frame, "dateTimeField", JTextField.class);

            JButton cancelButton =
                    getPrivateField(frame, "cancelButton", JButton.class);

            JTable table =
                    getPrivateField(frame, "table", JTable.class);

            userField.setText("hamdi");
            dateTimeField.setText(appointment.getDateTime().toString());

            cancelButton.doClick();

            assertFalse(appointment.isBookedByUser("hamdi"));
            assertEquals(0, table.getRowCount());
        });
    }

    @Test
    void adminModifyButtonShouldMoveBookingToNewAppointment() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            AppointmentRepository repo = new AppointmentRepository();

            Appointment oldAppointment = new Appointment(
                    LocalDateTime.now().plusDays(1),
                    30,
                    2
            );

            Appointment newAppointment = new Appointment(
                    LocalDateTime.now().plusDays(2),
                    30,
                    2
            );

            oldAppointment.setTypeForUser("hamdi", AppointmentType.VIRTUAL);
            oldAppointment.incrementParticipants("hamdi");

            repo.addAppointment(oldAppointment);
            repo.addAppointment(newAppointment);

            AppointmentService service =
                    new AppointmentService(repo, true);

            frame = new BookingsGUI(service, "admin");

            JTextField userField =
                    getPrivateField(frame, "userField", JTextField.class);

            JTextField dateTimeField =
                    getPrivateField(frame, "dateTimeField", JTextField.class);

            JTextField newDateTimeField =
                    getPrivateField(frame, "newDateTimeField", JTextField.class);

            JButton modifyButton =
                    getPrivateField(frame, "modifyButton", JButton.class);

            userField.setText("hamdi");
            dateTimeField.setText(oldAppointment.getDateTime().toString());
            newDateTimeField.setText(newAppointment.getDateTime().toString());

            modifyButton.doClick();

            assertFalse(oldAppointment.isBookedByUser("hamdi"));
            assertTrue(newAppointment.isBookedByUser("hamdi"));
            assertEquals(AppointmentType.VIRTUAL, newAppointment.getTypeForUser("hamdi"));
        });
    }

    @Test
    void backButtonShouldCloseBookingsWindow() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            AppointmentService service =
                    new AppointmentService(new AppointmentRepository(), true);

            frame = new BookingsGUI(service, "admin");

            JButton backButton =
                    getPrivateField(frame, "backButton", JButton.class);

            backButton.doClick();

            assertFalse(frame.isDisplayable());
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