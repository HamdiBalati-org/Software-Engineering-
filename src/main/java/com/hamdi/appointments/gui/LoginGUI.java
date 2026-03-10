package com.hamdi.appointments.gui;

import com.hamdi.appointments.service.AppointmentService;
import com.hamdi.appointments.service.AuthService;
import com.hamdi.appointments.repository.AppointmentRepository;
import com.hamdi.appointments.domain.Appointment;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;

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

    /**
     * Initializes shared services once, then builds the login UI.
     */
    public LoginGUI() {
        super("Login");

        if (sharedRepo == null) {
            sharedRepo = new AppointmentRepository();
            // ✅ مواعيد بدون نوع - المستخدم يختار وقت الحجز
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
            sharedAuth.addAdministrator("admin", "1234");
            sharedAuth.addAdministrator("hamdi", "1122");
            sharedAuth.addUser("user1", "1234");
            sharedAuth.addUser("user2", "1234");
        }

        setLayout(new GridLayout(3, 2));

        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        loginButton = new JButton("Login");
        add(loginButton);

        setSize(400, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (sharedAuth.login(username, password)) {
                if (sharedAuth.isAdmin(username)) {
                    JOptionPane.showMessageDialog(this,
                            "Welcome Admin, " + username + "! 👋");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Welcome, " + username + "! 👋");
                }
                new AppointmentsGUI(sharedService, username, sharedAuth.isAdmin(username));
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Login failed! Check credentials.");
            }
        });
    }

    public static void main(String[] args) {
        new LoginGUI();
    }
}