package com.hamdi.appointments.gui;

/**
 * Validates booking input fields for GUI forms.
 */
public class BookingInputValidator {

    public static String validateBookInput(String dateTime, String duration) {
        if (dateTime == null || dateTime.trim().isEmpty()) {
            return "Please enter a DateTime!";
        }
        try {
            Integer.parseInt(duration.trim());
        } catch (NumberFormatException e) {
            return "Invalid duration!";
        }
        return null;
    }

    public static String validateCancelInput(String dateTime) {
        if (dateTime == null || dateTime.trim().isEmpty()) {
            return "Please enter a DateTime!";
        }
        return null;
    }

    public static String validateModifyInput(String oldDateTime, String newDateTime) {
        if (oldDateTime == null || oldDateTime.trim().isEmpty()) {
            return "Please enter the current DateTime!";
        }
        if (newDateTime == null || newDateTime.trim().isEmpty()) {
            return "Please enter the new DateTime!";
        }
        return null;
    }

    public static String validateAdminInput(String user, String dateTime) {
        if (user == null || user.trim().isEmpty()) {
            return "Please enter a Target User!";
        }
        if (dateTime == null || dateTime.trim().isEmpty()) {
            return "Please enter a DateTime!";
        }
        return null;
    }
}