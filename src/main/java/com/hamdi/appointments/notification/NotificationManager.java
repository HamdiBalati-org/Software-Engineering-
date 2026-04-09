package com.hamdi.appointments.notification;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages a list of observers and notifies them of appointment events.
 * Implements the Observer Pattern.
 *
 * @author Hamdi
 * @version 1.2
 */
public class NotificationManager {

    private List<Observer> observers = new ArrayList<>();

    /**
     * Registers a new observer if it is not already added.
     *
     * @param observer the observer to add
     */
    public void addObserver(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * Notifies all registered observers with a message.
     *
     * @param username the username to notify
     * @param message  the notification message
     */
    public void notifyAllObservers(String username, String message) {
        for (Observer observer : observers) {
            observer.notifyUser(username, message);
        }
    }
}