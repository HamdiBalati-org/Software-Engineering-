package com.hamdi.appointments.domain;

/**
 * Represents an administrator user
 */
/*
Administrator class is a domain class that represents an administrator user in the system.
*/



public class Administrator {
    private String username;
    private String password;

    public Administrator(String username, String password){
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
}
