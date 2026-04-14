package com.rakib.reward;

public class UserModel {

    public String id;
    public String name;
    public String phone;
    public String points;

    public UserModel(String id, String name, String phone, String points) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.points = points;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getPoints() { return points; }
}