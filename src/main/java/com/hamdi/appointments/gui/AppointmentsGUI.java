package com.hamdi.appointments.gui;

import com.hamdi.appointments.domain.Appointment;
import com.hamdi.appointments.domain.AppointmentType;
import com.hamdi.appointments.service.AppointmentService;
import com.hamdi.appointments.service.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Main appointments window for booking, cancellation, and modification.
 *
 * @author Hamdi
 * @version 3.2
 */
public class AppointmentsGUI extends JFrame {

    private static final long serialVersionUID = 1L;


    private static final String BTN_ADD_APPOINTMENT = "Add Appointment";
    private final transient AppointmentService service;
    private final String currentUser;
    private final boolean isAdmin;

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
    private JButton addAppointmentButton;
    private JButton addUserButton;
    private JButton viewUsersButton;
    private JButton logoutButton;

    public AppointmentsGUI(AppointmentService service, String username, boolean isAdmin) {
        super("Appointments - " + username + (isAdmin ? " [ADMIN]" : ""));
        this.service = service;
        this.currentUser = username;
        this.isAdmin = isAdmin;

        buildUI();
        wireActions();
        refreshTable();

        setSize(980, 540);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        showPendingNotificationsForUser();
    }

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        buildTable();
        add(buildCenterPanel(), BorderLayout.CENTER);

