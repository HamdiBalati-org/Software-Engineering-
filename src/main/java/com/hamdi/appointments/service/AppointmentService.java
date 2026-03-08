package com.hamdi.appointments.service;

import com.hamdi.appointments.domain.Appointment;
import com.hamdi.appointments.repository.AppointmentRepository;
import com.hamdi.appointments.notification.NotificationManager;
import com.hamdi.appointments.notification.NotificationService;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * منسق عمليات المواعيد وحجزها مع دعم إشعارات Mock
 */
public class AppointmentService {

    private AppointmentRepository repo;
    private NotificationManager notificationManager;

    public AppointmentService(AppointmentRepository repo){
        this.repo = repo;
        this.notificationManager = new NotificationManager();
        // أضف قناة تنبيه واحدة (Mock)
        this.notificationManager.addObserver(new NotificationService());
    }

    public List<Appointment> getAvailableAppointments(){
        return repo.getAvailableAppointments();
    }

    // ------------------ حجز موعد مع اسم المستخدم ------------------
    public void bookAppointment(String dateTime, int duration, String username){

        LocalDateTime dt = LocalDateTime.parse(dateTime);

        Appointment appointment = repo.findByDateTime(dt);

        if(appointment == null){
            JOptionPane.showMessageDialog(null, "Appointment not found.");
            return;
        }

        if(duration != appointment.getDurationMinutes()){
            JOptionPane.showMessageDialog(null, "Invalid duration! Must be " + appointment.getDurationMinutes() + " mins.");
            return;
        }

        if(appointment.getCurrentParticipants() >= appointment.getMaxParticipants()){
            JOptionPane.showMessageDialog(null, "Appointment is fully booked.");
            return;
        }

        appointment.incrementParticipants();

        JOptionPane.showMessageDialog(null,
                "Appointment booked successfully for " + dateTime +
                ". Current participants: " + appointment.getCurrentParticipants());

        // إرسال إشعار عبر Mock Notification
        notificationManager.notifyAllObservers(username,
                "Your appointment at " + dateTime + " is confirmed.");
    }
}
