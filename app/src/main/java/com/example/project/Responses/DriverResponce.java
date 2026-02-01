package com.example.project.Responses;

public class DriverResponce {
    private int id;
    private String login;
    private String password;
    private  int status;
    private  String licenses;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLicenses(String licenses) {
        this.licenses = licenses;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getLicenses() {
        return licenses;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public int getStatus() {
        return status;
    }
}