        if (isAdmin) {
            add(buildAdminBottomPanel(), BorderLayout.SOUTH);
        } else {
            add(buildUserBottomPanel(), BorderLayout.SOUTH);
        }
    }

    private void buildTable() {
        if (isAdmin) {
            tableModel = new DefaultTableModel(
                    new Object[]{"DateTime", "Duration", "Max", "Current", "Status", "Booked By"}, 0);
        } else {
            tableModel = new DefaultTableModel(
                    new Object[]{"DateTime", "Duration", "Max", "Current", "Status", "Type"}, 0);
        }

        table = new JTable(tableModel);
        table.getTableHeader().setReorderingAllowed(false);
        table.setRowHeight(22);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        if (!isAdmin) {
            table.getSelectionModel().addListSelectionListener(e -> {
                int row = table.getSelectedRow();
                if (row != -1) {
                    dateTimeField.setText(tableModel.getValueAt(row, 0).toString());
                    durationField.setText(tableModel.getValueAt(row, 1).toString());
                }
            });
        }
    }

    private JPanel buildCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout(8, 8));

        JLabel titleLabel = new JLabel("Available Appointments", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(new EmptyBorder(0, 0, 8, 0));

        JScrollPane scrollPane = new JScrollPane(table);

        centerPanel.add(titleLabel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        return centerPanel;
    }

    private JPanel buildUserBottomPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(new EmptyBorder(8, 0, 0, 0));

        dateTimeField = new JTextField(12);
        durationField = new JTextField(6);
        newDateTimeField = new JTextField(12);
        typeComboBox = new JComboBox<>(AppointmentType.values());

        bookButton = new JButton("Book");
        cancelButton = new JButton("Cancel My Booking");
        modifyButton = new JButton("Modify My Booking");
        myBookingsButton = new JButton("My Bookings");
        logoutButton = new JButton("Logout");

        Dimension smallButton = new Dimension(110, 30);
        Dimension mediumButton = new Dimension(160, 30);
        Dimension logoutSize = new Dimension(100, 30);

        bookButton.setPreferredSize(smallButton);
        cancelButton.setPreferredSize(mediumButton);
        modifyButton.setPreferredSize(mediumButton);
        myBookingsButton.setPreferredSize(new Dimension(130, 30));
        logoutButton.setPreferredSize(logoutSize);

        JPanel bookingCard = new JPanel();
        bookingCard.setLayout(new BoxLayout(bookingCard, BoxLayout.Y_AXIS));
        bookingCard.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Book / Cancel / View",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12)
        ));

        JPanel bookingRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        JPanel bookingRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));

        bookingRow1.add(new JLabel("DateTime:"));
        bookingRow1.add(dateTimeField);
        bookingRow1.add(new JLabel("Duration:"));
        bookingRow1.add(durationField);
        bookingRow1.add(new JLabel("Type:"));
        bookingRow1.add(typeComboBox);
        bookingRow1.add(bookButton);

        bookingRow2.add(cancelButton);
        bookingRow2.add(myBookingsButton);

        bookingCard.add(bookingRow1);
        bookingCard.add(bookingRow2);

        JPanel modifyCard = new JPanel(new BorderLayout(10, 10));
        modifyCard.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Modify Booking",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12)
        ));

        JPanel modifyLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JPanel modifyRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        modifyLeft.add(new JLabel("New DateTime:"));
        modifyLeft.add(newDateTimeField);
        modifyLeft.add(modifyButton);

        modifyRight.add(logoutButton);

        modifyCard.add(modifyLeft, BorderLayout.WEST);
        modifyCard.add(modifyRight, BorderLayout.EAST);

        bottomPanel.add(bookingCard);
        bottomPanel.add(Box.createVerticalStrut(8));
        bottomPanel.add(modifyCard);

        return bottomPanel;
    }

    private JPanel buildAdminBottomPanel() {

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        viewBookingsButton = new JButton("View All Bookings");
        addAppointmentButton = new JButton(BTN_ADD_APPOINTMENT);
        addUserButton = new JButton("Add User");
        viewUsersButton = new JButton("View Users");
        logoutButton = new JButton("Logout");

        Dimension buttonSize = new Dimension(160, 35);

        addAppointmentButton.setPreferredSize(buttonSize);
        addUserButton.setPreferredSize(buttonSize);
        viewUsersButton.setPreferredSize(buttonSize);
        viewBookingsButton.setPreferredSize(buttonSize);
        logoutButton.setPreferredSize(buttonSize);

        Font btnFont = new Font("Arial", Font.BOLD, 13);
        addAppointmentButton.setFont(btnFont);
        addUserButton.setFont(btnFont);
        viewUsersButton.setFont(btnFont);
        viewBookingsButton.setFont(btnFont);
        logoutButton.setFont(btnFont);

        bottomPanel.add(addAppointmentButton);
        bottomPanel.add(addUserButton);
        bottomPanel.add(viewUsersButton);
        bottomPanel.add(viewBookingsButton);
        bottomPanel.add(logoutButton);

        return bottomPanel;
    }

    private void wireActions() {
        if (!isAdmin) {
            bookButton.addActionListener(e -> {
                String dt = dateTimeField.getText().trim();
                int dur;

                if (dt.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter a DateTime!");
                    return;
                }

                try {
                    dur = Integer.parseInt(durationField.getText().trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid duration!");
                    return;
                }

                AppointmentType type = (AppointmentType) typeComboBox.getSelectedItem();
                service.bookAppointment(dt, dur, currentUser, type);
                refreshTable();
            });

            cancelButton.addActionListener(e -> {
                String dt = dateTimeField.getText().trim();

                if (dt.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter a DateTime!");
                    return;
                }

                service.cancelAppointment(dt, currentUser);
                refreshTable();
            });

            modifyButton.addActionListener(e -> {
                String oldDt = dateTimeField.getText().trim();
                String newDt = newDateTimeField.getText().trim();

                if (oldDt.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter the current DateTime!");
                    return;
                }

                if (newDt.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter the new DateTime!");
                    return;
                }

                service.modifyAppointment(oldDt, newDt, currentUser);
                refreshTable();
            });

            myBookingsButton.addActionListener(e -> {
                StringBuilder sb = new StringBuilder("Your Bookings:\n\n");
                boolean found = false;

                for (Appointment a : service.getAllAppointments()) {
                    if (a.isBookedByUser(currentUser)) {
                        found = true;
                        sb.append("- ").append(a.getDateTime())
                                .append(" | Duration: ").append(a.getDurationMinutes()).append(" min")
                                .append(" | Type: ").append(a.getTypeForUser(currentUser))
                                .append(" | Status: ").append(a.getStatusForUser(currentUser))
                                .append("\n");
                    }
                }

                if (!found) {
                    sb.append("You have no bookings yet.");
                }

                JOptionPane.showMessageDialog(
                        this,
                        sb.toString(),
                        "My Bookings",
                        JOptionPane.INFORMATION_MESSAGE
                );
            });
        }

        if (isAdmin) {
            viewBookingsButton.addActionListener(e -> new BookingsGUI(service, currentUser));

            addAppointmentButton.addActionListener(e -> {
                String dateTime = JOptionPane.showInputDialog(
                        this,
                        "Enter date and time (yyyy-MM-ddTHH:mm):",
                        BTN_ADD_APPOINTMENT,
                        JOptionPane.PLAIN_MESSAGE
                );
                if (dateTime == null) return;

                String durationStr = JOptionPane.showInputDialog(
                        this,
                        "Enter duration (minutes):",
                        BTN_ADD_APPOINTMENT,
                        JOptionPane.PLAIN_MESSAGE
                );
                if (durationStr == null) return;

                String maxStr = JOptionPane.showInputDialog(
                        this,
                        "Enter max participants:",
                        BTN_ADD_APPOINTMENT,
                        JOptionPane.PLAIN_MESSAGE
                );
                if (maxStr == null) return;

                try {
                    int duration = Integer.parseInt(durationStr.trim());
                    int maxParticipants = Integer.parseInt(maxStr.trim());

                    service.addAppointment(dateTime.trim(), duration, maxParticipants);
                    refreshTable();

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Duration and max participants must be valid numbers.",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            });

            addUserButton.addActionListener(e -> {
                AuthService auth = LoginGUI.getSharedAuth();

                String username = JOptionPane.showInputDialog(
                        this,
                        "Enter new username:",
                        "Add User",
                        JOptionPane.PLAIN_MESSAGE
                );
                if (username == null) return;
                username = username.trim();

                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Username cannot be empty.");
                    return;
                }

                if (auth.usernameExists(username)) {
                    JOptionPane.showMessageDialog(this, "This username already exists.");
                    return;
                }

                String password = JOptionPane.showInputDialog(
                        this,
                        "Enter password:",
                        "Add User",
                        JOptionPane.PLAIN_MESSAGE
                );
                if (password == null) return;
                password = password.trim();

                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Password cannot be empty.");
                    return;
                }

                String[] options = {"User", "Admin"};
                int choice = JOptionPane.showOptionDialog(
                        this,
                        "Select account type:",
                        "Add User",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        options,
                        options[0]
                );

                if (choice == -1) return;

                if (choice == 0) {
                    auth.addUser(username, password);
                    JOptionPane.showMessageDialog(this, "User added successfully.");
                } else {
                    auth.addAdministrator(username, password);
                    JOptionPane.showMessageDialog(this, "Administrator added successfully.");
                }
            });

            viewUsersButton.addActionListener(e -> {
                AuthService auth = LoginGUI.getSharedAuth();

                Map<String, String> users = auth.getAllUsers();

                if (users.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No users found.");
                    return;
                }

                String[] usernames = users.keySet().toArray(new String[0]);

                String selectedUser = (String) JOptionPane.showInputDialog(
                        this,
                        "Select user to delete:",
                        "Users List",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        usernames,
                        usernames[0]
                );

                if (selectedUser == null) return;

                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete " + selectedUser + "?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    auth.removeUser(selectedUser);
                    JOptionPane.showMessageDialog(this, "User deleted successfully.");
                }
            });
        }

        logoutButton.addActionListener(e -> {
            dispose();
            new LoginGUI();
        });
    }

    private void showPendingNotificationsForUser() {
        if (!isAdmin) {
            List<String> msgs = LoginGUI.popPendingMessages(currentUser);
            if (!msgs.isEmpty()) {
                StringBuilder sb = new StringBuilder("Notifications:\n\n");
                for (String msg : msgs) {
                    sb.append("- ").append(msg).append("\n");
                }
                JOptionPane.showMessageDialog(
                        this,
                        sb.toString(),
                        "Notifications",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        }
    }

    /**
     * Refreshes the appointments table.
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