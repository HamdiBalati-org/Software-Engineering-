package com.hamdi.appointments.gui;

import com.hamdi.appointments.UandAandA.Data;
import com.hamdi.appointments.repository.AppointmentRepository;
import com.hamdi.appointments.service.AppointmentService;
import com.hamdi.appointments.service.AuthService;

import javax.swing.*;
import java.awt.*;
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
    private static AppointmentService sharedService;
    private static AuthService sharedAuth;

    public static Map<String, List<String>> pendingMessages = new HashMap<>();

    /**
     * Adds a pending message for a user.
     *
     * @param username the target username
     * @param message the message to deliver
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

        initializeSharedObjects();
        buildUI();
        registerActions();
    }

    /**
     * Initializes repository, services, and seed data once.
     */
    private void initializeSharedObjects() {
        if (sharedRepo == null) {
            sharedRepo = new AppointmentRepository();
        }

        if (sharedService == null) {
            sharedService = new AppointmentService(sharedRepo);
        }

        if (sharedAuth == null) {
            sharedAuth = new AuthService();
            Data.initialize(sharedRepo, sharedAuth);
        }
    }

    /**
     * Builds the login window UI.
     */
    private void buildUI() {
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        loginButton = new JButton("Login");

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(loginButton, gbc);

        setSize(400, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Registers button actions.
     */
    private void registerActions() {
        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
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