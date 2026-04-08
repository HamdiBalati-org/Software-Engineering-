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
    private JButton myBookingsButton;
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
        table.getTableHeader().setReorderingAllowed(false);
        refreshTable();
        JScrollPane scrollPane = new JScrollPane(table);

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

        if (!isAdmin) {
            bookButton       = new JButton("Book");
            cancelButton     = new JButton("Cancel My Booking");
            modifyButton     = new JButton("Modify My Booking");
            myBookingsButton = new JButton("My Bookings");

            topPanel.add(new JLabel("DateTime:"));
            topPanel.add(dateTimeField);
            topPanel.add(new JLabel("Duration:"));
            topPanel.add(durationField);
            topPanel.add(new JLabel("Type:"));
            topPanel.add(typeComboBox);
            topPanel.add(bookButton);
            topPanel.add(cancelButton);
            topPanel.add(myBookingsButton);

            bottomPanel.add(new JLabel("New DateTime:"));
            bottomPanel.add(newDateTimeField);
            bottomPanel.add(modifyButton);
        }

        if (isAdmin) {
            viewBookingsButton = new JButton("View All Bookings");
            topPanel.add(viewBookingsButton);
        }

        logoutButton = new JButton("Logout");
        bottomPanel.add(logoutButton);

        setLayout(new BorderLayout());

        if (isAdmin) {
            JPanel centerPanel = new JPanel(new BorderLayout());
            JLabel titleLabel  = new JLabel("Available Appointments", JLabel.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
            titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            centerPanel.add(titleLabel, BorderLayout.NORTH);
            centerPanel.add(scrollPane, BorderLayout.CENTER);
            add(centerPanel, BorderLayout.CENTER);
        } else {
            add(scrollPane, BorderLayout.CENTER);
        }

        add(topPanel,    BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.SOUTH);

        setSize(950, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        if (!isAdmin) {
            List<String> msgs = LoginGUI.popPendingMessages(currentUser);
            if (!msgs.isEmpty()) {
                StringBuilder sb = new StringBuilder("📢 Notifications:\n\n");
                for (String msg : msgs) {
                    sb.append("• ").append(msg).append("\n");
                }
                JOptionPane.showMessageDialog(this, sb.toString(),
                    "Notifications", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        if (!isAdmin) {

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

            cancelButton.addActionListener(e -> {
                String dt = dateTimeField.getText().trim();
                if (dt.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter a DateTime!");
                    return;
                }
                service.cancelAppointment(dt, currentUser);
                refreshTable();
            });

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

            myBookingsButton.addActionListener(e -> {
                StringBuilder sb = new StringBuilder("📅 Your Bookings:\n\n");
                boolean found = false;

                for (Appointment a : service.getAllAppointments()) {
                    if (a.isBookedByUser(currentUser)) {
                        found = true;
                        sb.append("• ").append(a.getDateTime())
                          .append(" | Duration: ").append(a.getDurationMinutes()).append(" min")
                          .append(" | Type: ").append(a.getTypeForUser(currentUser))
                          .append(" | Status: ").append(a.getStatusForUser(currentUser))
                          .append("\n");
                    }
                }

                if (!found) {
                    sb.append("You have no bookings yet.");
                }

                JOptionPane.showMessageDialog(this, sb.toString(),
                    "My Bookings", JOptionPane.INFORMATION_MESSAGE);
            });
        }

        if (isAdmin) {
            viewBookingsButton.addActionListener(e -> new BookingsGUI(service, currentUser));
        }

        logoutButton.addActionListener(e -> {
            dispose();
            new LoginGUI();
        });
    }

    /**
     * Refreshes the appointments table.
     * Admins see all appointments, users see appointments returned by the service.
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
                        a.getStatusForUser(currentUser),
                        a.getTypeForUser(currentUser) != null ? a.getTypeForUser(currentUser) : "-"
                });
            }
        }
    }
}