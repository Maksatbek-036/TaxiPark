package com.example.taxiclient.API;

public class RegisterRequest {
    public RegisterRequest(String login, String name) {
        this.login = login;
        this.name = name;
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


