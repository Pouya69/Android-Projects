package com.pouyasalehi.dada;

import java.io.Serializable;

public class UserObject implements Serializable {

    private String  uid,
            name,
            email,notificationKey;


    private Boolean selected = false;

    public UserObject(String uid){
        this.uid = uid;
    }
    public UserObject(String uid, String name, String email){
        this.uid = uid;
        this.name = name;
        this.email=email;

    }

    public String getemail() {
        return email;
    }
    public String getName() {
        return name;
    }
    public String getuid() {
        return uid;
    }
    public String getnotificationKey() {
        return notificationKey;
    }
    public Boolean getSelected() {
        return selected;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setNotificationKey(String notificationKey) {
        this.notificationKey = notificationKey;
    }
    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
