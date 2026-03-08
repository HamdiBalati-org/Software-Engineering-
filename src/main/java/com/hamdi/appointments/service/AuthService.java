package com.hamdi.appointments.service;

import java.util.HashMap;
import java.util.Map;

public class AuthService {

    private Map<String,String> admins = new HashMap<>();

    public void addAdministrator(String username, String password){
        admins.put(username, password);
    }

    public boolean login(String username, String password){
        return admins.containsKey(username) && admins.get(username).equals(password);
    }
}
