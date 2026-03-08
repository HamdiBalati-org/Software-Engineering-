package com.hamdi.appointments.gui;

import com.hamdi.appointments.service.AppointmentService;
import com.hamdi.appointments.service.AuthService;
import com.hamdi.appointments.repository.AppointmentRepository;
import com.hamdi.appointments.domain.Appointment;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;

public class LoginGUI extends JFrame {

    private AuthService auth;
    private AppointmentService service;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginGUI() {
        super("Login");

        // ------------------ إعداد Repository وService ------------------
        AppointmentRepository repo = new AppointmentRepository();

        // إضافة مواعيد افتراضية
        repo.addAppointment(new Appointment(LocalDateTime.of(2026,3,7,10,0), 30, 2));
        repo.addAppointment(new Appointment(LocalDateTime.of(2026,3,7,11,0), 60, 1));
        repo.addAppointment(new Appointment(LocalDateTime.of(2026,3,7,12,0), 45, 3));

        service = new AppointmentService(repo);

        // إعداد المستخدمين
        auth = new AuthService();
        auth.addAdministrator("admin", "1234");
        auth.addAdministrator("hamdi", "1122");

        // ------------------ إعداد واجهة المستخدم ------------------
        setLayout(new GridLayout(3,2));

        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        loginButton = new JButton("Login");
        add(loginButton);

        setSize(400,150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        // ------------------ زر تسجيل الدخول ------------------
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if(auth.login(username, password)){
                JOptionPane.showMessageDialog(this, username + " logged in successfully!");
                // افتح نافذة المواعيد
                new AppointmentsGUI(service, username);
                dispose(); // اغلق نافذة تسجيل الدخول
            } else {
                JOptionPane.showMessageDialog(this, "Login failed! Check credentials.");
            }
        });
    }

    public static void main(String[] args){
        new LoginGUI();
    }
}
