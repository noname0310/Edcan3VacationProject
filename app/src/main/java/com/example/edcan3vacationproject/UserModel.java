package com.example.edcan3vacationproject;

public class UserModel {
    private String id, name, email, time;

    public UserModel(){}

    public UserModel(String id, String name, String email, String time) {
       this.id = id;
        this.name = name;
        this.email = email;
        this.time = time;
    }

    public String getId(){ return  id; }

    public void setId(String id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
