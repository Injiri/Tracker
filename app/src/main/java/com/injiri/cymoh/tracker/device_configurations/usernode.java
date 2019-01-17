package com.injiri.cymoh.tracker.device_configurations;

import java.util.ArrayList;

public class usernode {
    private String userMail;
    private String password;
    private ArrayList<Device> devices= new ArrayList<Device>();

    public usernode(String userMail, String password, ArrayList<Device> devices) {
        this.userMail = userMail;
        this.password = password;
        this.devices = devices;
    }

    public String getuserMail() {
        return userMail;
    }

    public void setuserMail(String userMail) {
        this.userMail = userMail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<Device> getDevices() {
        return devices;
    }

    public void setDevices(ArrayList<Device> devices) {
        this.devices = devices;
    }


}
