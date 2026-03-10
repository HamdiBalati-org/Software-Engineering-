package com.hamdi.appointments.gui;

import com.hamdi.appointments.domain.Appointment;
import com.hamdi.appointments.domain.AppointmentType;
import com.hamdi.appointments.service.AppointmentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Shows all bookings for admin management.
 *
 * @author Hamdi
 * @version 1.0
 */
public class BookingsGUI extends JFrame {

    private static final long serialVersionUID = 1L;

    private AppointmentService service;
    private String adminName;

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField userField;
    private JTextField dateTimeField;
    private JTextField newDateTimeField;

    private JButton cancelButton;
    private JButton modifyButton;
    private JButton backButton;

    /**
     * Builds the bookings management window for admin.
     *
     * @param service   the appointment service
     * @param adminName the logged-in admin username
     */
    public BookingsGUI(AppointmentService service, String adminName) {
        super("All Bookings - Admin: " + adminName);
        this.service   = service;
        this.adminName = adminName;

        tableModel = new DefaultTableModel(
            new Object[]{"User", "DateTime", "Duration", "Type", "Status"}, 0);

        table = new JTable(tableModel);
        refreshTable();
        JScrollPane scrollPane = new JScrollPane(table);

        // ✅ لما الأدمن يضغط على سطر يتعبى تلقائياً
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                userField.setText(tableModel.getValueAt(row, 0).toString());
                dateTimeField.setText(tableModel.getValueAt(row, 1).toString());
            }
        });

        JPanel topPanel    = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        userField        = new JTextField(8);
        dateTimeField    = new JTextField(12);
        newDateTimeField = new JTextField(12);
        cancelButton     = new JButton("Admin Cancel");
        modifyButton     = new JButton("Admin Modify");
        backButton       = new JButton("Back");

        // ✅ سطر أول - Cancel
        topPanel.add(new JLabel("Target User:"));
        topPanel.add(userField);
        topPanel.add(new JLabel("DateTime:"));
        topPanel.add(dateTimeField);
        topPanel.add(cancelButton);

        // ✅ سطر ثاني - Modify + Back
        bottomPanel.add(new JLabel("New DateTime:"));
        bottomPanel.add(newDateTimeField);
        bottomPanel.add(modifyButton);
        bottomPanel.add(backButton);

        setLayout(new BorderLayout());
        add(scrollPane,  BorderLayout.CENTER);
        add(topPanel,    BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.SOUTH);

        setSize(750, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        // ------------------ Admin Cancel ------------------
        cancelButton.addActionListener(e -> {
            String user = userField.getText().trim();
            String dt   = dateTimeField.getText().trim();
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

        // ------------------ Admin Modify ------------------
        modifyButton.addActionListener(e -> {
            String user  = userField.getText().trim();
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

        // ------------------ Back ------------------
        backButton.addActionListener(e -> dispose());
    }

    /**
     * Refreshes the bookings table showing all users and their bookings.
     */
    private void refreshTable() {
        tableModel.setRowCount(0);

        List<Appointment> appointments = service.getAllAppointments();

        for (Appointment a : appointments) {
            if (!a.getBookedUsers().isEmpty()) {
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
    }
}