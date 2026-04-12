package com.hamdi.appointments.gui;

import com.hamdi.appointments.UandAandA.Data;
import com.hamdi.appointments.repository.AppointmentRepository;
import com.hamdi.appointments.service.AppointmentService;
import com.hamdi.appointments.service.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Login window for the Appointment Scheduling System.
 *
 * @author Hamdi
 * @version 2.0
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
        super("Appointment Scheduling System - Login");

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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 320);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Top title section
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Appointment Scheduling System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subTitleLabel = new JLabel("Please log in to continue");
        subTitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subTitleLabel.setForeground(Color.DARK_GRAY);
        subTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(8));
        titlePanel.add(subTitleLabel);

        // Center form section
        JPanel formWrapper = new JPanel(new GridBagLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Login Details"),
                new EmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        usernameField = new JTextField(18);
        passwordField = new JPasswordField(18);
        loginButton = new JButton("Login");

        loginButton.setPreferredSize(new Dimension(120, 32));
        loginButton.setFont(new Font("Arial", Font.BOLD, 13));

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");

        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1;
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(loginButton, gbc);

        formWrapper.add(formPanel);

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(formWrapper, BorderLayout.CENTER);

        setContentPane(mainPanel);
        setVisible(true);
    }

    /**
     * Registers button actions.
     */
    private void registerActions() {
        loginButton.addActionListener(e -> performLogin());

        passwordField.addActionListener(e -> performLogin());
        usernameField.addActionListener(e -> performLogin());
    }

    /**
     * Handles login logic.
     */
    private void performLogin() {
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
            JOptionPane.showMessageDialog(this, "Login failed! Check user name or password.");
        }
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