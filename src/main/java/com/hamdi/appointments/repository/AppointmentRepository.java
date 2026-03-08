package com.hamdi.appointments.repository;

import com.hamdi.appointments.domain.Appointment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentRepository {

    private List<Appointment> appointments = new ArrayList<>();

    public void addAppointment(Appointment a){
        appointments.add(a);
    }

    public Appointment findByDateTime(LocalDateTime dt){
        for(Appointment a : appointments){
            if(a.getDateTime().equals(dt)){
                return a;
            }
        }
        return null;
    }

    public List<Appointment> getAvailableAppointments(){
        List<Appointment> available = new ArrayList<>();
        for(Appointment a : appointments){
            if(a.getCurrentParticipants() < a.getMaxParticipants()){
                available.add(a);
            }
        }
        return available;
    }
}
