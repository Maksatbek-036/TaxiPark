package com.example.project.API;

public class DriverEditRequest {
    private int id;
    private String name;
    private String phone;
    private String login;

    public DriverEditRequest(int id, String name, String phone, String login) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.login = login;
    }

    // Геттеры
    public int getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getLogin() { return login; }
}