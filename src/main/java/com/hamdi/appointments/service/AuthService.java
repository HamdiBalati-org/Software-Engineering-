package com.hamdi.appointments.service;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages authentication for administrators and users.
 *
 * @author Hamdi
 * @version 1.0
 */
public class AuthService {

    private Map<String, String> admins = new HashMap<>();
    private Map<String, String> users  = new HashMap<>();

    /**
     * Registers a new administrator.
     *
     * @param username the admin username
     * @param password the admin password
     */
    public void addAdministrator(String username, String password) {
        admins.put(username, password);
    }

    /**
     * Registers a new user.
     *
     * @param username the user username
     * @param password the user password
     */
    public void addUser(String username, String password) {
        users.put(username, password);
    }

    /**
     * Authenticates a user or administrator.
     *
     * @param username the username
     * @param password the password
     * @return true if credentials are valid
     */
    public boolean login(String username, String password) {
        return isAdmin(username) && admins.get(username).equals(password)
            || isUser(username)  && users.get(username).equals(password);
    }

    /**
     * Checks if the given username belongs to an administrator.
     *
     * @param username the username to check
     * @return true if administrator
     */
    public boolean isAdmin(String username) {
        return admins.containsKey(username);
    }

    /**
     * Checks if the given username belongs to a user.
     *
     * @param username the username to check
     * @return true if user
     */
    public boolean isUser(String username) {
        return users.containsKey(username);
    }
}