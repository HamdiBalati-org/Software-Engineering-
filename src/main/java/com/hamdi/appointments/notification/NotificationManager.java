package com.hamdi.appointments.notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationManager {

    private List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer observer){
        observers.add(observer);
    }

    public void notifyAllObservers(String username, String message){
        for(Observer obs : observers){
            obs.notifyUser(username, message);
        }
    }
}
