package com.hamdi.appointments.domain;

import java.time.LocalDateTime;

public class Appointment {
    private LocalDateTime dateTime;
    private int durationMinutes;
    private int maxParticipants;
    private int currentParticipants;

    public Appointment(LocalDateTime dateTime, int durationMinutes, int maxParticipants){
        this.dateTime = dateTime;
        this.durationMinutes = durationMinutes;
        this.maxParticipants = maxParticipants;
        this.currentParticipants = 0;
    }

    public LocalDateTime getDateTime(){ return dateTime; }
    public int getDurationMinutes(){ return durationMinutes; }
    public int getMaxParticipants(){ return maxParticipants; }
    public int getCurrentParticipants(){ return currentParticipants; }

    // زيادة عدد المشاركين
    public void incrementParticipants(){
        if(currentParticipants < maxParticipants){
            currentParticipants++;
        }
    }
}
