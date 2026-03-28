package com.hamdi.appointments.gui;

import com.hamdi.appointments.service.AppointmentService;
import com.hamdi.appointments.service.AuthService;
import com.hamdi.appointments.repository.AppointmentRepository;
import com.hamdi.appointments.domain.Appointment;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Login window for the Appointment Scheduling System.
 *
 * @author Hamdi
 * @version 1.0
 */
public class LoginGUI extends JFrame {

    private static final long serialVersionUID = 1L;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    private static AppointmentRepository sharedRepo;
    private static AppointmentService    sharedService;
    private static AuthService           sharedAuth;

    /** ✅ رسائل منتظرة لكل مستخدم */
    public static Map<String, List<String>> pendingMessages = new HashMap<>();

    /**
     * Adds a pending message for a user.
     *
     * @param username the target username
     * @param message  the message to deliver
     */
    public static void addPendingMessage(String username, String message) {
        pendingMessages.computeIfAbsent(username, k -> new ArrayList<>()).add(message);
    }

    /**
     * Returns and clears pending messages for a user.
     *
     * @param username the username
     * @return list of pending messages
     */
    public static List<String> popPendingMessages(String username) {
        List<String> msgs = pendingMessages.getOrDefault(username, new ArrayList<>());
        pendingMessages.remove(username);
        return msgs;
    }

    /**
     * Initializes shared services once, then builds the login UI.
     */
    public LoginGUI() {
        super("Login");

        if (sharedRepo == null) {
            sharedRepo = new AppointmentRepository();
            sharedRepo.addAppointment(new Appointment(LocalDateTime.of(2026, 6, 1, 10, 0), 30, 1));
            sharedRepo.addAppointment(new Appointment(LocalDateTime.of(2026, 6, 1, 11, 0), 60, 3));
            sharedRepo.addAppointment(new Appointment(LocalDateTime.of(2026, 6, 1, 12, 0), 45, 5));
            sharedRepo.addAppointment(new Appointment(LocalDateTime.of(2026, 6, 1, 13, 0), 30, 2));
            sharedRepo.addAppointment(new Appointment(LocalDateTime.of(2026, 6, 1, 14, 0), 60, 2));
            sharedRepo.addAppointment(new Appointment(LocalDateTime.of(2026, 6, 1, 15, 0), 90, 3));
            sharedRepo.addAppointment(new Appointment(LocalDateTime.of(2026, 6, 1, 16, 0), 45, 8));
            sharedService = new AppointmentService(sharedRepo);
        }

        if (sharedAuth == null) {
            sharedAuth = new AuthService();
            sharedAuth.addAdministrator("Abood", "1234");
            sharedAuth.addAdministrator("Hamdi", "1122");
            sharedAuth.addUser("user1", "1234");
            sharedAuth.addUser("user2", "1234");
        }

        // ✅ Fields
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        loginButton   = new JButton("Login");

        // ✅ GridBagLayout لتوسيط الزر
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        // Username Label
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        add(new JLabel("Username:"), gbc);

        // Username Field
        gbc.gridx = 1; gbc.gridy = 0;
        add(usernameField, gbc);

        // Password Label
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Password:"), gbc);

        // Password Field
        gbc.gridx = 1; gbc.gridy = 1;
        add(passwordField, gbc);

        // ✅ Login Button في النص
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill   = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(loginButton, gbc);

        setSize(400, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (sharedAuth.login(username, password)) {
                if (sharedAuth.isAdmin(username)) {
                    JOptionPane.showMessageDialog(this, "Welcome Admin, " + username + "! 👋");
                } else {
                    JOptionPane.showMessageDialog(this, "Welcome, " + username + "! 👋");
                }
                dispose();
                new AppointmentsGUI(sharedService, username, sharedAuth.isAdmin(username));
            } else {
                JOptionPane.showMessageDialog(this, "Login failed! Check credentials.");
            }
        });
    }

    /**
     * Application entry point.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        new LoginGUI();
    }
}