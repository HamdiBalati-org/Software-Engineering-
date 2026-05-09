package com.hamdi.appointments.gui;

import com.hamdi.appointments.domain.Appointment;
import com.hamdi.appointments.domain.AppointmentType;
import com.hamdi.appointments.service.AppointmentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BookingsGUI extends JFrame {

    private static final long serialVersionUID = 1L;

    private AppointmentService service;
    private String adminName;

    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel noBookingsLabel;

    private JTextField userField;
    private JTextField dateTimeField;
    private JTextField newDateTimeField;

    private JButton cancelButton;
    private JButton modifyButton;
    private JButton backButton;

    public BookingsGUI(AppointmentService service, String adminName) {
        super("All Bookings - Admin: " + adminName);
        this.service = service;
        this.adminName = adminName;

        tableModel = new DefaultTableModel(
                new Object[]{"User", "DateTime", "Duration", "Type", "Status"}, 0);

        table = new JTable(tableModel);
        table.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(table);

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                userField.setText(tableModel.getValueAt(row, 0).toString());
                dateTimeField.setText(tableModel.getValueAt(row, 1).toString());
            }
        });

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        userField = new JTextField(8);
        dateTimeField = new JTextField(12);
        newDateTimeField = new JTextField(12);
        cancelButton = new JButton("Admin Cancel");
        modifyButton = new JButton("Admin Modify");
        backButton = new JButton("Back");

        topPanel.add(new JLabel("Target User:"));
        topPanel.add(userField);
        topPanel.add(new JLabel("DateTime:"));
        topPanel.add(dateTimeField);
        topPanel.add(cancelButton);

        bottomPanel.add(new JLabel("New DateTime:"));
        bottomPanel.add(newDateTimeField);
        bottomPanel.add(modifyButton);
        bottomPanel.add(backButton);

        noBookingsLabel = new JLabel("No bookings available for any user.", JLabel.CENTER);
        noBookingsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        noBookingsLabel.setForeground(Color.GRAY);
        noBookingsLabel.setVisible(false);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(noBookingsLabel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(centerPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.SOUTH);

        setSize(750, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        refreshTable();
        setVisible(true);

        cancelButton.addActionListener(e -> {
            String user = userField.getText().trim();
            String dt = dateTimeField.getText().trim();

            if (user.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter a Target User!");
                return;
            }

            if (dt.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter a DateTime!");
                return;
            }

            service.adminCancelAppointment(dt, user, adminName);
            refreshTable();
        });

        modifyButton.addActionListener(e -> {
            String user = userField.getText().trim();
            String oldDt = dateTimeField.getText().trim();
            String newDt = newDateTimeField.getText().trim();

            if (user.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter a Target User!");
                return;
            }

            if (oldDt.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter the current DateTime!");
                return;
            }

            if (newDt.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter the new DateTime!");
                return;
            }

            service.adminModifyAppointment(oldDt, newDt, user, adminName);
            refreshTable();
        });

        backButton.addActionListener(e -> dispose());
    }

    private void refreshTable() {
        tableModel.setRowCount(0);

        List<Appointment> appointments = service.getAllAppointments();
        boolean hasBookings = false;

        for (Appointment a : appointments) {
            if (!a.getBookedUsers().isEmpty()) {
                hasBookings = true;
                for (String user : a.getBookedUsers()) {
                    AppointmentType type = a.getTypeForUser(user);
                    tableModel.addRow(new Object[]{
                            user,
                            a.getDateTime(),
                            a.getDurationMinutes(),
                            type != null ? type : "-",
                            a.getStatus()
                    });
                }
            }
        }

        noBookingsLabel.setVisible(!hasBookings);
    }
}