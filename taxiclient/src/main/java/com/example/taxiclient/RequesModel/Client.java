package com.example.taxiclient.RequesModel;

public class Client {
    public Client(String login, String password) {
        this.login = login;
        this.password = password;
    }

    private int id;
    private String name;
    private  String login;
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
