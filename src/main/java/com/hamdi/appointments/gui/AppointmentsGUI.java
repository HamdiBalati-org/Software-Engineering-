package com.hamdi.appointments.gui;

import com.hamdi.appointments.domain.Appointment;
import com.hamdi.appointments.domain.AppointmentType;
import com.hamdi.appointments.service.AppointmentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Main appointments window for booking, cancellation, and modification.
 *
 * @author Hamdi
 * @version 1.0
 */
public class AppointmentsGUI extends JFrame {

    private static final long serialVersionUID = 1L;

    private AppointmentService service;
    private String currentUser;
    private boolean isAdmin;

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField dateTimeField;
    private JTextField durationField;
    private JTextField newDateTimeField;
    private JComboBox<AppointmentType> typeComboBox;

    private JButton bookButton;
    private JButton cancelButton;
    private JButton modifyButton;
    private JButton viewBookingsButton;
    private JButton logoutButton;

    /**
     * Builds the appointments GUI for the logged-in user.
     *
     * @param service   the appointment service
     * @param username  the logged-in username
     * @param isAdmin   true if administrator
     */
    public AppointmentsGUI(AppointmentService service, String username, boolean isAdmin) {
        super("Appointments - " + username + (isAdmin ? " [ADMIN]" : ""));
        this.service     = service;
        this.currentUser = username;
        this.isAdmin     = isAdmin;

        if (isAdmin) {
            tableModel = new DefaultTableModel(
                new Object[]{"DateTime", "Duration", "Max", "Current", "Status", "Booked By"}, 0);
        } else {
            tableModel = new DefaultTableModel(
                new Object[]{"DateTime", "Duration", "Max", "Current", "Status", "Type"}, 0);
        }

        table = new JTable(tableModel);
        refreshTable();
        JScrollPane scrollPane = new JScrollPane(table);

        // ✅ لما اليوزر يضغط على سطر يتعبى تلقائياً
        if (!isAdmin) {
            table.getSelectionModel().addListSelectionListener(e -> {
                int row = table.getSelectedRow();
                if (row != -1) {
                    dateTimeField.setText(tableModel.getValueAt(row, 0).toString());
                    durationField.setText(tableModel.getValueAt(row, 1).toString());
                }
            });
        }

        JPanel topPanel    = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        dateTimeField    = new JTextField(10);
        newDateTimeField = new JTextField(10);
        durationField    = new JTextField(5);
        typeComboBox     = new JComboBox<>(AppointmentType.values());

        // ✅ واجهة المستخدم العادي
        if (!isAdmin) {
            bookButton   = new JButton("Book");
            cancelButton = new JButton("Cancel My Booking");
            modifyButton = new JButton("Modify My Booking");

            topPanel.add(new JLabel("DateTime:"));
            topPanel.add(dateTimeField);
            topPanel.add(new JLabel("Duration:"));
            topPanel.add(durationField);
            topPanel.add(new JLabel("Type:"));
            topPanel.add(typeComboBox);
            topPanel.add(bookButton);
            topPanel.add(cancelButton);

            bottomPanel.add(new JLabel("New DateTime:"));
            bottomPanel.add(newDateTimeField);
            bottomPanel.add(modifyButton);
        }

        // ✅ واجهة الأدمن
        if (isAdmin) {
            viewBookingsButton = new JButton("View All Bookings");
            topPanel.add(viewBookingsButton);
        }

        logoutButton = new JButton("Logout");
        bottomPanel.add(logoutButton);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(topPanel,    BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.SOUTH);

        setSize(950, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        // ==================== أحداث الأزرار ====================

        if (!isAdmin) {

            // ------------------ Book ------------------
            bookButton.addActionListener(e -> {
                String dt = dateTimeField.getText().trim();
                int dur;
                if (dt.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter a DateTime!");
                    return;
                }
                try {
                    dur = Integer.parseInt(durationField.getText().trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid duration!");
                    return;
                }
                AppointmentType type = (AppointmentType) typeComboBox.getSelectedItem();
                service.bookAppointment(dt, dur, currentUser, type);
                refreshTable();
            });

            // ------------------ Cancel My Booking ------------------
            cancelButton.addActionListener(e -> {
                String dt = dateTimeField.getText().trim();
                if (dt.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter a DateTime!");
                    return;
                }
                service.cancelAppointment(dt, currentUser);
                refreshTable();
            });

            // ------------------ Modify My Booking ------------------
            modifyButton.addActionListener(e -> {
                String oldDt = dateTimeField.getText().trim();
                String newDt = newDateTimeField.getText().trim();
                if (oldDt.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter the current DateTime!");
                    return;
                }
                if (newDt.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter the new DateTime!");
                    return;
                }
                service.modifyAppointment(oldDt, newDt, currentUser);
                refreshTable();
            });
        }

        if (isAdmin) {

            // ------------------ View All Bookings ------------------
            viewBookingsButton.addActionListener(e -> {
                new BookingsGUI(service, currentUser);
            });
        }

        // ------------------ Logout ------------------
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginGUI();
        });
    }

    /**
     * Refreshes the appointments table.
     * Admins see all appointments, users see available ones only.
     */
    private void refreshTable() {
        tableModel.setRowCount(0);

        List<Appointment> appointments = isAdmin
                ? service.getAllAppointments()
                : service.getAvailableAppointments();

        for (Appointment a : appointments) {
            if (isAdmin) {
                String bookedBy = a.getBookedUsers().isEmpty()
                        ? "-"
                        : String.join(", ", a.getBookedUsers());
                tableModel.addRow(new Object[]{
                        a.getDateTime(),
                        a.getDurationMinutes(),
                        a.getMaxParticipants(),
                        a.getCurrentParticipants(),
                        a.getStatus(),
                        bookedBy
                });
            } else {
                tableModel.addRow(new Object[]{
                        a.getDateTime(),
                        a.getDurationMinutes(),
                        a.getMaxParticipants(),
                        a.getCurrentParticipants(),
                        a.getStatus(),
                        a.getTypeForUser(currentUser) != null ? a.getTypeForUser(currentUser) : "-"
                });
            }
        }
    }
}