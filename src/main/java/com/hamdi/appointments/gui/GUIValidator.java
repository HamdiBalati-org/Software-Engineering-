package com.hamdi.appointments.gui;

/**
 * Validates GUI input fields.
 */
public class GUIValidator {

    public static boolean isValidDateTime(String dt) {
        return dt != null && !dt.trim().isEmpty();
    }

    public static boolean isValidDuration(String duration) {
        if (duration == null || duration.trim().isEmpty()) return false;
        try {
            Integer.parseInt(duration.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidUser(String user) {
        return user != null && !user.trim().isEmpty();
    }
}