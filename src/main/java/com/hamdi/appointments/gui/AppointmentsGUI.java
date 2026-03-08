package com.hamdi.appointments.gui;

import com.hamdi.appointments.domain.Appointment;
import com.hamdi.appointments.service.AppointmentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AppointmentsGUI extends JFrame {

    private AppointmentService service;
    private String currentUser;

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField dateTimeField;
    private JTextField durationField;
    private JButton bookButton;
    private JButton logoutButton;

    public AppointmentsGUI(AppointmentService service, String username) {
        super("Appointments - " + username);
        this.service = service;
        this.currentUser = username;

        // ------------------ إعداد جدول المواعيد ------------------
        tableModel = new DefaultTableModel(new Object[]{"DateTime", "Duration", "Max", "Current"}, 0);
        table = new JTable(tableModel);
        refreshTable(); // تعريف refreshTable موجود أسفل الكلاس

        JScrollPane scrollPane = new JScrollPane(table);

        // ------------------ حقول الإدخال ------------------
        dateTimeField = new JTextField(10);
        durationField = new JTextField(5);
        bookButton = new JButton("Book Appointment");
        logoutButton = new JButton("Logout");

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("DateTime (YYYY-MM-DDTHH:MM):"));
        inputPanel.add(dateTimeField);
        inputPanel.add(new JLabel("Duration:"));
        inputPanel.add(durationField);
        inputPanel.add(bookButton);
        inputPanel.add(logoutButton);

        // ------------------ إعداد Frame ------------------
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        setSize(700,300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        // ------------------ أزرار ------------------
        bookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String dt = dateTimeField.getText();
                int dur;
                try {
                    dur = Integer.parseInt(durationField.getText());
                } catch(NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid duration!");
                    return;
                }

                service.bookAppointment(dt, dur, currentUser);

                // تحديث الجدول بعد الحجز
                refreshTable();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // اغلق نافذة المواعيد
                // هنا لازم يكون عندك LoginGUI جاهز
                new LoginGUI(); 
            }
        });
    }

    // ------------------ تحديث الجدول ------------------
    private void refreshTable() {
        tableModel.setRowCount(0); // مسح الجدول
        List<Appointment> appointments = service.getAvailableAppointments();
        for(Appointment a : appointments){
            tableModel.addRow(new Object[]{
                    a.getDateTime(),
                    a.getDurationMinutes(),
                    a.getMaxParticipants(),
                    a.getCurrentParticipants()
            });
        }
    }
}
