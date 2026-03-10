package com.hamdi.appointments.notification;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages a list of observers and notifies them of appointment events.
 * Implements the Observer Pattern.
 *
 * @author Hamdi
 * @version 1.0
 */
public class NotificationManager {

    private List<Observer> observers = new ArrayList<>();

    /**
     * Registers a new observer.
     *
     * @param observer the observer to add
     */
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    /**
     * Notifies all registered observers with a message.
     *
     * @param username the username to notify
     * @param message  the notification message
     */
    public void notifyAllObservers(String username, String message) {
        for (Observer obs : observers) {
            obs.notifyUser(username, message);
        }
    }
}